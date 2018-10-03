\echo Dropping aggregate datum views...

DROP VIEW eniwareagg.da_datum_avail_hourly;
DROP VIEW eniwareagg.da_datum_avail_daily;
DROP VIEW eniwareagg.da_datum_avail_monthly;

\echo Removing domains from datum tables...

ALTER TABLE eniwaredatum.da_datum
  ALTER COLUMN ts SET DATA TYPE timestamp with time zone,
  ALTER COLUMN Edge_id SET DATA TYPE bigint,
  ALTER COLUMN source_id SET DATA TYPE character varying(64),
  ALTER COLUMN posted SET DATA TYPE timestamp with time zone;

ALTER TABLE eniwaredatum.da_meta
  ALTER COLUMN Edge_id SET DATA TYPE bigint,
  ALTER COLUMN source_id SET DATA TYPE character varying(64),
  ALTER COLUMN created SET DATA TYPE timestamp with time zone,
  ALTER COLUMN updated SET DATA TYPE timestamp with time zone;

ALTER TABLE eniwareagg.agg_stale_datum
  ALTER COLUMN Edge_id SET DATA TYPE bigint,
  ALTER COLUMN source_id SET DATA TYPE character varying(64);

ALTER TABLE eniwareagg.agg_messages
  ALTER COLUMN Edge_id SET DATA TYPE bigint,
  ALTER COLUMN source_id SET DATA TYPE character varying(64),
  ALTER COLUMN ts SET DATA TYPE timestamp with time zone;

\echo Removing domains from datum aggregate tables...

ALTER TABLE eniwareagg.aud_datum_hourly
  ALTER COLUMN Edge_id SET DATA TYPE bigint,
  ALTER COLUMN source_id SET DATA TYPE character varying(64);

ALTER TABLE eniwareagg.agg_datum_hourly
  ALTER COLUMN Edge_id SET DATA TYPE bigint,
  ALTER COLUMN source_id SET DATA TYPE character varying(64);

ALTER TABLE eniwareagg.agg_datum_daily
  ALTER COLUMN Edge_id SET DATA TYPE bigint,
  ALTER COLUMN source_id SET DATA TYPE character varying(64);

ALTER TABLE eniwareagg.agg_datum_monthly
  ALTER COLUMN Edge_id SET DATA TYPE bigint,
  ALTER COLUMN source_id SET DATA TYPE character varying(64);

\echo Recreating aggregate datum views...

CREATE VIEW eniwareagg.da_datum_avail_hourly AS
WITH Edgetz AS (
	SELECT n.Edge_id, COALESCE(l.time_zone, 'UTC') AS tz
	FROM eniwarenet.sn_Edge n
	LEFT OUTER JOIN eniwarenet.sn_loc l ON l.id = n.loc_id
)
SELECT date_trunc('hour', d.ts at time zone Edgetz.tz) at time zone Edgetz.tz AS ts_start, d.Edge_id, d.source_id
FROM eniwaredatum.da_datum d
INNER JOIN Edgetz ON Edgetz.Edge_id = d.Edge_id
GROUP BY date_trunc('hour', d.ts at time zone Edgetz.tz) at time zone Edgetz.tz, d.Edge_id, d.source_id;

CREATE VIEW eniwareagg.da_datum_avail_daily AS
WITH Edgetz AS (
	SELECT n.Edge_id, COALESCE(l.time_zone, 'UTC') AS tz
	FROM eniwarenet.sn_Edge n
	LEFT OUTER JOIN eniwarenet.sn_loc l ON l.id = n.loc_id
)
SELECT date_trunc('day', d.ts at time zone Edgetz.tz) at time zone Edgetz.tz AS ts_start, d.Edge_id, d.source_id
FROM eniwaredatum.da_datum d
INNER JOIN Edgetz ON Edgetz.Edge_id = d.Edge_id
GROUP BY date_trunc('day', d.ts at time zone Edgetz.tz) at time zone Edgetz.tz, d.Edge_id, d.source_id;

CREATE VIEW eniwareagg.da_datum_avail_monthly AS
WITH Edgetz AS (
	SELECT n.Edge_id, COALESCE(l.time_zone, 'UTC') AS tz
	FROM eniwarenet.sn_Edge n
	LEFT OUTER JOIN eniwarenet.sn_loc l ON l.id = n.loc_id
)
SELECT date_trunc('month', d.ts at time zone Edgetz.tz) at time zone Edgetz.tz AS ts_start, d.Edge_id, d.source_id
FROM eniwaredatum.da_datum d
INNER JOIN Edgetz ON Edgetz.Edge_id = d.Edge_id
GROUP BY date_trunc('month', d.ts at time zone Edgetz.tz) at time zone Edgetz.tz, d.Edge_id, d.source_id;

\echo Recreating datum functions...

DROP FUNCTION eniwaredatum.store_meta(eniwarecommon.ts, eniwarecommon.Edge_id, eniwarecommon.source_id, text);
CREATE OR REPLACE FUNCTION eniwaredatum.store_meta(
	cdate timestamp with time zone,
	Edge bigint,
	src text,
	jdata text)
  RETURNS void LANGUAGE plpgsql VOLATILE AS
$BODY$
DECLARE
	udate timestamp with time zone := now();
	jdata_json json := jdata::json;
BEGIN
	INSERT INTO eniwaredatum.da_meta(Edge_id, source_id, created, updated, jdata)
	VALUES (Edge, src, cdate, udate, jdata_json)
	ON CONFLICT (Edge_id, source_id) DO UPDATE
	SET jdata = EXCLUDED.jdata, updated = EXCLUDED.updated;
END;
$BODY$;

DROP FUNCTION eniwaredatum.find_available_sources(eniwarecommon.Edge_id, eniwarecommon.ts, eniwarecommon.ts);
CREATE OR REPLACE FUNCTION eniwaredatum.find_available_sources(
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
		SELECT l.time_zone  FROM eniwarenet.sn_Edge n
		INNER JOIN eniwarenet.sn_loc l ON l.id = n.loc_id
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
			FROM eniwareagg.agg_datum_daily d
			WHERE d.Edge_id = Edge;

		WHEN en IS NULL THEN
			RETURN QUERY SELECT DISTINCT CAST(d.source_id AS text)
			FROM eniwareagg.agg_datum_daily d
			WHERE d.Edge_id = Edge
				AND d.ts_start >= CAST(st at time zone Edge_tz AS DATE);

		WHEN st IS NULL THEN
			RETURN QUERY SELECT DISTINCT CAST(d.source_id AS text)
			FROM eniwareagg.agg_datum_daily d
			WHERE d.Edge_id = Edge
				AND d.ts_start <= CAST(en at time zone Edge_tz AS DATE);

		ELSE
			RETURN QUERY SELECT DISTINCT CAST(d.source_id AS text)
			FROM eniwareagg.agg_datum_daily d
			WHERE d.Edge_id = Edge
				AND d.ts_start >= CAST(st at time zone Edge_tz AS DATE)
				AND d.ts_start <= CAST(en at time zone Edge_tz AS DATE);
	END CASE;
END;$BODY$
  LANGUAGE plpgsql STABLE ROWS 50;

DROP FUNCTION eniwaredatum.find_reportable_interval(eniwarecommon.Edge_id, eniwarecommon.source_id);
CREATE OR REPLACE FUNCTION eniwaredatum.find_reportable_interval(
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
			SELECT min(ts) FROM eniwaredatum.da_datum WHERE Edge_id = Edge
			INTO ts_start;
		ELSE
			SELECT min(ts) FROM eniwaredatum.da_datum WHERE Edge_id = Edge AND source_id = src
			INTO ts_start;
	END CASE;

	CASE
		WHEN src IS NULL THEN
			SELECT max(ts) FROM eniwaredatum.da_datum WHERE Edge_id = Edge
			INTO ts_end;
		ELSE
			SELECT max(ts) FROM eniwaredatum.da_datum WHERE Edge_id = Edge AND source_id = src
			INTO ts_end;
	END CASE;

	SELECT
		l.time_zone,
		CAST(EXTRACT(epoch FROM z.utc_offset) / 60 AS INTEGER)
	FROM eniwarenet.sn_Edge n
	INNER JOIN eniwarenet.sn_loc l ON l.id = n.loc_id
	INNER JOIN pg_timezone_names z ON z.name = l.time_zone
	WHERE n.Edge_id = Edge
	INTO Edge_tz, Edge_tz_offset;

	IF NOT FOUND THEN
		Edge_tz := 'UTC';
		Edge_tz_offset := 0;
	END IF;

END;$BODY$
  LANGUAGE plpgsql STABLE;

DROP FUNCTION eniwaredatum.find_sources_for_meta(bigint[],text);
CREATE OR REPLACE FUNCTION eniwaredatum.find_sources_for_meta(
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

stmt = plv8.prepare('SELECT Edge_id, source_id, jdata FROM eniwaredatum.da_meta WHERE Edge_id = ANY($1)', ['bigint[]']);
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
