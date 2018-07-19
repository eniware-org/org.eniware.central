CREATE SCHEMA IF NOT EXISTS solardatum;

CREATE SCHEMA IF NOT EXISTS solaragg;

CREATE TABLE solardatum.da_datum (
  ts timestamp with time zone NOT NULL,
  Edge_id bigint NOT NULL,
  source_id text NOT NULL,
  posted timestamp with time zone NOT NULL,
  jdata_i jsonb,
  jdata_a jsonb,
  jdata_s jsonb,
  jdata_t text[],
  CONSTRAINT da_datum_pkey PRIMARY KEY (Edge_id, ts, source_id)
);

CREATE OR REPLACE FUNCTION solardatum.jdata_from_datum(datum solardatum.da_datum)
	RETURNS jsonb
	LANGUAGE SQL IMMUTABLE AS
$$
	SELECT solarcommon.jdata_from_components(datum.jdata_i, datum.jdata_a, datum.jdata_s, datum.jdata_t);
$$;

CREATE VIEW solardatum.da_datum_data AS
    SELECT d.ts, d.Edge_id, d.source_id, d.posted, solardatum.jdata_from_datum(d) AS jdata
    FROM solardatum.da_datum d;

CREATE TABLE solardatum.da_meta (
  Edge_id bigint NOT NULL,
  source_id text NOT NULL,
  created timestamp with time zone NOT NULL,
  updated timestamp with time zone NOT NULL,
  jdata jsonb NOT NULL,
  CONSTRAINT da_meta_pkey PRIMARY KEY (Edge_id, source_id)
);

CREATE TABLE solaragg.agg_stale_datum (
  ts_start timestamp with time zone NOT NULL,
  Edge_id bigint NOT NULL,
  source_id text NOT NULL,
  agg_kind char(1) NOT NULL,
  created timestamp NOT NULL DEFAULT now(),
  CONSTRAINT agg_stale_datum_pkey PRIMARY KEY (agg_kind, ts_start, Edge_id, source_id)
);

CREATE TABLE solaragg.agg_messages (
  created timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  Edge_id bigint NOT NULL,
  source_id text NOT NULL,
  ts timestamp with time zone NOT NULL,
  msg text NOT NULL
);

CREATE INDEX agg_messages_ts_Edge_idx ON solaragg.agg_messages (ts, Edge_id);

CREATE TABLE solaragg.agg_datum_hourly (
  ts_start timestamp with time zone NOT NULL,
  local_date timestamp without time zone NOT NULL,
  Edge_id bigint NOT NULL,
  source_id text NOT NULL,
  jdata_i jsonb,
  jdata_a jsonb,
  jdata_s jsonb,
  jdata_t text[],
 CONSTRAINT agg_datum_hourly_pkey PRIMARY KEY (Edge_id, ts_start, source_id)
);

CREATE TABLE solaragg.aud_datum_hourly (
  ts_start timestamp with time zone NOT NULL,
  Edge_id bigint NOT NULL,
  source_id text NOT NULL,
  prop_count integer NOT NULL,
  datum_q_count integer NOT NULL DEFAULT 0,
  CONSTRAINT aud_datum_hourly_pkey PRIMARY KEY (Edge_id, ts_start, source_id)
);

CREATE TABLE solaragg.agg_datum_daily (
  ts_start timestamp with time zone NOT NULL,
  local_date date NOT NULL,
  Edge_id bigint NOT NULL,
  source_id text NOT NULL,
  jdata_i jsonb,
  jdata_a jsonb,
  jdata_s jsonb,
  jdata_t text[],
 CONSTRAINT agg_datum_daily_pkey PRIMARY KEY (Edge_id, ts_start, source_id)
);

CREATE TABLE solaragg.agg_datum_monthly (
  ts_start timestamp with time zone NOT NULL,
  local_date date NOT NULL,
  Edge_id bigint NOT NULL,
  source_id text NOT NULL,
  jdata_i jsonb,
  jdata_a jsonb,
  jdata_s jsonb,
  jdata_t text[],
 CONSTRAINT agg_datum_monthly_pkey PRIMARY KEY (Edge_id, ts_start, source_id)
);

CREATE OR REPLACE FUNCTION solaragg.jdata_from_datum(datum solaragg.agg_datum_hourly)
	RETURNS jsonb
	LANGUAGE SQL IMMUTABLE AS
$$
	SELECT solarcommon.jdata_from_components(datum.jdata_i, datum.jdata_a, datum.jdata_s, datum.jdata_t);
$$;

CREATE OR REPLACE FUNCTION solaragg.jdata_from_datum(datum solaragg.agg_datum_daily)
	RETURNS jsonb
	LANGUAGE SQL IMMUTABLE AS
$$
	SELECT solarcommon.jdata_from_components(datum.jdata_i, datum.jdata_a, datum.jdata_s, datum.jdata_t);
$$;

CREATE OR REPLACE FUNCTION solaragg.jdata_from_datum(datum solaragg.agg_datum_monthly)
	RETURNS jsonb
	LANGUAGE SQL IMMUTABLE AS
$$
	SELECT solarcommon.jdata_from_components(datum.jdata_i, datum.jdata_a, datum.jdata_s, datum.jdata_t);
$$;

CREATE VIEW solaragg.agg_datum_hourly_data AS
  SELECT d.ts_start, d.local_date, d.Edge_id, d.source_id, solaragg.jdata_from_datum(d) AS jdata
  FROM solaragg.agg_datum_hourly d;

CREATE VIEW solaragg.agg_datum_daily_data AS
  SELECT d.ts_start, d.local_date, d.Edge_id, d.source_id, solaragg.jdata_from_datum(d) AS jdata
  FROM solaragg.agg_datum_daily d;

CREATE VIEW solaragg.agg_datum_monthly_data AS
  SELECT d.ts_start, d.local_date, d.Edge_id, d.source_id, solaragg.jdata_from_datum(d) AS jdata
  FROM solaragg.agg_datum_monthly d;

CREATE VIEW solaragg.da_datum_avail_hourly AS
WITH Edgetz AS (
	SELECT n.Edge_id, COALESCE(l.time_zone, 'UTC') AS tz
	FROM solarnet.sn_Edge n
	LEFT OUTER JOIN solarnet.sn_loc l ON l.id = n.loc_id
)
SELECT date_trunc('hour', d.ts at time zone Edgetz.tz) at time zone Edgetz.tz AS ts_start, d.Edge_id, d.source_id
FROM solardatum.da_datum d
INNER JOIN Edgetz ON Edgetz.Edge_id = d.Edge_id
GROUP BY date_trunc('hour', d.ts at time zone Edgetz.tz) at time zone Edgetz.tz, d.Edge_id, d.source_id;

CREATE VIEW solaragg.da_datum_avail_daily AS
WITH Edgetz AS (
	SELECT n.Edge_id, COALESCE(l.time_zone, 'UTC') AS tz
	FROM solarnet.sn_Edge n
	LEFT OUTER JOIN solarnet.sn_loc l ON l.id = n.loc_id
)
SELECT date_trunc('day', d.ts at time zone Edgetz.tz) at time zone Edgetz.tz AS ts_start, d.Edge_id, d.source_id
FROM solardatum.da_datum d
INNER JOIN Edgetz ON Edgetz.Edge_id = d.Edge_id
GROUP BY date_trunc('day', d.ts at time zone Edgetz.tz) at time zone Edgetz.tz, d.Edge_id, d.source_id;

CREATE VIEW solaragg.da_datum_avail_monthly AS
WITH Edgetz AS (
	SELECT n.Edge_id, COALESCE(l.time_zone, 'UTC') AS tz
	FROM solarnet.sn_Edge n
	LEFT OUTER JOIN solarnet.sn_loc l ON l.id = n.loc_id
)
SELECT date_trunc('month', d.ts at time zone Edgetz.tz) at time zone Edgetz.tz AS ts_start, d.Edge_id, d.source_id
FROM solardatum.da_datum d
INNER JOIN Edgetz ON Edgetz.Edge_id = d.Edge_id
GROUP BY date_trunc('month', d.ts at time zone Edgetz.tz) at time zone Edgetz.tz, d.Edge_id, d.source_id;

CREATE OR REPLACE FUNCTION solardatum.store_meta(
	cdate timestamp with time zone,
	Edge bigint,
	src text,
	jdata text)
  RETURNS void LANGUAGE plpgsql VOLATILE AS
$BODY$
DECLARE
	udate timestamp with time zone := now();
	jdata_json jsonb := jdata::jsonb;
BEGIN
	INSERT INTO solardatum.da_meta(Edge_id, source_id, created, updated, jdata)
	VALUES (Edge, src, cdate, udate, jdata_json)
	ON CONFLICT (Edge_id, source_id) DO UPDATE
	SET jdata = EXCLUDED.jdata, updated = EXCLUDED.updated;
END;
$BODY$;

CREATE OR REPLACE FUNCTION solardatum.find_available_sources(
	IN Edge bigint,
	IN st timestamp with time zone DEFAULT NULL,
	IN en timestamp with time zone DEFAULT NULL)
  RETURNS TABLE(source_id text) AS
$BODY$
DECLARE
	Edge_tz text;
BEGIN
	IF st IS NOT NULL OR en IS NOT NULL THEN
		-- get the Edge TZ for local date/time
		SELECT l.time_zone  FROM solarnet.sn_Edge n
		INNER JOIN solarnet.sn_loc l ON l.id = n.loc_id
		WHERE n.Edge_id = Edge
		INTO Edge_tz;

		IF NOT FOUND THEN
			RAISE NOTICE 'Edge % has no time zone, will use UTC.', Edge;
			Edge_tz := 'UTC';
		END IF;
	END IF;

	CASE
		WHEN st IS NULL AND en IS NULL THEN
			RETURN QUERY SELECT DISTINCT CAST(d.source_id AS text)
			FROM solaragg.agg_datum_daily d
			WHERE d.Edge_id = Edge;

		WHEN en IS NULL THEN
			RETURN QUERY SELECT DISTINCT CAST(d.source_id AS text)
			FROM solaragg.agg_datum_daily d
			WHERE d.Edge_id = Edge
				AND d.ts_start >= CAST(st at time zone Edge_tz AS DATE);

		WHEN st IS NULL THEN
			RETURN QUERY SELECT DISTINCT CAST(d.source_id AS text)
			FROM solaragg.agg_datum_daily d
			WHERE d.Edge_id = Edge
				AND d.ts_start <= CAST(en at time zone Edge_tz AS DATE);

		ELSE
			RETURN QUERY SELECT DISTINCT CAST(d.source_id AS text)
			FROM solaragg.agg_datum_daily d
			WHERE d.Edge_id = Edge
				AND d.ts_start >= CAST(st at time zone Edge_tz AS DATE)
				AND d.ts_start <= CAST(en at time zone Edge_tz AS DATE);
	END CASE;
END;$BODY$
  LANGUAGE plpgsql STABLE ROWS 50;

CREATE OR REPLACE FUNCTION solardatum.find_reportable_interval(
	IN Edge bigint,
	IN src text DEFAULT NULL,
	OUT ts_start timestamp with time zone,
	OUT ts_end timestamp with time zone,
	OUT Edge_tz TEXT,
	OUT Edge_tz_offset INTEGER)
  RETURNS RECORD AS
$BODY$
BEGIN
	CASE
		WHEN src IS NULL THEN
			SELECT min(ts) FROM solardatum.da_datum WHERE Edge_id = Edge
			INTO ts_start;
		ELSE
			SELECT min(ts) FROM solardatum.da_datum WHERE Edge_id = Edge AND source_id = src
			INTO ts_start;
	END CASE;

	CASE
		WHEN src IS NULL THEN
			SELECT max(ts) FROM solardatum.da_datum WHERE Edge_id = Edge
			INTO ts_end;
		ELSE
			SELECT max(ts) FROM solardatum.da_datum WHERE Edge_id = Edge AND source_id = src
			INTO ts_end;
	END CASE;

	SELECT
		l.time_zone,
		CAST(EXTRACT(epoch FROM z.utc_offset) / 60 AS INTEGER)
	FROM solarnet.sn_Edge n
	INNER JOIN solarnet.sn_loc l ON l.id = n.loc_id
	INNER JOIN pg_timezone_names z ON z.name = l.time_zone
	WHERE n.Edge_id = Edge
	INTO Edge_tz, Edge_tz_offset;

	IF NOT FOUND THEN
		Edge_tz := 'UTC';
		Edge_tz_offset := 0;
	END IF;

END;$BODY$
  LANGUAGE plpgsql STABLE;

CREATE OR REPLACE FUNCTION solardatum.populate_updated()
  RETURNS "trigger" AS
$BODY$
BEGIN
	NEW.updated := now();
	RETURN NEW;
END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE;

CREATE TRIGGER populate_updated
  BEFORE INSERT OR UPDATE
  ON solardatum.da_meta
  FOR EACH ROW
  EXECUTE PROCEDURE solardatum.populate_updated();




/**
 * Find source IDs matching a datum metadata search filter.
 *
 * Search filters are specified using LDAP filter syntax, e.g. <code>(/m/foo=bar)</code>.
 *
 * @param Edges				array of Edge IDs
 * @param criteria			the search filter
 *
 * @returns All matching source IDs.
 */
CREATE OR REPLACE FUNCTION solardatum.find_sources_for_meta(
    IN Edges bigint[],
    IN criteria text
  )
  RETURNS TABLE(Edge_id bigint, source_id text)
  LANGUAGE plv8 ROWS 100 STABLE AS
$BODY$
'use strict';

var objectPathMatcher = require('util/objectPathMatcher').default,
	searchFilter = require('util/searchFilter').default;

var filter = searchFilter(criteria),
	stmt,
	curs,
	rec,
	meta,
	matcher,
	resultRec = {};

if ( !filter.rootEdge ) {
	plv8.elog(NOTICE, 'Malformed search filter:', criteria);
	return;
}

stmt = plv8.prepare('SELECT Edge_id, source_id, jdata FROM solardatum.da_meta WHERE Edge_id = ANY($1)', ['bigint[]']);
curs = stmt.cursor([Edges]);

while ( rec = curs.fetch() ) {
	meta = rec.jdata;
	matcher = objectPathMatcher(meta);
	if ( matcher.matchesFilter(filter) ) {
		resultRec.Edge_id = rec.Edge_id;
		resultRec.source_id = rec.source_id;
		plv8.return_next(resultRec);
	}
}

curs.close();
stmt.free();

$BODY$;

/**
 * Count the properties in a datum JSON object.
 *
 * @param jdata				the datum JSON
 *
 * @returns The property count.
 */
CREATE OR REPLACE FUNCTION solardatum.datum_prop_count(IN jdata jsonb)
  RETURNS INTEGER
  LANGUAGE plv8
  IMMUTABLE AS
$BODY$
'use strict';
var count = 0, prop, val;
if ( jdata ) {
	for ( prop in jdata ) {
		val = jdata[prop];
		if ( Array.isArray(val) ) {
			count += val.length;
		} else {
			count += Object.keys(val).length;
		}
	}
}
return count;
$BODY$;
