\echo Dropping aggregate datum views...

DROP VIEW solaragg.da_datum_avail_hourly;
DROP VIEW solaragg.da_datum_avail_daily;
DROP VIEW solaragg.da_datum_avail_monthly;

\echo Removing domains from datum tables...

ALTER TABLE solardatum.da_datum
  ALTER COLUMN ts SET DATA TYPE timestamp with time zone,
  ALTER COLUMN node_id SET DATA TYPE bigint,
  ALTER COLUMN source_id SET DATA TYPE character varying(64),
  ALTER COLUMN posted SET DATA TYPE timestamp with time zone;

ALTER TABLE solardatum.da_meta
  ALTER COLUMN node_id SET DATA TYPE bigint,
  ALTER COLUMN source_id SET DATA TYPE character varying(64),
  ALTER COLUMN created SET DATA TYPE timestamp with time zone,
  ALTER COLUMN updated SET DATA TYPE timestamp with time zone;

ALTER TABLE solaragg.agg_stale_datum
  ALTER COLUMN node_id SET DATA TYPE bigint,
  ALTER COLUMN source_id SET DATA TYPE character varying(64);

ALTER TABLE solaragg.agg_messages
  ALTER COLUMN node_id SET DATA TYPE bigint,
  ALTER COLUMN source_id SET DATA TYPE character varying(64),
  ALTER COLUMN ts SET DATA TYPE timestamp with time zone;

\echo Removing domains from datum aggregate tables...

ALTER TABLE solaragg.aud_datum_hourly
  ALTER COLUMN node_id SET DATA TYPE bigint,
  ALTER COLUMN source_id SET DATA TYPE character varying(64);

ALTER TABLE solaragg.agg_datum_hourly
  ALTER COLUMN node_id SET DATA TYPE bigint,
  ALTER COLUMN source_id SET DATA TYPE character varying(64);

ALTER TABLE solaragg.agg_datum_daily
  ALTER COLUMN node_id SET DATA TYPE bigint,
  ALTER COLUMN source_id SET DATA TYPE character varying(64);

ALTER TABLE solaragg.agg_datum_monthly
  ALTER COLUMN node_id SET DATA TYPE bigint,
  ALTER COLUMN source_id SET DATA TYPE character varying(64);

\echo Recreating aggregate datum views...

CREATE VIEW solaragg.da_datum_avail_hourly AS
WITH nodetz AS (
	SELECT n.node_id, COALESCE(l.time_zone, 'UTC') AS tz
	FROM solarnet.sn_node n
	LEFT OUTER JOIN solarnet.sn_loc l ON l.id = n.loc_id
)
SELECT date_trunc('hour', d.ts at time zone nodetz.tz) at time zone nodetz.tz AS ts_start, d.node_id, d.source_id
FROM solardatum.da_datum d
INNER JOIN nodetz ON nodetz.node_id = d.node_id
GROUP BY date_trunc('hour', d.ts at time zone nodetz.tz) at time zone nodetz.tz, d.node_id, d.source_id;

CREATE VIEW solaragg.da_datum_avail_daily AS
WITH nodetz AS (
	SELECT n.node_id, COALESCE(l.time_zone, 'UTC') AS tz
	FROM solarnet.sn_node n
	LEFT OUTER JOIN solarnet.sn_loc l ON l.id = n.loc_id
)
SELECT date_trunc('day', d.ts at time zone nodetz.tz) at time zone nodetz.tz AS ts_start, d.node_id, d.source_id
FROM solardatum.da_datum d
INNER JOIN nodetz ON nodetz.node_id = d.node_id
GROUP BY date_trunc('day', d.ts at time zone nodetz.tz) at time zone nodetz.tz, d.node_id, d.source_id;

CREATE VIEW solaragg.da_datum_avail_monthly AS
WITH nodetz AS (
	SELECT n.node_id, COALESCE(l.time_zone, 'UTC') AS tz
	FROM solarnet.sn_node n
	LEFT OUTER JOIN solarnet.sn_loc l ON l.id = n.loc_id
)
SELECT date_trunc('month', d.ts at time zone nodetz.tz) at time zone nodetz.tz AS ts_start, d.node_id, d.source_id
FROM solardatum.da_datum d
INNER JOIN nodetz ON nodetz.node_id = d.node_id
GROUP BY date_trunc('month', d.ts at time zone nodetz.tz) at time zone nodetz.tz, d.node_id, d.source_id;

\echo Recreating datum functions...

DROP FUNCTION solardatum.store_meta(solarcommon.ts, solarcommon.node_id, solarcommon.source_id, text);
CREATE OR REPLACE FUNCTION solardatum.store_meta(
	cdate timestamp with time zone,
	node bigint,
	src text,
	jdata text)
  RETURNS void LANGUAGE plpgsql VOLATILE AS
$BODY$
DECLARE
	udate timestamp with time zone := now();
	jdata_json json := jdata::json;
BEGIN
	INSERT INTO solardatum.da_meta(node_id, source_id, created, updated, jdata)
	VALUES (node, src, cdate, udate, jdata_json)
	ON CONFLICT (node_id, source_id) DO UPDATE
	SET jdata = EXCLUDED.jdata, updated = EXCLUDED.updated;
END;
$BODY$;

DROP FUNCTION solardatum.find_available_sources(solarcommon.node_id, solarcommon.ts, solarcommon.ts);
CREATE OR REPLACE FUNCTION solardatum.find_available_sources(
	IN node bigint,
	IN st timestamp with time zone DEFAULT NULL,
	IN en timestamp with time zone DEFAULT NULL)
  RETURNS TABLE(source_id text) AS
$BODY$
DECLARE
	node_tz text;
BEGIN
	IF st IS NOT NULL OR en IS NOT NULL THEN
		-- get the node TZ for local date/time
		SELECT l.time_zone  FROM solarnet.sn_node n
		INNER JOIN solarnet.sn_loc l ON l.id = n.loc_id
		WHERE n.node_id = node
		INTO node_tz;

		IF NOT FOUND THEN
			RAISE NOTICE 'Node % has no time zone, will use UTC.', node;
			node_tz := 'UTC';
		END IF;
	END IF;

	CASE
		WHEN st IS NULL AND en IS NULL THEN
			RETURN QUERY SELECT DISTINCT CAST(d.source_id AS text)
			FROM solaragg.agg_datum_daily d
			WHERE d.node_id = node;

		WHEN en IS NULL THEN
			RETURN QUERY SELECT DISTINCT CAST(d.source_id AS text)
			FROM solaragg.agg_datum_daily d
			WHERE d.node_id = node
				AND d.ts_start >= CAST(st at time zone node_tz AS DATE);

		WHEN st IS NULL THEN
			RETURN QUERY SELECT DISTINCT CAST(d.source_id AS text)
			FROM solaragg.agg_datum_daily d
			WHERE d.node_id = node
				AND d.ts_start <= CAST(en at time zone node_tz AS DATE);

		ELSE
			RETURN QUERY SELECT DISTINCT CAST(d.source_id AS text)
			FROM solaragg.agg_datum_daily d
			WHERE d.node_id = node
				AND d.ts_start >= CAST(st at time zone node_tz AS DATE)
				AND d.ts_start <= CAST(en at time zone node_tz AS DATE);
	END CASE;
END;$BODY$
  LANGUAGE plpgsql STABLE ROWS 50;

DROP FUNCTION solardatum.find_reportable_interval(solarcommon.node_id, solarcommon.source_id);
CREATE OR REPLACE FUNCTION solardatum.find_reportable_interval(
	IN node bigint,
	IN src text DEFAULT NULL,
	OUT ts_start timestamp with time zone,
	OUT ts_end timestamp with time zone,
	OUT node_tz TEXT,
	OUT node_tz_offset INTEGER)
  RETURNS RECORD AS
$BODY$
BEGIN
	CASE
		WHEN src IS NULL THEN
			SELECT min(ts) FROM solardatum.da_datum WHERE node_id = node
			INTO ts_start;
		ELSE
			SELECT min(ts) FROM solardatum.da_datum WHERE node_id = node AND source_id = src
			INTO ts_start;
	END CASE;

	CASE
		WHEN src IS NULL THEN
			SELECT max(ts) FROM solardatum.da_datum WHERE node_id = node
			INTO ts_end;
		ELSE
			SELECT max(ts) FROM solardatum.da_datum WHERE node_id = node AND source_id = src
			INTO ts_end;
	END CASE;

	SELECT
		l.time_zone,
		CAST(EXTRACT(epoch FROM z.utc_offset) / 60 AS INTEGER)
	FROM solarnet.sn_node n
	INNER JOIN solarnet.sn_loc l ON l.id = n.loc_id
	INNER JOIN pg_timezone_names z ON z.name = l.time_zone
	WHERE n.node_id = node
	INTO node_tz, node_tz_offset;

	IF NOT FOUND THEN
		node_tz := 'UTC';
		node_tz_offset := 0;
	END IF;

END;$BODY$
  LANGUAGE plpgsql STABLE;

DROP FUNCTION solardatum.find_sources_for_meta(bigint[],text);
CREATE OR REPLACE FUNCTION solardatum.find_sources_for_meta(
    IN nodes bigint[],
    IN criteria text
  )
  RETURNS TABLE(node_id bigint, source_id text)
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

if ( !filter.rootNode ) {
	plv8.elog(NOTICE, 'Malformed search filter:', criteria);
	return;
}

stmt = plv8.prepare('SELECT node_id, source_id, jdata FROM solardatum.da_meta WHERE node_id = ANY($1)', ['bigint[]']);
curs = stmt.cursor([nodes]);

while ( rec = curs.fetch() ) {
	meta = rec.jdata;
	matcher = objectPathMatcher(meta);
	if ( matcher.matchesFilter(filter) ) {
		resultRec.node_id = rec.node_id;
		resultRec.source_id = rec.source_id;
		plv8.return_next(resultRec);
	}
}

curs.close();
stmt.free();

$BODY$;
