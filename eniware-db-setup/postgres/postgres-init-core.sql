CREATE SCHEMA eniwarenet;

CREATE SEQUENCE eniwarenet.eniwarenet_seq;
CREATE SEQUENCE eniwarenet.Edge_seq;

/* =========================================================================
   =========================================================================
   LOCATION
   =========================================================================
   ========================================================================= */

CREATE TABLE eniwarenet.sn_loc (
	id				BIGINT NOT NULL DEFAULT nextval('eniwarenet.eniwarenet_seq'),
	created			TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	country			CHARACTER VARYING(2) NOT NULL,
	time_zone		CHARACTER VARYING(64) NOT NULL,
	region			CHARACTER VARYING(128),
	state_prov		CHARACTER VARYING(128),
	locality		CHARACTER VARYING(128),
	postal_code		CHARACTER VARYING(32),
	address			CHARACTER VARYING(256),
	latitude		NUMERIC(9,6),
	longitude		NUMERIC(9,6),
	elevation		NUMERIC(8,3),
	fts_default 	tsvector,
	PRIMARY KEY (id)
);

CREATE INDEX sn_loc_fts_default_idx ON eniwarenet.sn_loc USING gin(fts_default);

CREATE TRIGGER maintain_fts
  BEFORE INSERT OR UPDATE ON eniwarenet.sn_loc
  FOR EACH ROW EXECUTE PROCEDURE
  tsvector_update_trigger(fts_default, 'pg_catalog.english',
  	country, region, state_prov, locality, postal_code, address);

/* =========================================================================
   =========================================================================
   WEATHER / LOCATION
   =========================================================================
   ========================================================================= */

CREATE SEQUENCE eniwarenet.weather_seq;

CREATE TABLE eniwarenet.sn_weather_source (
	id				BIGINT NOT NULL DEFAULT nextval('eniwarenet.eniwarenet_seq'),
	created			TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	sname			CHARACTER VARYING(128) NOT NULL,
	fts_default		tsvector,
	PRIMARY KEY(id)
);

CREATE TRIGGER maintain_fts
  BEFORE INSERT OR UPDATE ON eniwarenet.sn_weather_source
  FOR EACH ROW EXECUTE PROCEDURE
  tsvector_update_trigger(fts_default, 'pg_catalog.english', sname);

CREATE INDEX sn_weather_source_fts_default_idx ON eniwarenet.sn_weather_source USING gin(fts_default);

/* --- sn_weather_loc */

CREATE TABLE eniwarenet.sn_weather_loc (
	id				BIGINT NOT NULL DEFAULT nextval('eniwarenet.eniwarenet_seq'),
	created			TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	loc_id			BIGINT NOT NULL,
	source_id		BIGINT NOT NULL,
	source_data		CHARACTER VARYING(128),
	fts_default		tsvector,
	PRIMARY KEY (id),
	CONSTRAINT sn_weather_location_sn_loc_fk FOREIGN KEY (loc_id)
		REFERENCES eniwarenet.sn_loc (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT sn_weather_location_sn_weather_source_fk FOREIGN KEY (source_id)
		REFERENCES eniwarenet.sn_weather_source (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TRIGGER maintain_fts
  BEFORE INSERT OR UPDATE ON eniwarenet.sn_weather_loc
  FOR EACH ROW EXECUTE PROCEDURE
  tsvector_update_trigger(fts_default, 'pg_catalog.english', source_data);

CREATE INDEX sn_weather_loc_fts_default_idx ON eniwarenet.sn_weather_loc USING gin(fts_default);

/* =========================================================================
   =========================================================================
   Edge
   =========================================================================
   ========================================================================= */

CREATE TABLE eniwarenet.sn_Edge (
	Edge_id			BIGINT NOT NULL DEFAULT nextval('eniwarenet.Edge_seq'),
	created			TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	loc_id			BIGINT NOT NULL,
	wloc_id			BIGINT,
	Edge_name		CHARACTER VARYING(128),
	PRIMARY KEY (Edge_id),
	CONSTRAINT sn_Edge_loc_fk FOREIGN KEY (loc_id)
		REFERENCES eniwarenet.sn_loc (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT sn_Edge_weather_loc_fk FOREIGN KEY (wloc_id)
		REFERENCES eniwarenet.sn_weather_loc (id)
		ON UPDATE NO ACTION ON DELETE NO ACTION
);

/******************************************************************************
 * TABLE eniwarenet.sn_Edge_meta
 *
 * Stores JSON metadata specific to a Edge.
 */
CREATE TABLE eniwarenet.sn_Edge_meta (
  Edge_id 			bigint NOT NULL,
  created 			timestamp with time zone NOT NULL,
  updated 			timestamp with time zone NOT NULL,
  jdata				jsonb NOT NULL,
  CONSTRAINT sn_Edge_meta_pkey PRIMARY KEY (Edge_id),
  CONSTRAINT sn_Edge_meta_Edge_fk FOREIGN KEY (Edge_id)
        REFERENCES eniwarenet.sn_Edge (Edge_id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE CASCADE
);

/******************************************************************************
 * FUNCTION eniwarenet.store_Edge_meta(timestamptz, bigint, text)
 *
 * Add or update Edge metadata.
 *
 * @param cdate the creation date to use
 * @param Edge the Edge ID
 * @param jdata the metadata to store
 */
CREATE OR REPLACE FUNCTION eniwarenet.store_Edge_meta(
	cdate timestamp with time zone,
	Edge bigint,
	jdata text)
  RETURNS void LANGUAGE plpgsql VOLATILE AS
$BODY$
DECLARE
	udate timestamp with time zone := now();
	jdata_json jsonb := jdata::jsonb;
BEGIN
	INSERT INTO eniwarenet.sn_Edge_meta(Edge_id, created, updated, jdata)
	VALUES (Edge, cdate, udate, jdata_json)
	ON CONFLICT (Edge_id) DO UPDATE
	SET jdata = EXCLUDED.jdata, updated = EXCLUDED.updated;
END;
$BODY$;

CREATE OR REPLACE FUNCTION eniwarenet.get_Edge_local_timestamp(timestamp with time zone, bigint)
  RETURNS timestamp without time zone AS
$BODY$
	SELECT $1 AT TIME ZONE l.time_zone
	FROM eniwarenet.sn_Edge n
	INNER JOIN eniwarenet.sn_loc l ON l.id = n.loc_id
	WHERE n.Edge_id = $2
$BODY$
  LANGUAGE 'sql' STABLE;

/******************************************************************************
 * FUNCTION eniwarenet.get_Edge_timezone(bigint)
 *
 * Return a Edge's time zone.
 *
 * @param bigint the Edge ID
 * @return time zone name, e.g. 'Pacific/Auckland'
 */
CREATE OR REPLACE FUNCTION eniwarenet.get_Edge_timezone(bigint)
  RETURNS text AS
$BODY$
	SELECT l.time_zone
	FROM eniwarenet.sn_Edge n
	INNER JOIN eniwarenet.sn_loc l ON l.id = n.loc_id
	WHERE n.Edge_id = $1
$BODY$
  LANGUAGE 'sql' STABLE;

/* =========================================================================
   =========================================================================
   SEASON SUPPORT
   =========================================================================
   ========================================================================= */

/**************************************************************************************************
 * FUNCTION eniwarenet.get_season(date)
 *
 * Assign a "season" number to a date. Seasons are defined as:
 *
 * Dec,Jan,Feb = 0
 * Mar,Apr,May = 1
 * Jun,Jul,Aug = 2
 * Sep,Oct,Nov = 3
 *
 * @param date the date to calcualte the season for
 * @returns integer season constant
 */
CREATE OR REPLACE FUNCTION eniwarenet.get_season(date)
RETURNS INTEGER AS
$BODY$
	SELECT
	CASE EXTRACT(MONTH FROM $1)
		WHEN 12 THEN 0
		WHEN 1 THEN 0
		WHEN 2 THEN 0
		WHEN 3 THEN 1
		WHEN 4 THEN 1
		WHEN 5 THEN 1
		WHEN 6 THEN 2
		WHEN 7 THEN 2
		WHEN 8 THEN 2
		WHEN 9 THEN 3
		WHEN 10 THEN 3
		WHEN 11 THEN 3
	END AS season
$BODY$
  LANGUAGE 'sql' IMMUTABLE;


/**************************************************************************************************
 * FUNCTION eniwarenet.get_season_monday_start(date)
 *
 * Returns a date representing the first Monday within the provide date's season, where season
 * is defined by the eniwarenet.get_season(date) function. The actual returned date is meaningless
 * other than it will be a Monday and will be within the appropriate season.
 *
 * @param date the date to calcualte the Monday season date for
 * @returns date representing the first Monday within the season
 * @see eniwarenet.get_season(date)
 */
CREATE OR REPLACE FUNCTION eniwarenet.get_season_monday_start(date)
RETURNS DATE AS
$BODY$
	SELECT
	CASE eniwarenet.get_season($1)
		WHEN 0 THEN DATE '2000-12-04'
		WHEN 1 THEN DATE '2001-03-05'
		WHEN 2 THEN DATE '2001-06-04'
		ELSE DATE '2001-09-03'
  END AS season_monday
$BODY$
  LANGUAGE 'sql' IMMUTABLE;


/* =========================================================================
   =========================================================================
   PRICE
   =========================================================================
   ========================================================================= */

CREATE SEQUENCE eniwarenet.price_seq;

CREATE TABLE eniwarenet.sn_price_source (
	id				BIGINT NOT NULL DEFAULT nextval('eniwarenet.eniwarenet_seq'),
	created			TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	sname			CHARACTER VARYING(128) NOT NULL,
	fts_default		tsvector,
	PRIMARY KEY(id)
);

CREATE TRIGGER maintain_fts
  BEFORE INSERT OR UPDATE ON eniwarenet.sn_price_source
  FOR EACH ROW EXECUTE PROCEDURE
  tsvector_update_trigger(fts_default, 'pg_catalog.english', sname);

CREATE INDEX sn_price_source_fts_default_idx ON eniwarenet.sn_price_source USING gin(fts_default);

CREATE TABLE eniwarenet.sn_price_loc (
	id				BIGINT NOT NULL DEFAULT nextval('eniwarenet.eniwarenet_seq'),
	created			TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	loc_id			BIGINT NOT NULL,
	loc_name		CHARACTER VARYING(128) NOT NULL UNIQUE,
	source_id		BIGINT NOT NULL,
	source_data		CHARACTER VARYING(128),
	currency		VARCHAR(10) NOT NULL,
	unit			VARCHAR(20) NOT NULL,
	fts_default		tsvector,
	PRIMARY KEY (id),
    CONSTRAINT sn_price_loc_loc_fk FOREIGN KEY (loc_id)
  	    REFERENCES eniwarenet.sn_loc (id) MATCH SIMPLE
	    ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT sn_price_loc_sn_price_source_fk FOREIGN KEY (source_id)
		REFERENCES eniwarenet.sn_price_source (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TRIGGER maintain_fts
  BEFORE INSERT OR UPDATE ON eniwarenet.sn_price_loc
  FOR EACH ROW EXECUTE PROCEDURE
  tsvector_update_trigger(fts_default, 'pg_catalog.english', loc_name, source_data, currency);

CREATE INDEX sn_price_loc_fts_default_idx ON eniwarenet.sn_price_loc USING gin(fts_default);
