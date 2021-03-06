DROP FUNCTION eniwareagg.calc_datum_time_slots(bigint,text[],timestamp with time zone,interval,integer,interval);
CREATE OR REPLACE FUNCTION eniwareagg.calc_datum_time_slots(
	IN Edge bigint,
	IN sources text[],
	IN start_ts timestamp with time zone,
	IN span interval,
	IN slotsecs integer DEFAULT 600,
	IN tolerance interval DEFAULT interval '1 hour')
  RETURNS TABLE(ts_start timestamp with time zone, source_id text, jdata jsonb) LANGUAGE plv8 AS
$BODY$
'use strict';

var intervalMs = require('util/intervalMs').default;
var aggregator = require('datum/aggregator').default;
var slotAggregator = require('datum/slotAggregator').default;

var spanMs = intervalMs(span),
	endTs = start_ts.getTime() + spanMs,
	slotMode = (slotsecs >= 60 && slotsecs <= 1800),
	ignoreLogMessages = (slotMode === true || spanMs !== 3600000),
	stmt,
	cur,
	rec,
	helper,
	aggResult,
	i;

if ( slotMode ) {
	stmt = plv8.prepare(
		'SELECT ts, eniwareagg.minute_time_slot(ts, '+slotsecs+') as ts_start, source_id, jdata FROM eniwareagg.find_datum_for_time_span($1, $2, $3, $4, $5)',
		['bigint', 'text[]', 'timestamp with time zone', 'interval', 'interval']);
	helper = slotAggregator({
		startTs : start_ts.getTime(),
		endTs : endTs,
		slotSecs : slotsecs
	});
} else {
	stmt = plv8.prepare(
		'SELECT ts, source_id, jdata FROM eniwareagg.find_datum_for_time_span($1, $2, $3, $4, $5)',
		['bigint', 'text[]', 'timestamp with time zone', 'interval', 'interval']);
	helper = aggregator({
		startTs : start_ts.getTime(),
		endTs : endTs,
	});
}

cur = stmt.cursor([Edge, sources, start_ts, span, tolerance]);

while ( rec = cur.fetch() ) {
	if ( !rec.jdata ) {
		continue;
	}
	aggResult = helper.addDatumRecord(rec);
	if ( aggResult ) {
		plv8.return_next(aggResult);
	}
}
aggResult = helper.finish();
if ( Array.isArray(aggResult) ) {
	for ( i = 0; i < aggResult.length; i += 1 ) {
		plv8.return_next(aggResult[i]);
	}
}

cur.close();
stmt.free();

$BODY$ STABLE;

DROP FUNCTION eniwareagg.find_agg_datum_minute(bigint,text[],timestamptz,timestamptz,integer,interval);
CREATE OR REPLACE FUNCTION eniwareagg.find_agg_datum_minute(
	IN Edge bigint,
	IN source text[],
	IN start_ts timestamp with time zone,
	IN end_ts timestamp with time zone,
	IN slotsecs integer DEFAULT 600,
	IN tolerance interval DEFAULT interval '1 hour')
  RETURNS TABLE(
	Edge_id bigint,
	ts_start timestamp with time zone,
	local_date timestamp without time zone,
	source_id text,
	jdata jsonb)
  LANGUAGE sql
  STABLE AS
$BODY$
SELECT
	Edge AS Edge_id,
	d.ts_start,
	d.ts_start AT TIME ZONE COALESCE(l.time_zone, 'UTC') AS local_date,
	d.source_id,
	d.jdata
 FROM eniwareagg.calc_datum_time_slots(
	Edge,
	source,
	eniwareagg.minute_time_slot(start_ts, eniwareagg.slot_seconds(slotsecs)),
	(end_ts - eniwareagg.minute_time_slot(start_ts, eniwareagg.slot_seconds(slotsecs))),
	eniwareagg.slot_seconds(slotsecs),
	tolerance
) AS d
JOIN eniwarenet.sn_Edge n ON n.Edge_id = Edge
LEFT OUTER JOIN eniwarenet.sn_loc l ON l.id = n.loc_id
$BODY$;

DROP FUNCTION eniwareagg.find_datum_for_time_span(bigint,text[],timestamp with time zone,interval,interval);
CREATE OR REPLACE FUNCTION eniwareagg.find_datum_for_time_span(
    IN Edge bigint,
    IN sources text[],
    IN start_ts timestamp with time zone,
    IN span interval,
    IN tolerance interval DEFAULT '01:00:00'::interval)
  RETURNS TABLE(ts timestamp with time zone, source_id text, jdata jsonb) AS
$BODY$
SELECT sub.ts, sub.source_id, sub.jdata FROM (
	-- subselect filters out "extra" leading/lagging rows from results
	SELECT
		d.ts,
		d.source_id,
		CASE
			WHEN lead(d.ts) over win < start_ts OR lag(d.ts) over win > (start_ts + span)
				THEN TRUE
			ELSE FALSE
		END AS outside,
		eniwaredatum.jdata_from_datum(d) as jdata
	FROM eniwaredatum.da_datum d
	WHERE d.Edge_id = Edge
		AND d.source_id = ANY(sources)
		AND d.ts >= start_ts - tolerance
		AND d.ts <= start_ts + span + tolerance
	WINDOW win AS (PARTITION BY d.source_id ORDER BY d.ts)
	ORDER BY d.ts, d.source_id
) AS sub
WHERE
	sub.outside = FALSE
$BODY$
  LANGUAGE sql STABLE;

DROP FUNCTION eniwareagg.find_agg_datum_hod(bigint,text[],text[],timestamptz,timestamptz);
CREATE OR REPLACE FUNCTION eniwareagg.find_agg_datum_hod(
	IN Edge bigint,
	IN source text[],
	IN path text[],
	IN start_ts timestamp with time zone DEFAULT '2008-01-01 00:00+0'::timestamptz,
	IN end_ts timestamp with time zone DEFAULT CURRENT_TIMESTAMP)
  RETURNS TABLE(
	Edge_id bigint,
	ts_start timestamp with time zone,
	local_date timestamp without time zone,
	source_id text,
	jdata jsonb)
  LANGUAGE sql
  STABLE AS
$BODY$
SELECT
	Edge AS Edge_id,
	(CAST('2001-01-01 ' || to_char(EXTRACT(hour FROM d.local_date), '00') || ':00' AS TIMESTAMP)) AT TIME ZONE 'UTC' AS ts_start,
	(CAST('2001-01-01 ' || to_char(EXTRACT(hour FROM d.local_date), '00') || ':00' AS TIMESTAMP)) AS local_date,
	d.source_id,
	('{"' || path[1] || '":{"' || path[2] || '":'
		|| ROUND(AVG(CAST(jsonb_extract_path_text(eniwareagg.jdata_from_datum(d), VARIADIC path) AS double precision)) * 1000) / 1000
		|| '}}')::jsonb as jdata
FROM eniwareagg.agg_datum_hourly d
WHERE
	d.Edge_id = Edge
	AND d.source_id = ANY(source)
	AND d.ts_start >= start_ts
	AND d.ts_start < end_ts
GROUP BY
	EXTRACT(hour FROM d.local_date),
	d.source_id
$BODY$;

DROP FUNCTION eniwareagg.find_agg_datum_seasonal_hod(bigint,text[],text[],timestamptz,timestamptz);
CREATE OR REPLACE FUNCTION eniwareagg.find_agg_datum_seasonal_hod(
	IN Edge bigint,
	IN source text[],
	IN path text[],
	IN start_ts timestamp with time zone DEFAULT '2008-01-01 00:00+0'::timestamptz,
	IN end_ts timestamp with time zone DEFAULT CURRENT_TIMESTAMP)
  RETURNS TABLE(
	Edge_id bigint,
	ts_start timestamp with time zone,
	local_date timestamp without time zone,
	source_id text,
	jdata jsonb)
  LANGUAGE sql
  STABLE AS
$BODY$
SELECT
	Edge AS Edge_id,
	(eniwarenet.get_season_monday_start(CAST(d.local_date AS DATE))
		+ CAST(EXTRACT(hour FROM d.local_date) || ' hour' AS INTERVAL)) AT TIME ZONE 'UTC' AS ts_start,
	eniwarenet.get_season_monday_start(CAST(d.local_date AS DATE))
		+ CAST(EXTRACT(hour FROM d.local_date) || ' hour' AS INTERVAL) AS local_date,
	d.source_id,
	('{"' || path[1] || '":{"' || path[2] || '":'
		|| ROUND(AVG(CAST(jsonb_extract_path_text(eniwareagg.jdata_from_datum(d), VARIADIC path) AS double precision)) * 1000) / 1000
		|| '}}')::jsonb as jdata
FROM eniwareagg.agg_datum_hourly d
WHERE
	d.Edge_id = Edge
	AND d.source_id = ANY(source)
	AND d.ts_start >= start_ts
	AND d.ts_start < end_ts
GROUP BY
	eniwarenet.get_season_monday_start(CAST(d.local_date AS date)),
	EXTRACT(hour FROM d.local_date),
	d.source_id
$BODY$;

DROP FUNCTION eniwareagg.find_agg_datum_dow(bigint,text[],text[],timestamptz,timestamptz);
CREATE OR REPLACE FUNCTION eniwareagg.find_agg_datum_dow(
	IN Edge bigint,
	IN source text[],
	IN path text[],
	IN start_ts timestamp with time zone DEFAULT '2001-01-01 00:00+0'::timestamptz,
	IN end_ts timestamp with time zone DEFAULT CURRENT_TIMESTAMP)
  RETURNS TABLE(
	Edge_id bigint,
	ts_start timestamp with time zone,
	local_date timestamp without time zone,
	source_id text,
	jdata jsonb)
  LANGUAGE sql
  STABLE AS
$BODY$
SELECT
	Edge AS Edge_id,
	(DATE '2001-01-01' + CAST((EXTRACT(isodow FROM d.local_date) - 1) || ' day' AS INTERVAL)) AT TIME ZONE 'UTC' AS ts_start,
	(DATE '2001-01-01' + CAST((EXTRACT(isodow FROM d.local_date) - 1) || ' day' AS INTERVAL)) AS local_date,
	d.source_id,
	('{"' || path[1] || '":{"' || path[2] || '":'
		|| ROUND(AVG(CAST(jsonb_extract_path_text(eniwareagg.jdata_from_datum(d), VARIADIC path) AS double precision)) * 1000) / 1000
		|| '}}')::jsonb as jdata
FROM eniwareagg.agg_datum_daily d
WHERE
	d.Edge_id = Edge
	AND d.source_id = ANY(source)
	AND d.ts_start >= start_ts
	AND d.ts_start < end_ts
GROUP BY
	EXTRACT(isodow FROM d.local_date),
	d.source_id
$BODY$;

DROP FUNCTION eniwareagg.find_agg_datum_seasonal_dow(bigint,text[],text[],timestamptz,timestamptz);
CREATE OR REPLACE FUNCTION eniwareagg.find_agg_datum_seasonal_dow(
	IN Edge bigint,
	IN source text[],
	IN path text[],
	IN start_ts timestamp with time zone DEFAULT '2001-01-01 00:00+0'::timestamptz,
	IN end_ts timestamp with time zone DEFAULT CURRENT_TIMESTAMP)
  RETURNS TABLE(
	Edge_id bigint,
	ts_start timestamp with time zone,
	local_date timestamp without time zone,
	source_id text,
	jdata jsonb)
  LANGUAGE sql
  STABLE AS
$BODY$
SELECT
	Edge AS Edge_id,
	(eniwarenet.get_season_monday_start(d.local_date)
		+ CAST((EXTRACT(isodow FROM d.local_date) - 1) || ' day' AS INTERVAL)) AT TIME ZONE 'UTC' AS ts_start,
	(eniwarenet.get_season_monday_start(d.local_date)
		+ CAST((EXTRACT(isodow FROM d.local_date) - 1) || ' day' AS INTERVAL)) AS local_date,
	d.source_id,
	('{"' || path[1] || '":{"' || path[2] || '":'
		|| ROUND(AVG(CAST(jsonb_extract_path_text(eniwareagg.jdata_from_datum(d), VARIADIC path) AS double precision)) * 1000) / 1000
		|| '}}')::jsonb as jdata
FROM eniwareagg.agg_datum_daily d
WHERE
	d.Edge_id = Edge
	AND d.source_id = ANY(source)
	AND d.ts_start >= start_ts
	AND d.ts_start < end_ts
GROUP BY
	eniwarenet.get_season_monday_start(CAST(d.local_date AS date)),
	EXTRACT(isodow FROM d.local_date),
	d.source_id
$BODY$;

CREATE OR REPLACE FUNCTION eniwareagg.process_one_agg_stale_datum(kind char)
  RETURNS integer LANGUAGE plpgsql VOLATILE AS
$BODY$
DECLARE
	stale record;
	curs CURSOR FOR SELECT * FROM eniwareagg.agg_stale_datum
			WHERE agg_kind = kind
			ORDER BY ts_start ASC, created ASC, Edge_id ASC, source_id ASC
			LIMIT 1
			FOR UPDATE SKIP LOCKED;
	agg_span interval;
	agg_json jsonb := NULL;
	Edge_tz text := 'UTC';
	result integer := 0;
BEGIN
	CASE kind
		WHEN 'h' THEN
			agg_span := interval '1 hour';
		WHEN 'd' THEN
			agg_span := interval '1 day';
		ELSE
			agg_span := interval '1 month';
	END CASE;

	OPEN curs;
	FETCH NEXT FROM curs INTO stale;

	IF FOUND THEN
		-- get the Edge TZ for local date/time
		SELECT l.time_zone  FROM eniwarenet.sn_Edge n
		INNER JOIN eniwarenet.sn_loc l ON l.id = n.loc_id
		WHERE n.Edge_id = stale.Edge_id
		INTO Edge_tz;

		IF NOT FOUND THEN
			RAISE NOTICE 'Edge % has no time zone, will use UTC.', stale.Edge_id;
			Edge_tz := 'UTC';
		END IF;

		SELECT jdata FROM eniwareagg.calc_datum_time_slots(stale.Edge_id, ARRAY[stale.source_id::text],
			stale.ts_start, agg_span, 0, interval '1 hour')
		INTO agg_json;
		IF agg_json IS NULL THEN
			CASE kind
				WHEN 'h' THEN
					DELETE FROM eniwareagg.agg_datum_hourly
					WHERE Edge_id = stale.Edge_id
						AND source_id = stale.source_id
						AND ts_start = stale.ts_start;
				WHEN 'd' THEN
					DELETE FROM eniwareagg.agg_datum_daily
					WHERE Edge_id = stale.Edge_id
						AND source_id = stale.source_id
						AND ts_start = stale.ts_start;
				ELSE
					DELETE FROM eniwareagg.agg_datum_monthly
					WHERE Edge_id = stale.Edge_id
						AND source_id = stale.source_id
						AND ts_start = stale.ts_start;
			END CASE;
		ELSE
			CASE kind
				WHEN 'h' THEN
					INSERT INTO eniwareagg.agg_datum_hourly (
						ts_start, local_date, Edge_id, source_id,
						jdata_i, jdata_a, jdata_s, jdata_t)
					VALUES (
						stale.ts_start,
						stale.ts_start at time zone Edge_tz,
						stale.Edge_id,
						stale.source_id,
						agg_json->'i',
						agg_json->'a',
						agg_json->'s',
						eniwarecommon.json_array_to_text_array(agg_json->'t')
					)
					ON CONFLICT (Edge_id, ts_start, source_id) DO UPDATE
					SET jdata_i = EXCLUDED.jdata_i,
						jdata_a = EXCLUDED.jdata_a,
						jdata_s = EXCLUDED.jdata_s,
						jdata_t = EXCLUDED.jdata_t;
				WHEN 'd' THEN
					INSERT INTO eniwareagg.agg_datum_daily (
						ts_start, local_date, Edge_id, source_id,
						jdata_i, jdata_a, jdata_s, jdata_t)
					VALUES (
						stale.ts_start,
						CAST(stale.ts_start at time zone Edge_tz AS DATE),
						stale.Edge_id,
						stale.source_id,
						agg_json->'i',
						agg_json->'a',
						agg_json->'s',
						eniwarecommon.json_array_to_text_array(agg_json->'t')
					)
					ON CONFLICT (Edge_id, ts_start, source_id) DO UPDATE
					SET jdata_i = EXCLUDED.jdata_i,
						jdata_a = EXCLUDED.jdata_a,
						jdata_s = EXCLUDED.jdata_s,
						jdata_t = EXCLUDED.jdata_t;
				ELSE
					INSERT INTO eniwareagg.agg_datum_monthly (
						ts_start, local_date, Edge_id, source_id,
						jdata_i, jdata_a, jdata_s, jdata_t)
					VALUES (
						stale.ts_start,
						CAST(stale.ts_start at time zone Edge_tz AS DATE),
						stale.Edge_id,
						stale.source_id,
						agg_json->'i',
						agg_json->'a',
						agg_json->'s',
						eniwarecommon.json_array_to_text_array(agg_json->'t')
					)
					ON CONFLICT (Edge_id, ts_start, source_id) DO UPDATE
					SET jdata_i = EXCLUDED.jdata_i,
						jdata_a = EXCLUDED.jdata_a,
						jdata_s = EXCLUDED.jdata_s,
						jdata_t = EXCLUDED.jdata_t;
			END CASE;
		END IF;
		DELETE FROM eniwareagg.agg_stale_datum WHERE CURRENT OF curs;
		result := 1;

		-- now make sure we recalculate the next aggregate level by submitting a stale record for the next level
		CASE kind
			WHEN 'h' THEN
				INSERT INTO eniwareagg.agg_stale_datum (ts_start, Edge_id, source_id, agg_kind)
				VALUES (date_trunc('day', stale.ts_start at time zone Edge_tz) at time zone Edge_tz, stale.Edge_id, stale.source_id, 'd')
				ON CONFLICT (agg_kind, Edge_id, ts_start, source_id) DO NOTHING;
			WHEN 'd' THEN
				INSERT INTO eniwareagg.agg_stale_datum (ts_start, Edge_id, source_id, agg_kind)
				VALUES (date_trunc('month', stale.ts_start at time zone Edge_tz) at time zone Edge_tz, stale.Edge_id, stale.source_id, 'm')
				ON CONFLICT (agg_kind, Edge_id, ts_start, source_id) DO NOTHING;
			ELSE
				-- nothing
		END CASE;
	END IF;
	CLOSE curs;
	RETURN result;
END;
$BODY$;

DROP FUNCTION eniwareagg.find_running_datum(bigint,text[], timestamp with time zone);
CREATE OR REPLACE FUNCTION eniwareagg.find_running_datum(
    IN Edge bigint,
    IN sources text[],
    IN end_ts timestamp with time zone DEFAULT CURRENT_TIMESTAMP)
  RETURNS TABLE(
  	ts_start timestamp with time zone,
  	local_date timestamp without time zone,
  	Edge_id bigint,
  	source_id text,
  	jdata jsonb,
  	weight integer)
LANGUAGE sql
STABLE AS
$BODY$
	-- get the Edge TZ, falling back to UTC if not available so we always have a time zone even if Edge not found
	WITH Edgetz AS (
		SELECT n.Edge_id, COALESCE(l.time_zone, 'UTC') AS tz
		FROM eniwarenet.sn_Edge n
		LEFT OUTER JOIN eniwarenet.sn_loc l ON l.id = n.loc_id
		WHERE n.Edge_id = Edge
		UNION ALL
		SELECT Edge::bigint AS Edge_id, 'UTC'::character varying AS tz
		WHERE NOT EXISTS (SELECT Edge_id FROM eniwarenet.sn_Edge WHERE Edge_id = Edge)
	)
	SELECT d.ts_start, d.local_date, d.Edge_id, d.source_id, eniwareagg.jdata_from_datum(d),
		CAST(extract(epoch from (local_date + interval '1 month') - local_date) / 3600 AS integer) AS weight
	FROM eniwareagg.agg_datum_monthly d
	INNER JOIN Edgetz ON Edgetz.Edge_id = d.Edge_id
	WHERE d.ts_start < date_trunc('month', end_ts AT TIME ZONE Edgetz.tz) AT TIME ZONE Edgetz.tz
		AND d.source_id = ANY(sources)
	UNION ALL
	SELECT d.ts_start, d.local_date, d.Edge_id, d.source_id, eniwareagg.jdata_from_datum(d),
		24::integer as weight
	FROM eniwareagg.agg_datum_daily d
	INNER JOIN Edgetz ON Edgetz.Edge_id = d.Edge_id
	WHERE ts_start < date_trunc('day', end_ts AT TIME ZONE Edgetz.tz) AT TIME ZONE Edgetz.tz
		AND d.ts_start >= date_trunc('month', end_ts AT TIME ZONE Edgetz.tz) AT TIME ZONE Edgetz.tz
		AND d.source_id = ANY(sources)
	UNION ALL
	SELECT d.ts_start, d.local_date, d.Edge_id, d.source_id, eniwareagg.jdata_from_datum(d),
		1::INTEGER as weight
	FROM eniwareagg.agg_datum_hourly d
	INNER JOIN Edgetz ON Edgetz.Edge_id = d.Edge_id
	WHERE d.ts_start < date_trunc('hour', end_ts AT TIME ZONE Edgetz.tz) AT TIME ZONE Edgetz.tz
		AND d.ts_start >= date_trunc('day', end_ts AT TIME ZONE Edgetz.tz) AT TIME ZONE Edgetz.tz
		AND d.source_id = ANY(sources)
	UNION ALL
	SELECT ts_start, ts_start at time zone Edgetz.tz AS local_date, Edgetz.Edge_id, source_id, jdata, 1::integer as weight
	FROM eniwareagg.calc_datum_time_slots(
		Edge,
		sources,
		date_trunc('hour', end_ts),
		interval '1 hour',
		0,
		interval '1 hour')
	INNER JOIN Edgetz ON Edgetz.Edge_id = Edge_id
	ORDER BY ts_start, source_id
$BODY$;

DROP FUNCTION eniwareagg.calc_running_total(bigint,text[],timestamp with time zone,boolean);
CREATE OR REPLACE FUNCTION eniwareagg.calc_running_total(
	IN pk bigint,
	IN sources text[],
	IN end_ts timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
	IN loc_mode boolean DEFAULT FALSE)
RETURNS TABLE(source_id text, jdata jsonb)
LANGUAGE plv8
STABLE
ROWS 10 AS
$BODY$
'use strict';

var totalor = require('datum/totalor').default;

var query = (loc_mode === true
		? 'SELECT * FROM eniwareagg.find_running_loc_datum($1, $2, $3)'
		: 'SELECT * FROM eniwareagg.find_running_datum($1, $2, $3)'),
	stmt,
	cur,
	rec,
	helper = totalor(),
	aggResult,
	i;

stmt = plv8.prepare(query, ['bigint', 'text[]', 'timestamp with time zone']);
cur = stmt.cursor([pk, sources, end_ts]);

while ( rec = cur.fetch() ) {
	if ( !rec.jdata ) {
		continue;
	}
	helper.addDatumRecord(rec);
}

aggResult = helper.finish();
if ( Array.isArray(aggResult) ) {
	for ( i = 0; i < aggResult.length; i += 1 ) {
		plv8.return_next(aggResult[i]);
	}
}

cur.close();
stmt.free();

$BODY$;

DROP FUNCTION eniwareagg.calc_running_datum_total(bigint,text[],timestamp with time zone);
CREATE OR REPLACE FUNCTION eniwareagg.calc_running_datum_total(
	IN Edge bigint,
	IN sources text[],
	IN end_ts timestamp with time zone DEFAULT CURRENT_TIMESTAMP)
RETURNS TABLE(
	ts_start timestamp with time zone,
	local_date timestamp without time zone,
	Edge_id bigint,
	source_id text,
	jdata jsonb)
LANGUAGE sql
STABLE
ROWS 10 AS
$BODY$
	WITH Edgetz AS (
		SELECT n.Edge_id, COALESCE(l.time_zone, 'UTC') AS tz
		FROM eniwarenet.sn_Edge n
		LEFT OUTER JOIN eniwarenet.sn_loc l ON l.id = n.loc_id
		WHERE n.Edge_id = Edge
		UNION ALL
		SELECT Edge::bigint AS Edge_id, 'UTC'::character varying AS tz
		WHERE NOT EXISTS (SELECT Edge_id FROM eniwarenet.sn_Edge WHERE Edge_id = Edge)
	)
	SELECT end_ts, end_ts AT TIME ZONE Edgetz.tz AS local_date, Edge, r.source_id, r.jdata
	FROM eniwareagg.calc_running_total(
		Edge,
		sources,
		end_ts,
		FALSE
	) AS r
	INNER JOIN Edgetz ON Edgetz.Edge_id = Edge;
$BODY$;

DROP FUNCTION eniwareagg.find_most_recent_hourly(bigint, text[]);
CREATE OR REPLACE FUNCTION eniwareagg.find_most_recent_hourly(
	Edge bigint,
	sources text[] DEFAULT NULL)
  RETURNS SETOF eniwareagg.agg_datum_hourly AS
$BODY$
BEGIN
	IF sources IS NULL OR array_length(sources, 1) < 1 THEN
		RETURN QUERY
		WITH maxes AS (
			SELECT max(d.ts_start) as ts_start, d.source_id, Edge as Edge_id FROM eniwareagg.agg_datum_hourly d
			INNER JOIN (SELECT eniwaredatum.find_available_sources(Edge) AS source_id) AS s ON s.source_id = d.source_id
			WHERE d. Edge_id = Edge
			GROUP BY d.source_id
		)
		SELECT d.* FROM eniwareagg.agg_datum_hourly d
		INNER JOIN maxes ON maxes.Edge_id = d.Edge_id AND maxes.source_id = d.source_id AND maxes.ts_start = d.ts_start
		ORDER BY d.source_id ASC;
	ELSE
		RETURN QUERY
		WITH maxes AS (
			SELECT max(d.ts_start) as ts_start, d.source_id, Edge as Edge_id FROM eniwareagg.agg_datum_hourly d
			INNER JOIN (SELECT unnest(sources) AS source_id) AS s ON s.source_id = d.source_id
			WHERE d. Edge_id = Edge
			GROUP BY d.source_id
		)
		SELECT d.* FROM eniwareagg.agg_datum_hourly d
		INNER JOIN maxes ON maxes.Edge_id = d.Edge_id AND maxes.source_id = d.source_id AND maxes.ts_start = d.ts_start
		ORDER BY d.source_id ASC;
	END IF;
END;$BODY$
  LANGUAGE plpgsql STABLE
  ROWS 20;

DROP FUNCTION eniwareagg.find_most_recent_daily(bigint, text[]);
CREATE OR REPLACE FUNCTION eniwareagg.find_most_recent_daily(
	Edge bigint,
	sources text[] DEFAULT NULL)
  RETURNS SETOF eniwareagg.agg_datum_daily AS
$BODY$
BEGIN
	IF sources IS NULL OR array_length(sources, 1) < 1 THEN
		RETURN QUERY
		WITH maxes AS (
			SELECT max(d.ts_start) as ts_start, d.source_id, Edge as Edge_id FROM eniwareagg.agg_datum_daily d
			INNER JOIN (SELECT eniwaredatum.find_available_sources(Edge) AS source_id) AS s ON s.source_id = d.source_id
			WHERE d. Edge_id = Edge
			GROUP BY d.source_id
		)
		SELECT d.* FROM eniwareagg.agg_datum_daily d
		INNER JOIN maxes ON maxes.Edge_id = d.Edge_id AND maxes.source_id = d.source_id AND maxes.ts_start = d.ts_start
		ORDER BY d.source_id ASC;
	ELSE
		RETURN QUERY
		WITH maxes AS (
			SELECT max(d.ts_start) as ts_start, d.source_id, Edge as Edge_id FROM eniwareagg.agg_datum_daily d
			INNER JOIN (SELECT unnest(sources) AS source_id) AS s ON s.source_id = d.source_id
			WHERE d. Edge_id = Edge
			GROUP BY d.source_id
		)
		SELECT d.* FROM eniwareagg.agg_datum_daily d
		INNER JOIN maxes ON maxes.Edge_id = d.Edge_id AND maxes.source_id = d.source_id AND maxes.ts_start = d.ts_start
		ORDER BY d.source_id ASC;
	END IF;
END;$BODY$
  LANGUAGE plpgsql STABLE
  ROWS 20;

DROP FUNCTION eniwareagg.find_most_recent_monthly(bigint, text[]);
CREATE OR REPLACE FUNCTION eniwareagg.find_most_recent_monthly(
	Edge bigint,
	sources text[] DEFAULT NULL)
  RETURNS SETOF eniwareagg.agg_datum_monthly AS
$BODY$
BEGIN
	IF sources IS NULL OR array_length(sources, 1) < 1 THEN
		RETURN QUERY
		WITH maxes AS (
			SELECT max(d.ts_start) as ts_start, d.source_id, Edge as Edge_id FROM eniwareagg.agg_datum_monthly d
			INNER JOIN (SELECT eniwaredatum.find_available_sources(Edge) AS source_id) AS s ON s.source_id = d.source_id
			WHERE d. Edge_id = Edge
			GROUP BY d.source_id
		)
		SELECT d.* FROM eniwareagg.agg_datum_monthly d
		INNER JOIN maxes ON maxes.Edge_id = d.Edge_id AND maxes.source_id = d.source_id AND maxes.ts_start = d.ts_start
		ORDER BY d.source_id ASC;
	ELSE
		RETURN QUERY
		WITH maxes AS (
			SELECT max(d.ts_start) as ts_start, d.source_id, Edge as Edge_id FROM eniwareagg.agg_datum_monthly d
			INNER JOIN (SELECT unnest(sources) AS source_id) AS s ON s.source_id = d.source_id
			WHERE d. Edge_id = Edge
			GROUP BY d.source_id
		)
		SELECT d.* FROM eniwareagg.agg_datum_monthly d
		INNER JOIN maxes ON maxes.Edge_id = d.Edge_id AND maxes.source_id = d.source_id AND maxes.ts_start = d.ts_start
		ORDER BY d.source_id ASC;
	END IF;
END;$BODY$
  LANGUAGE plpgsql STABLE
  ROWS 20;
