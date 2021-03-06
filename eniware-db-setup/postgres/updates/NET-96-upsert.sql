CREATE OR REPLACE FUNCTION eniwareagg.process_one_agg_stale_datum(kind char)
  RETURNS integer LANGUAGE plpgsql VOLATILE AS
$BODY$
DECLARE
	stale record;
	curs CURSOR FOR SELECT * FROM eniwareagg.agg_stale_datum
			WHERE agg_kind = kind
			--ORDER BY ts_start ASC, created ASC, Edge_id ASC, source_id ASC
			LIMIT 1
			FOR UPDATE;
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
			--ORDER BY ts_start ASC, created ASC, loc_id ASC, source_id ASC
			LIMIT 1
			FOR UPDATE;
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

-- upsert does not work on deferrable constraints, so make new non-deferrable ones
CREATE UNIQUE INDEX CONCURRENTLY IF NOT EXISTS da_datum_pkey_new ON eniwaredatum.da_datum (Edge_id, ts, source_id);
ALTER TABLE eniwaredatum.da_datum DROP CONSTRAINT da_datum_pkey;
ALTER TABLE eniwaredatum.da_datum ADD CONSTRAINT da_datum_pkey PRIMARY KEY USING INDEX da_datum_pkey_new NOT DEFERRABLE;

CREATE UNIQUE INDEX CONCURRENTLY IF NOT EXISTS aud_datum_hourly_pkey_new ON eniwareagg.aud_datum_hourly (Edge_id, ts_start, source_id);
ALTER TABLE eniwareagg.aud_datum_hourly DROP CONSTRAINT aud_datum_hourly_pkey;
ALTER TABLE eniwareagg.aud_datum_hourly ADD CONSTRAINT aud_datum_hourly_pkey PRIMARY KEY USING INDEX aud_datum_hourly_pkey_new NOT DEFERRABLE;

CREATE OR REPLACE FUNCTION eniwaredatum.store_datum(
	cdate eniwarecommon.ts,
	Edge eniwarecommon.Edge_id,
	src eniwarecommon.source_id,
	pdate eniwarecommon.ts,
	jdata text)
  RETURNS void LANGUAGE plpgsql VOLATILE AS
$BODY$
DECLARE
	ts_crea eniwarecommon.ts := COALESCE(cdate, now());
	ts_post eniwarecommon.ts := COALESCE(pdate, now());
	jdata_json json := jdata::json;
	jdata_prop_count integer := eniwaredatum.datum_prop_count(jdata_json);
	ts_post_hour timestamp with time zone := date_trunc('hour', ts_post);
BEGIN
	INSERT INTO eniwaredatum.da_datum(ts, Edge_id, source_id, posted, jdata)
	VALUES (ts_crea, Edge, src, ts_post, jdata_json)
	ON CONFLICT (Edge_id, ts, source_id) DO UPDATE
	SET jdata = EXCLUDED.jdata, posted = EXCLUDED.posted;

	INSERT INTO eniwareagg.aud_datum_hourly (
		ts_start, Edge_id, source_id, prop_count)
	VALUES (ts_post_hour, Edge, src, jdata_prop_count)
	ON CONFLICT (Edge_id, ts_start, source_id) DO UPDATE
	SET prop_count = aud_datum_hourly.prop_count + EXCLUDED.prop_count;
END;
$BODY$;

-- upsert does not work on deferrable constraints, so make new non-deferrable ones
CREATE UNIQUE INDEX CONCURRENTLY IF NOT EXISTS da_loc_datum_pkey_new ON eniwaredatum.da_loc_datum (loc_id, ts, source_id);
ALTER TABLE eniwaredatum.da_loc_datum DROP CONSTRAINT da_loc_datum_pkey;
ALTER TABLE eniwaredatum.da_loc_datum ADD CONSTRAINT da_loc_datum_pkey PRIMARY KEY USING INDEX da_loc_datum_pkey_new NOT DEFERRABLE;

CREATE UNIQUE INDEX CONCURRENTLY IF NOT EXISTS aud_loc_datum_hourly_pkey_new ON eniwareagg.aud_loc_datum_hourly (loc_id, ts_start, source_id);
ALTER TABLE eniwareagg.aud_loc_datum_hourly DROP CONSTRAINT aud_loc_datum_hourly_pkey;
ALTER TABLE eniwareagg.aud_loc_datum_hourly ADD CONSTRAINT aud_loc_datum_hourly_pkey PRIMARY KEY USING INDEX aud_loc_datum_hourly_pkey_new NOT DEFERRABLE;

CREATE OR REPLACE FUNCTION eniwaredatum.store_loc_datum(
	cdate eniwarecommon.ts,
	loc eniwarecommon.loc_id,
	src eniwarecommon.source_id,
	pdate eniwarecommon.ts,
	jdata text)
  RETURNS void LANGUAGE plpgsql VOLATILE AS
$BODY$
DECLARE
	ts_crea eniwarecommon.ts := COALESCE(cdate, now());
	ts_post eniwarecommon.ts := COALESCE(pdate, now());
	jdata_json json := jdata::json;
	jdata_prop_count integer := eniwaredatum.datum_prop_count(jdata_json);
	ts_post_hour timestamp with time zone := date_trunc('hour', ts_post);
BEGIN
	INSERT INTO eniwaredatum.da_loc_datum(ts, loc_id, source_id, posted, jdata)
	VALUES (ts_crea, loc, src, ts_post, jdata_json)
	ON CONFLICT (loc_id, ts, source_id) DO UPDATE
	SET jdata = EXCLUDED.jdata, posted = EXCLUDED.posted;

	INSERT INTO eniwareagg.aud_loc_datum_hourly (
		ts_start, loc_id, source_id, prop_count)
	VALUES (ts_post_hour, loc, src, jdata_prop_count)
	ON CONFLICT (loc_id, ts_start, source_id) DO UPDATE
	SET prop_count = aud_loc_datum_hourly.prop_count + EXCLUDED.prop_count;
END;
$BODY$;

-- upsert does not work on deferrable constraints, so make new non-deferrable ones
CREATE UNIQUE INDEX CONCURRENTLY IF NOT EXISTS sn_Edge_meta_pkey_new ON eniwarenet.sn_Edge_meta (Edge_id);
ALTER TABLE eniwarenet.sn_Edge_meta DROP CONSTRAINT sn_Edge_meta_pkey;
ALTER TABLE eniwarenet.sn_Edge_meta ADD CONSTRAINT sn_Edge_meta_pkey PRIMARY KEY USING INDEX sn_Edge_meta_pkey_new NOT DEFERRABLE;

CREATE OR REPLACE FUNCTION eniwarenet.store_Edge_meta(
	cdate eniwarecommon.ts,
	Edge eniwarecommon.Edge_id,
	jdata text)
  RETURNS void LANGUAGE plpgsql VOLATILE AS
$BODY$
DECLARE
	udate eniwarecommon.ts := now();
	jdata_json json := jdata::json;
BEGIN
	INSERT INTO eniwarenet.sn_Edge_meta(Edge_id, created, updated, jdata)
	VALUES (Edge, cdate, udate, jdata_json)
	ON CONFLICT (Edge_id) DO UPDATE
	SET jdata = EXCLUDED.jdata, updated = EXCLUDED.updated;
END;
$BODY$;

CREATE OR REPLACE FUNCTION eniwareuser.store_user_meta(
	cdate eniwarecommon.ts,
	userid BIGINT,
	jdata text)
  RETURNS void LANGUAGE plpgsql VOLATILE AS
$BODY$
DECLARE
	udate eniwarecommon.ts := now();
	jdata_json json := jdata::json;
BEGIN
	INSERT INTO eniwareuser.user_meta(user_id, created, updated, jdata)
	VALUES (userid, cdate, udate, jdata_json)
	ON CONFLICT (user_id) DO UPDATE
	SET jdata = EXCLUDED.jdata, updated = EXCLUDED.updated;
END;
$BODY$;

CREATE OR REPLACE FUNCTION eniwaredatum.store_meta(
	cdate eniwarecommon.ts,
	Edge eniwarecommon.Edge_id,
	src eniwarecommon.source_id,
	jdata text)
  RETURNS void LANGUAGE plpgsql VOLATILE AS
$BODY$
DECLARE
	udate eniwarecommon.ts := now();
	jdata_json json := jdata::json;
BEGIN
	INSERT INTO eniwaredatum.da_meta(Edge_id, source_id, created, updated, jdata)
	VALUES (Edge, src, cdate, udate, jdata_json)
	ON CONFLICT (Edge_id, source_id) DO UPDATE
	SET jdata = EXCLUDED.jdata, updated = EXCLUDED.updated;
END;
$BODY$;

CREATE OR REPLACE FUNCTION eniwaredatum.store_loc_meta(
	cdate eniwarecommon.ts,
	loc eniwarecommon.loc_id,
	src eniwarecommon.source_id,
	jdata text)
  RETURNS void LANGUAGE plpgsql VOLATILE AS
$BODY$
DECLARE
	udate eniwarecommon.ts := now();
	jdata_json json := jdata::json;
BEGIN
	INSERT INTO eniwaredatum.da_loc_meta(loc_id, source_id, created, updated, jdata)
	VALUES (loc, src, cdate, udate, jdata_json)
	ON CONFLICT (loc_id, source_id) DO UPDATE
	SET jdata = EXCLUDED.jdata, updated = EXCLUDED.updated;
END;
$BODY$;
