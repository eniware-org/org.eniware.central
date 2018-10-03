
DROP FUNCTION IF EXISTS eniwareagg.find_datum_for_minute_time_slots(bigint, text[], timestamp with time zone, interval, integer, interval);
DROP FUNCTION IF EXISTS eniwareagg.find_datum_for_time_slot(bigint, text[], timestamp with time zone, interval, interval);

/**
 * Find matching datum rows for aggregation purposes across a specific time span.
 * The query can return adjacent rows before or after the given span so that
 * accumulating values can be calculated from the results.
 *
 * @param Edge				Edge ID
 * @param sources			array of source IDs
 * @param start_ts			the start timestamp
 * @param span				the length of time from start_ts to use as the end timestamp
 * @param tolerance			the number of milliseconds tolerance before/after span to
 *                          look for adjacent rows
 */
CREATE OR REPLACE FUNCTION eniwareagg.find_datum_for_time_span(
    IN Edge bigint,
    IN sources text[],
    IN start_ts timestamp with time zone,
    IN span interval,
    IN tolerance interval DEFAULT '01:00:00'::interval)
  RETURNS TABLE(ts timestamp with time zone, source_id text, jdata json) AS
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
		d.jdata as jdata
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


/**
 * Dynamically calculate time slot aggregate values for a Edge and set of source IDs.
 * If <code>slotsecs</code> is between 60 and 1800 then the the results will include
 * corresponding minute-level time slots per source ID. Otherwise at most a single
 * row per source ID will be returned.
 *
 * @param Edge				Edge ID
 * @param sources			array of source IDs
 * @param start_ts			the start timestamp
 * @param span				the length of time from start_ts to use as the end timestamp
 * @param slotsecs			the number of seconds per time slot, between 60 and 1800, e.g.
 *                          600 == 10 minutes (the default), 0 == disable
 * @param tolerance			the number of milliseconds tolerance before/after time slots to
 *                          look for adjacent rows
 */
CREATE OR REPLACE FUNCTION eniwareagg.calc_datum_time_slots(
	IN Edge bigint,
	IN sources text[],
	IN start_ts timestamp with time zone,
	IN span interval,
	IN slotsecs integer DEFAULT 600,
	IN tolerance interval DEFAULT interval '1 hour')
  RETURNS TABLE(ts_start timestamp with time zone, source_id text, jdata json) LANGUAGE plv8 AS
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


DROP FUNCTION IF EXISTS eniwareagg.find_loc_datum_for_minute_time_slots(bigint, text[], timestamp with time zone, interval, integer, interval);
DROP FUNCTION IF EXISTS eniwareagg.find_loc_datum_for_time_slot(bigint, text[], timestamp with time zone, interval, interval);

/**
 * Find matching location datum rows for aggregation purposes across a specific time
 * span. The query can return adjacent rows before or after the given span so that
 * accumulating values can be calculated from the results.
 *
 * @param loc				location ID
 * @param sources			array of source IDs
 * @param start_ts			the start timestamp
 * @param span				the length of time from start_ts to use as the end timestamp
 * @param tolerance			the number of milliseconds tolerance before/after span to
 *                          look for adjacent rows
 */
CREATE OR REPLACE FUNCTION eniwareagg.find_loc_datum_for_time_span(
    IN loc bigint,
    IN sources text[],
    IN start_ts timestamp with time zone,
    IN span interval,
    IN tolerance interval DEFAULT '01:00:00'::interval)
  RETURNS TABLE(ts timestamp with time zone, source_id text, jdata json) AS
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
		d.jdata as jdata
	FROM eniwaredatum.da_loc_datum d
	WHERE d.loc_id = loc
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


/**
 * Dynamically calculate time slot aggregate values for a location and set of source IDs.
 * If <code>slotsecs</code> is between 60 and 1800 then the the results will include
 * corresponding minute-level time slots per source ID. Otherwise at most a single
 * row per source ID will be returned.
 *
 * @param loc				location ID
 * @param sources			array of source IDs
 * @param start_ts			the start timestamp
 * @param span				the length of time from start_ts to use as the end timestamp
 * @param slotsecs			the number of seconds per time slot, between 60 and 1800, e.g.
 *                          600 == 10 minutes (the default), 0 == disable
 * @param tolerance			the number of milliseconds tolerance before/after time slots to
 *                          look for adjacent rows
 */
CREATE OR REPLACE FUNCTION eniwareagg.calc_loc_datum_time_slots(
	IN loc bigint,
	IN sources text[],
	IN start_ts timestamp with time zone,
	IN span interval,
	IN slotsecs integer DEFAULT 600,
	IN tolerance interval DEFAULT interval '1 hour')
  RETURNS TABLE(ts_start timestamp with time zone, source_id text, jdata json) LANGUAGE plv8 AS
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
		'SELECT ts, eniwareagg.minute_time_slot(ts, '+slotsecs+') as ts_start, source_id, jdata FROM eniwareagg.find_loc_datum_for_time_span($1, $2, $3, $4, $5)',
		['bigint', 'text[]', 'timestamp with time zone', 'interval', 'interval']);
	helper = slotAggregator({
		startTs : start_ts.getTime(),
		endTs : endTs,
		slotSecs : slotsecs
	});
} else {
	stmt = plv8.prepare(
		'SELECT ts, source_id, jdata FROM eniwareagg.find_loc_datum_for_time_span($1, $2, $3, $4, $5)',
		['bigint', 'text[]', 'timestamp with time zone', 'interval', 'interval']);
	helper = aggregator({
		startTs : start_ts.getTime(),
		endTs : endTs,
	});
}

cur = stmt.cursor([loc, sources, start_ts, span, tolerance]);

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

DROP FUNCTION IF EXISTS eniwareagg.calc_running_total(text, bigint, text[], timestamp with time zone);

CREATE OR REPLACE FUNCTION eniwareagg.calc_running_total(
	IN pk bigint,
	IN sources text[],
	IN end_ts timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
	IN loc_mode boolean DEFAULT FALSE)
RETURNS TABLE(source_id text, jdata json)
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


CREATE OR REPLACE FUNCTION eniwareagg.find_running_datum(
    IN Edge bigint,
    IN sources text[],
    IN end_ts timestamp with time zone DEFAULT CURRENT_TIMESTAMP)
  RETURNS TABLE(ts_start timestamp with time zone, local_date timestamp without time zone, Edge_id bigint, source_id text, jdata json, weight integer)
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
	SELECT d.ts_start, d.local_date, d.Edge_id, d.source_id, d.jdata, CAST(extract(epoch from (local_date + interval '1 month') - local_date) / 3600 AS integer) AS weight
	FROM eniwareagg.agg_datum_monthly d
	INNER JOIN Edgetz ON Edgetz.Edge_id = d.Edge_id
	WHERE d.ts_start < date_trunc('month', end_ts AT TIME ZONE Edgetz.tz) AT TIME ZONE Edgetz.tz
		AND d.source_id = ANY(sources)
	UNION ALL
	SELECT d.ts_start, d.local_date, d.Edge_id, d.source_id, d.jdata, 24::integer as weight
	FROM eniwareagg.agg_datum_daily d
	INNER JOIN Edgetz ON Edgetz.Edge_id = d.Edge_id
	WHERE ts_start < date_trunc('day', end_ts AT TIME ZONE Edgetz.tz) AT TIME ZONE Edgetz.tz
		AND d.ts_start >= date_trunc('month', end_ts AT TIME ZONE Edgetz.tz) AT TIME ZONE Edgetz.tz
		AND d.source_id = ANY(sources)
	UNION ALL
	SELECT d.ts_start, d.local_date, d.Edge_id, d.source_id, d.jdata, 1::INTEGER as weight
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


/**
 * Calculate a running average of datum up to a specific end date. There will
 * be at most one result row per source ID in the returned data.
 *
 * @param Edge    The ID of the Edge to query for.
 * @param sources An array of source IDs to query for.
 * @param end_ts  An optional date to limit the results to. If not provided the current date is used.
 */
CREATE OR REPLACE FUNCTION eniwareagg.calc_running_datum_total(
	IN Edge bigint,
	IN sources text[],
	IN end_ts timestamp with time zone DEFAULT CURRENT_TIMESTAMP)
RETURNS TABLE(ts_start timestamp with time zone, local_date timestamp without time zone, Edge_id bigint, source_id text, jdata json)
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


CREATE OR REPLACE FUNCTION eniwareagg.find_running_loc_datum(
	IN loc bigint,
	IN sources text[],
	IN end_ts timestamp with time zone DEFAULT CURRENT_TIMESTAMP)
RETURNS TABLE(ts_start timestamp with time zone, local_date timestamp without time zone, loc_id bigint, source_id text, jdata json, weight integer)
LANGUAGE sql
STABLE AS
$BODY$
	WITH loctz AS (
		SELECT l.id as loc_id, COALESCE(l.time_zone, 'UTC') AS tz
		FROM eniwarenet.sn_loc l
		WHERE l.id = loc
		UNION ALL
		SELECT loc::bigint AS loc_id, 'UTC'::character varying AS tz
		WHERE NOT EXISTS (SELECT id AS loc_id FROM eniwarenet.sn_loc WHERE id = loc)
	)
	SELECT d.ts_start, d.local_date, d.loc_id, d.source_id, d.jdata, CAST(extract(epoch from (local_date + interval '1 month') - local_date) / 3600 AS integer) AS weight
	FROM eniwareagg.agg_loc_datum_monthly d
	INNER JOIN loctz ON loctz.loc_id = d.loc_id
	WHERE d.ts_start < date_trunc('month', end_ts AT TIME ZONE loctz.tz) AT TIME ZONE loctz.tz
		AND d.source_id = ANY(sources)
	UNION ALL
	SELECT d.ts_start, d.local_date, d.loc_id, d.source_id, d.jdata, 24::integer as weight
	FROM eniwareagg.agg_loc_datum_daily d
	INNER JOIN loctz ON loctz.loc_id = d.loc_id
	WHERE ts_start < date_trunc('day', end_ts AT TIME ZONE loctz.tz) AT TIME ZONE loctz.tz
		AND d.ts_start >= date_trunc('month', end_ts AT TIME ZONE loctz.tz) AT TIME ZONE loctz.tz
		AND d.source_id = ANY(sources)
	UNION ALL
	SELECT d.ts_start, d.local_date, d.loc_id, d.source_id, d.jdata, 1::INTEGER as weight
	FROM eniwareagg.agg_loc_datum_hourly d
	INNER JOIN loctz ON loctz.loc_id = d.loc_id
	WHERE d.ts_start < date_trunc('hour', end_ts AT TIME ZONE loctz.tz) AT TIME ZONE loctz.tz
		AND d.ts_start >= date_trunc('day', end_ts AT TIME ZONE loctz.tz) AT TIME ZONE loctz.tz
		AND d.source_id = ANY(sources)
	UNION ALL
	SELECT ts_start, ts_start at time zone loctz.tz AS local_date, loctz.loc_id, source_id, jdata, 1::integer as weight
	FROM eniwareagg.calc_loc_datum_time_slots(
		loc,
		sources,
		date_trunc('hour', end_ts),
		interval '1 hour',
		0,
		interval '1 hour')
	INNER JOIN loctz ON loctz.loc_id = loc_id
	ORDER BY ts_start, source_id
$BODY$;


/**
 * Calculate a running average of location datum up to a specific end date. There will
 * be at most one result row per source ID in the returned data.
 *
 * @param loc     The ID of the location to query for.
 * @param sources An array of source IDs to query for.
 * @param end_ts  An optional date to limit the results to. If not provided the current date is used.
 */
CREATE OR REPLACE FUNCTION eniwareagg.calc_running_loc_datum_total(
	IN loc bigint,
	IN sources text[],
	IN end_ts timestamp with time zone DEFAULT CURRENT_TIMESTAMP)
RETURNS TABLE(ts_start timestamp with time zone, local_date timestamp without time zone, loc_id bigint, source_id text, jdata json)
LANGUAGE sql
STABLE
ROWS 10 AS
$BODY$
	WITH loctz AS (
		SELECT l.id as loc_id, COALESCE(l.time_zone, 'UTC') AS tz
		FROM eniwarenet.sn_loc l
		WHERE l.id = loc
		UNION ALL
		SELECT loc::bigint AS loc_id, 'UTC'::character varying AS tz
		WHERE NOT EXISTS (SELECT id AS loc_id FROM eniwarenet.sn_loc WHERE id = loc)
	)
	SELECT end_ts, end_ts AT TIME ZONE loctz.tz AS local_date, loc, r.source_id, r.jdata
	FROM eniwareagg.calc_running_total(
		loc,
		sources,
		end_ts,
		TRUE
	) AS r
	INNER JOIN loctz ON loctz.loc_id = loc;
$BODY$;
