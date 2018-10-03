/**
 * Return a valid minute-level time slot seconds value for an arbitrary input value.
 * This function is meant to validate and correct inappropriate input for minute level
 * time slot second values, which must be between 60 and 1800 and evenly divide into 1800.
 *
 * @param secs the seconds to validate
 * @returns integer seconds value, possibly different than the input seconds value
 */
CREATE OR REPLACE FUNCTION eniwareagg.slot_seconds(secs integer default 600)
  RETURNS integer
  LANGUAGE sql
  IMMUTABLE AS
$BODY$
	SELECT
	CASE
		WHEN secs < 60 OR secs > 1800 OR 1800 % secs <> 0 THEN 600
	ELSE
		secs
	END
$BODY$;

/**
 * Return a normalized minute time-slot timestamp for a given timestamp and slot interval.
 * This function returns the appropriate minute-level time aggregate <code>ts_start</code>
 * value for a given timestamp. For example passing <b>600</b> for <code>sec</code> will
 * return a timestamp who is truncated to <code>:00</code>, <code>:10</code>, <code>:20</code>,
 * <code>:30</code>, <code>:40</code>, or <code>:50</code>.
 *
 * @param ts the timestamp to normalize
 * @param sec the slot seconds
 * @returns normalized timestamp
 */
CREATE OR REPLACE FUNCTION eniwareagg.minute_time_slot(ts timestamp with time zone, sec integer default 600)
  RETURNS timestamp with time zone
  LANGUAGE sql
  IMMUTABLE AS
$BODY$
	SELECT date_trunc('hour', ts) + (
		ceil(extract('epoch' from ts) - extract('epoch' from date_trunc('hour', ts)))
		- ceil(extract('epoch' from ts))::bigint % sec
	) * interval '1 second'
$BODY$;

/**
 * Trigger that inserts a row into the <b>eniwareagg.agg_stale_datum<b> table based on
 * a change to a <b>eniwaredatum.da_datum</b> type row. The <b>agg_kind</b> column is
 * set to <code>h</code> and the <b>ts_start</b> column to the changed row's <b>ts</b>
 * timestamp, truncated to the <b>hour</b>. The changed row's <b>Edge_id</b> and
 * <b>source_id</b> columns are copied as-is. The trigger ignores any
 * a <code>unique_violation</code> exception thrown by the <code>INSERT</code>.
 */
CREATE OR REPLACE FUNCTION eniwaredatum.trigger_agg_stale_datum()
  RETURNS trigger AS
$BODY$
DECLARE
	datum_ts timestamp with time zone;
	neighbor eniwaredatum.da_datum;
BEGIN
	CASE TG_OP
		WHEN 'INSERT', 'UPDATE' THEN
			datum_ts := NEW.ts;
			BEGIN
				INSERT INTO eniwareagg.agg_stale_datum (ts_start, Edge_id, source_id, agg_kind)
				VALUES (date_trunc('hour', datum_ts), NEW.Edge_id, NEW.source_id, 'h');
			EXCEPTION WHEN unique_violation THEN
				-- Nothing to do, just continue
			END;

			SELECT * FROM eniwaredatum.da_datum d
			WHERE d.ts < datum_ts
				AND d.ts > datum_ts - interval '1 hour'
				AND d.Edge_id = NEW.Edge_id
				AND d.source_id = NEW.source_id
			ORDER BY d.ts DESC
			LIMIT 1
			INTO neighbor;
		ELSE
			datum_ts := OLD.ts;
			BEGIN
				INSERT INTO eniwareagg.agg_stale_datum (ts_start, Edge_id, source_id, agg_kind)
				VALUES (date_trunc('hour', datum_ts), OLD.Edge_id, OLD.source_id, 'h');
			EXCEPTION WHEN unique_violation THEN
				-- Nothing to do, just continue
			END;

			SELECT * FROM eniwaredatum.da_datum d
			WHERE d.ts < datum_ts
				AND d.ts > datum_ts - interval '1 hour'
				AND d.Edge_id = OLD.Edge_id
				AND d.source_id = OLD.source_id
			ORDER BY d.ts DESC
			LIMIT 1
			INTO neighbor;
	END CASE;
	IF FOUND AND neighbor.ts < date_trunc('hour', datum_ts) THEN
		-- the previous record for this source falls on the previous hour; we have to mark that hour as stale as well
		BEGIN
			INSERT INTO eniwareagg.agg_stale_datum (ts_start, Edge_id, source_id, agg_kind)
			VALUES (date_trunc('hour', neighbor.ts), neighbor.Edge_id, neighbor.source_id, 'h');
		EXCEPTION WHEN unique_violation THEN
			-- Nothing to do, just continue
		END;
	END IF;
	CASE TG_OP
		WHEN 'INSERT', 'UPDATE' THEN
			SELECT * FROM eniwaredatum.da_datum d
			WHERE d.ts > datum_ts
				AND d.ts < datum_ts + interval '1 hour'
				AND d.Edge_id = NEW.Edge_id
				AND d.source_id = NEW.source_id
			ORDER BY d.ts ASC
			LIMIT 1
			INTO neighbor;
		ELSE
			SELECT * FROM eniwaredatum.da_datum d
			WHERE d.ts > datum_ts
				AND d.ts < datum_ts + interval '1 hour'
				AND d.Edge_id = OLD.Edge_id
				AND d.source_id = OLD.source_id
			ORDER BY d.ts ASC
			LIMIT 1
			INTO neighbor;
	END CASE;
	IF FOUND AND neighbor.ts > date_trunc('hour', datum_ts) THEN
		-- the next record for this source falls on the next hour; we have to mark that hour as stale as well
		BEGIN
			INSERT INTO eniwareagg.agg_stale_datum (ts_start, Edge_id, source_id, agg_kind)
			VALUES (date_trunc('hour', neighbor.ts), neighbor.Edge_id, neighbor.source_id, 'h');
		EXCEPTION WHEN unique_violation THEN
			-- Nothing to do, just continue
		END;
	END IF;
	CASE TG_OP
		WHEN 'INSERT', 'UPDATE' THEN
			RETURN NEW;
		ELSE
			RETURN OLD;
	END CASE;
END;$BODY$
  LANGUAGE plpgsql VOLATILE;


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


/**
 * Dynamically calculate minute-level time slot aggregate values for a Edge and set of source IDs.
 *
 * @param Edge				Edge ID
 * @param source			array of source IDs
 * @param start_ts			the start timestamp
 * @param end_ts			the end timestamp
 * @param slotsecs			the number of seconds per time slot, e.g. 600 == 10 minutes.
 */
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


/**
 * Find rows in the <b>eniwaredatum.da_datum</b> table necessary to calculate aggregate
 * data for a specific duration of time, Edge, and set of sources. This function will return
 * all available rows within the specified duration, possibly with some rows <em>before</em> or
 * <em>after</em> the duration to enable calculating the actual aggregate over the duration.
 *
 * @param Edge The ID of the Edge to search for.
 * @param sources An array of one or more source IDs to search for, any of which may match.
 * @param start_ts The start time of the desired time duration.
 * @param span The interval of the time duration, which starts from <b>start_ts</b>.
 * @param slotsecs The number of seconds per minute time slot to assign output rows to. Must be
 *                 between 60 and 1800 and evenly divide into 1800.
 * @param tolerance An interval representing the maximum amount of time before between, and after
 *                  rows with the same source ID are allowed to be considered <em>consecutive</em>
 *                  for the purposes of calculating the overall aggregate of the time duration.
 *
 * @out ts The <b>eniwaredatum.da_datum.ts</b> value.
 * @out source_id The <b>eniwaredatum.da_datum.source_id</b> value.
 * @out jdata The <b>eniwaredatum.da_datum.jdata</b> value.
 * @returns one or more records
 */
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


/**
 * Calculate hour-of-day aggregate values for a Edge and set of source IDs
 * and one specific general data value. Note that the `path` parameter currently only
 * supports an array with exactly two elements.
 *
 * @param Edge				Edge ID
 * @param source			array of source IDs
 * @param path				the JSON path to the value to extract, e.g. ['i','watts']
 * @param start_ts			the start timestamp (defaults to SN epoch)
 * @param end_ts			the end timestamp (defaults to CURRENT_TIMESTAMP)
 */
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


/**
 * Calculate seasonal hour-of-day aggregate values for a Edge and set of source IDs
 * and one specific general data value. Note that the `path` parameter currently only
 * supports an array with exactly two elements.
 *
 * @param Edge				Edge ID
 * @param source			array of source IDs
 * @param path				the JSON path to the value to extract, e.g. ['i','watts']
 * @param start_ts			the start timestamp (defaults to SN epoch)
 * @param end_ts			the end timestamp (defaults to CURRENT_TIMESTAMP)
 */
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


/**
 * Calculate day-of-week aggregate values for a Edge and set of source IDs
 * and one specific general data value. Note that the `path` parameter currently only
 * supports an array with exactly two elements.
 *
 * @param Edge				Edge ID
 * @param source			array of source IDs
 * @param path				the JSON path to the value to extract, e.g. ['i','watts']
 * @param start_ts			the start timestamp (defaults to SN epoch)
 * @param end_ts			the end timestamp (defaults to CURRENT_TIMESTAMP)
 */
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


/**
 * Calculate seasonal day-of-week aggregate values for a Edge and set of source IDs
 * and one specific general data value. Note that the `path` parameter currently only
 * supports an array with exactly two elements.
 *
 * @param Edge				Edge ID
 * @param source			array of source IDs
 * @param path				the JSON path to the value to extract, e.g. ['i','watts']
 * @param start_ts			the start timestamp (defaults to SN epoch)
 * @param end_ts			the end timestamp (defaults to CURRENT_TIMESTAMP)
 */
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

CREATE OR REPLACE FUNCTION eniwareagg.process_agg_stale_datum(kind char, max integer)
  RETURNS INTEGER AS
$BODY$
DECLARE
	one_result INTEGER := 1;
	total_result INTEGER := 0;
BEGIN
	LOOP
		IF one_result < 1 OR (max > -1 AND total_result >= max) THEN
			EXIT;
		END IF;
		SELECT eniwareagg.process_one_agg_stale_datum(kind) INTO one_result;
		total_result := total_result + one_result;
	END LOOP;
	RETURN total_result;
END;$BODY$
  LANGUAGE plpgsql VOLATILE;

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


/**
 * Find aggregated data for a given Edge over all time up to an optional end date (else the current date).
 * The purpose of this function is to find as few as possible records of already aggregated data
 * so they can be combined into a single running total aggregate result. Each result row includes a
 * <b>weight</b> column that represents the number of hours the given row spans. This number can be
 * used to calculate a weighted average for all values over the entire result set.
 *
 * @param Edge    The ID of the Edge to query for.
 * @param sources An array of source IDs to query for.
 * @param end_ts  An optional date to limit the results to. If not provided the current date is used.
 */
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


/**
 * Calculate a running total for either a Edge or location ID. There will
 * be at most one result row per source ID in the returned data.
 *
 * @param pk       The ID of the Edge or location to query for.
 * @param sources  An array of source IDs to query for.
 * @param end_ts   An optional date to limit the results to. If not provided the current date is used.
 * @param loc_mode If TRUE then location datum are queried, otherwise Edge datum. Defaults to FALSE.
 */
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


/**
 * Find the minimum/maximum available dates for audit datum.
 *
 * The returned parameters include <code>ts_start</code> and <code>ts_end</code> values
 * for the date range. Additionally the <code>Edge_tz</code> and <code>Edge_tz_offset</code>
 * values will provide the time zone of the Edge, which defaults to UTC if not available.
 *
 * @param Edge The ID of the Edge to query for.
 * @param src  An optional source ID to query for. Pass <code>NULL</code> for all sources.
 */
CREATE OR REPLACE FUNCTION eniwareagg.find_audit_datum_interval(
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
			SELECT min(a.ts_start) FROM eniwareagg.aud_datum_hourly a WHERE Edge_id = Edge
			INTO ts_start;
		ELSE
			SELECT min(a.ts_start) FROM eniwareagg.aud_datum_hourly a WHERE Edge_id = Edge AND source_id = src
			INTO ts_start;
	END CASE;

	CASE
		WHEN src IS NULL THEN
			SELECT max(a.ts_start) FROM eniwareagg.aud_datum_hourly a WHERE Edge_id = Edge
			INTO ts_end;
		ELSE
			SELECT max(a.ts_start) FROM eniwareagg.aud_datum_hourly a WHERE Edge_id = Edge AND source_id = src
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
