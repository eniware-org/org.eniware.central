-- want efficient ORDER BY on kind, ts_start, Edge_id, source_id
CREATE UNIQUE INDEX IF NOT EXISTS agg_stale_datum_pkey_new ON eniwareagg.agg_stale_datum (agg_kind, ts_start, Edge_id, source_id);
ALTER TABLE eniwareagg.agg_stale_datum DROP CONSTRAINT agg_stale_datum_pkey;
ALTER TABLE eniwareagg.agg_stale_datum ADD CONSTRAINT agg_stale_datum_pkey PRIMARY KEY USING INDEX agg_stale_datum_pkey_new;

-- want efficient ORDER BY on kind, ts_start, loc_id, source_id
CREATE UNIQUE INDEX IF NOT EXISTS agg_stale_loc_datum_pkey_new ON eniwareagg.agg_stale_loc_datum (agg_kind, ts_start, loc_id, source_id);
ALTER TABLE eniwareagg.agg_stale_loc_datum DROP CONSTRAINT agg_stale_loc_datum_pkey;
ALTER TABLE eniwareagg.agg_stale_loc_datum ADD CONSTRAINT agg_stale_loc_datum_pkey PRIMARY KEY USING INDEX agg_stale_loc_datum_pkey_new;

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
	agg_json json := NULL;
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
						ts_start, local_date, Edge_id, source_id, jdata)
					VALUES (
						stale.ts_start,
						stale.ts_start at time zone Edge_tz,
						stale.Edge_id,
						stale.source_id,
						agg_json
					)
					ON CONFLICT (Edge_id, ts_start, source_id) DO UPDATE
					SET jdata = EXCLUDED.jdata;
				WHEN 'd' THEN
					INSERT INTO eniwareagg.agg_datum_daily (
						ts_start, local_date, Edge_id, source_id, jdata)
					VALUES (
						stale.ts_start,
						CAST(stale.ts_start at time zone Edge_tz AS DATE),
						stale.Edge_id,
						stale.source_id,
						agg_json
					)
					ON CONFLICT (Edge_id, ts_start, source_id) DO UPDATE
					SET jdata = EXCLUDED.jdata;
				ELSE
					INSERT INTO eniwareagg.agg_datum_monthly (
						ts_start, local_date, Edge_id, source_id, jdata)
					VALUES (
						stale.ts_start,
						CAST(stale.ts_start at time zone Edge_tz AS DATE),
						stale.Edge_id,
						stale.source_id,
						agg_json
					)
					ON CONFLICT (Edge_id, ts_start, source_id) DO UPDATE
					SET jdata = EXCLUDED.jdata;
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

CREATE OR REPLACE FUNCTION eniwareagg.process_one_agg_stale_loc_datum(kind char)
  RETURNS integer LANGUAGE plpgsql VOLATILE AS
$BODY$
DECLARE
	stale record;
	curs CURSOR FOR SELECT * FROM eniwareagg.agg_stale_loc_datum
			WHERE agg_kind = kind
			ORDER BY ts_start ASC, created ASC, loc_id ASC, source_id ASC
			LIMIT 1
			FOR UPDATE SKIP LOCKED;
	agg_span interval;
	agg_json json := NULL;
	loc_tz text := 'UTC';
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
		-- get the loc TZ for local date/time
		SELECT l.time_zone FROM eniwarenet.sn_loc l
		WHERE l.id = stale.loc_id
		INTO loc_tz;

		IF NOT FOUND THEN
			RAISE NOTICE 'Edge % has no time zone, will use UTC.', stale.loc_id;
			loc_tz := 'UTC';
		END IF;

		SELECT jdata FROM eniwareagg.calc_loc_datum_time_slots(stale.loc_id, ARRAY[stale.source_id::text],
			stale.ts_start, agg_span, 0, interval '1 hour')
		INTO agg_json;
		IF agg_json IS NULL THEN
			CASE kind
				WHEN 'h' THEN
					DELETE FROM eniwareagg.agg_loc_datum_hourly
					WHERE loc_id = stale.loc_id
						AND source_id = stale.source_id
						AND ts_start = stale.ts_start;
				WHEN 'd' THEN
					DELETE FROM eniwareagg.agg_loc_datum_daily
					WHERE loc_id = stale.loc_id
						AND source_id = stale.source_id
						AND ts_start = stale.ts_start;
				ELSE
					DELETE FROM eniwareagg.agg_loc_datum_monthly
					WHERE loc_id = stale.loc_id
						AND source_id = stale.source_id
						AND ts_start = stale.ts_start;
			END CASE;
		ELSE
			CASE kind
				WHEN 'h' THEN
					INSERT INTO eniwareagg.agg_loc_datum_hourly (
						ts_start, local_date, loc_id, source_id, jdata)
					VALUES (
						stale.ts_start,
						stale.ts_start at time zone loc_tz,
						stale.loc_id,
						stale.source_id,
						agg_json
					)
					ON CONFLICT (loc_id, ts_start, source_id) DO UPDATE
					SET jdata = EXCLUDED.jdata;
				WHEN 'd' THEN
					INSERT INTO eniwareagg.agg_loc_datum_daily (
						ts_start, local_date, loc_id, source_id, jdata)
					VALUES (
						stale.ts_start,
						CAST(stale.ts_start at time zone loc_tz AS DATE),
						stale.loc_id,
						stale.source_id,
						agg_json
					)
					ON CONFLICT (loc_id, ts_start, source_id) DO UPDATE
					SET jdata = EXCLUDED.jdata;
				ELSE
					INSERT INTO eniwareagg.agg_loc_datum_monthly (
						ts_start, local_date, loc_id, source_id, jdata)
					VALUES (
						stale.ts_start,
						CAST(stale.ts_start at time zone loc_tz AS DATE),
						stale.loc_id,
						stale.source_id,
						agg_json
					)
					ON CONFLICT (loc_id, ts_start, source_id) DO UPDATE
					SET jdata = EXCLUDED.jdata;
			END CASE;
		END IF;
		DELETE FROM eniwareagg.agg_stale_loc_datum WHERE CURRENT OF curs;
		result := 1;

		-- now make sure we recalculate the next aggregate level by submitting a stale record for the next level
		CASE kind
			WHEN 'h' THEN
				INSERT INTO eniwareagg.agg_stale_loc_datum (ts_start, loc_id, source_id, agg_kind)
				VALUES (date_trunc('day', stale.ts_start at time zone loc_tz) at time zone loc_tz, stale.loc_id, stale.source_id, 'd')
				ON CONFLICT (agg_kind, loc_id, ts_start, source_id) DO NOTHING;
			WHEN 'd' THEN
				INSERT INTO eniwareagg.agg_stale_loc_datum (ts_start, loc_id, source_id, agg_kind)
				VALUES (date_trunc('month', stale.ts_start at time zone loc_tz) at time zone loc_tz, stale.loc_id, stale.source_id, 'm')
				ON CONFLICT (agg_kind, loc_id, ts_start, source_id) DO NOTHING;
			ELSE
				-- nothing
		END CASE;
	END IF;
	CLOSE curs;
	RETURN result;
END;
$BODY$;
