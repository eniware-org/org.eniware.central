CREATE TABLE eniwarerep.rep_stale_Edge_datum (
	ts			TIMESTAMP WITH TIME ZONE NOT NULL,
	Edge_id 	BIGINT NOT NULL,
	agg_kind 	CHARACTER(1) NOT NULL,
	datum_kind 	CHARACTER VARYING(64) NOT NULL,
	PRIMARY KEY (ts, Edge_id, agg_kind, datum_kind)
);

CREATE TABLE eniwarerep.rep_stale_datum (
	ts			TIMESTAMP WITH TIME ZONE NOT NULL,
	agg_kind 	CHARACTER(1) NOT NULL,
	datum_kind 	CHARACTER VARYING(64) NOT NULL,
	PRIMARY KEY (ts, agg_kind, datum_kind)
);

/**************************************************************************************************
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

/**************************************************************************************************
 * FUNCTION eniwarerep.populate_rep_stale_datum(timestamp with time zone, bigint, varchar)
 * 
 * Insert records into the rep_stale_datum table for asynchronously aggregating updated data later.
 * 
 * @param ts the date of the changed data
 * @param Edge_id the Edge ID (or NULL if not Edge-specific)
 * @param datum_kind the type of datum, e.g. 'power', 'consumption', etc.
 */
CREATE OR REPLACE FUNCTION eniwarerep.populate_rep_stale_datum(
	ts timestamp with time zone, 
	Edge_id bigint, 
	datum_kind character varying(64))
  RETURNS void AS
$BODY$
DECLARE
	agg_kinds CHARACTER[] = ARRAY['h', 'd'];
	a CHARACTER(1);
	agg_ts timestamp with time zone;
BEGIN
	FOREACH a IN ARRAY agg_kinds LOOP
		CASE a 
			WHEN 'h' THEN
				agg_ts := date_trunc('hour', ts);
			ELSE 
				agg_ts := date_trunc('day', ts);
		END CASE;
		BEGIN
			IF Edge_id IS NULL THEN
				INSERT INTO eniwarerep.rep_stale_datum (ts, agg_kind, datum_kind)
				VALUES (agg_ts, a, datum_kind);
			ELSE
				INSERT INTO eniwarerep.rep_stale_Edge_datum (ts, Edge_id, agg_kind, datum_kind)
				VALUES (agg_ts, Edge_id, a, datum_kind);
			END IF;
		EXCEPTION WHEN unique_violation THEN
            -- Nothing to do... just continue
        END;
	END LOOP;
END;$BODY$
LANGUAGE 'plpgsql' VOLATILE;

CREATE OR REPLACE FUNCTION eniwarerep.trigger_rep_stale_Edge_datum()
  RETURNS "trigger" AS
$BODY$BEGIN
	PERFORM eniwarerep.populate_rep_stale_datum(NEW.created, NEW.Edge_id, TG_TABLE_NAME::text);
	RETURN NEW;
END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE;

-- NOTE: populates "loc_id" into "Edge_id" column.
CREATE OR REPLACE FUNCTION eniwarerep.trigger_rep_stale_loc_datum()
  RETURNS "trigger" AS
$BODY$BEGIN
	PERFORM eniwarerep.populate_rep_stale_datum(NEW.created, NEW.loc_id, TG_TABLE_NAME::text);
	RETURN NEW;
END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE;

DROP TRIGGER IF EXISTS populate_rep_stale_datum ON eniwarenet.sn_consum_datum;
CREATE TRIGGER populate_rep_stale_datum
  AFTER INSERT OR UPDATE
  ON eniwarenet.sn_consum_datum
  FOR EACH ROW
  EXECUTE PROCEDURE eniwarerep.trigger_rep_stale_Edge_datum();

DROP TRIGGER IF EXISTS populate_rep_stale_datum ON eniwarenet.sn_power_datum;
CREATE TRIGGER populate_rep_stale_datum
  AFTER INSERT OR UPDATE
  ON eniwarenet.sn_power_datum
  FOR EACH ROW
  EXECUTE PROCEDURE eniwarerep.trigger_rep_stale_Edge_datum();

DROP TRIGGER IF EXISTS populate_rep_stale_datum ON eniwarenet.sn_price_datum;
CREATE TRIGGER populate_rep_stale_datum
  AFTER INSERT OR UPDATE
  ON eniwarenet.sn_price_datum
  FOR EACH ROW
  EXECUTE PROCEDURE eniwarerep.trigger_rep_stale_loc_datum();

/**************************************************************************************************
 * FUNCTION eniwarerep.process_one_rep_stale_Edge_datum()
 * 
 * Process a single row from the rep_stale_Edge_datum table, calling the appropriate aggregation
 * query based on the row data. This function works by naming conventions. 
 * The rep_stale_Edge_datum.datum_kind values are assumed to be named 'sn_X', referring to table
 * names in the eniwarenet schema. A corresponding 'populate_rep_X_Y' function will be called, 
 * where Y is derived from rep_stale_Edge_datum.agg_kind:
 * 
 *   d -> 'daily'
 *   h -> 'hourly'
 *
 * The function is expected to accept a RECORD type of the table 'eniwarenet.X'.
 * 
 * For example, a datum_kind value of 'sn_power_datum' and agg_kind 'd' would result in a function
 * named 'populate_rep_power_datum_daily', which will be passed rows from the 
 * 'eniwarenet.sn_power_datum' table.
 * 
 * @return count of rows processed (i.e. 0 or 1)
 */
CREATE OR REPLACE FUNCTION eniwarerep.process_one_rep_stale_Edge_datum()
  RETURNS INTEGER AS
$BODY$
DECLARE
	stale eniwarerep.rep_stale_Edge_datum;
	curs CURSOR FOR SELECT * FROM eniwarerep.rep_stale_Edge_datum 
					ORDER BY agg_kind DESC, ts ASC, Edge_id ASC, datum_kind ASC
					FOR UPDATE;
	func_name text;
	func_agg text;
	trunc_kind text;
	sql_call text;
	max_date timestamp with time zone;
	result integer := 0;
	group_kind text;
	key_kind text;
BEGIN
	OPEN curs;
	FETCH NEXT FROM curs INTO stale;
	
	IF FOUND THEN
		CASE stale.agg_kind
			WHEN 'h' THEN
				func_agg := '_hourly';
				trunc_kind := 'hour';
				max_date := stale.ts + INTERVAL '1 hour';
			ELSE
				func_agg := '_daily';
				trunc_kind := 'day';
				max_date := stale.ts + INTERVAL '1 day';
		END CASE;
		CASE stale.datum_kind
			WHEN 'sn_price_datum' THEN
				key_kind := 'loc_id';
				group_kind := 'loc_id';
			ELSE
				key_kind := 'Edge_id';
				group_kind := 'Edge_id, source_id';
		END CASE;
		func_name := 'populate_rep_' || substring(stale.datum_kind FROM 4) || func_agg;
		-- find all records in aggregate range, grouped by source, to run with
		sql_call := 'SELECT eniwarerep.' || func_name || '(c) FROM eniwarenet.' ||stale.datum_kind || ' c'
			|| ' where c.id in (select max(id) from eniwarenet.' || stale.datum_kind 
			|| ' where ' || key_kind || ' = $1 and created >= $2 and created < $3 and prev_datum IS NOT NULL'
			|| ' group by date_trunc(''' || trunc_kind || ''', created), ' || group_kind || ')';
	
		--RAISE NOTICE 'Calling aggregate SQL %; %, %, %', sql_call, stale.Edge_id, stale.ts, max_date;
		EXECUTE sql_call USING stale.Edge_id, stale.ts, max_date;
	
		DELETE FROM eniwarerep.rep_stale_Edge_datum WHERE CURRENT OF curs;
		result := 1;
	END IF;
	
	CLOSE curs;
	RETURN result;
END;$BODY$
LANGUAGE 'plpgsql' VOLATILE;

/**************************************************************************************************
 * FUNCTION eniwarerep.process_rep_stale_Edge_datum()
 * 
 * Process all rows in rep_stale_Edge_datum by repeatedly calling 
 * eniwarerep.process_one_rep_stale_Edge_datum() until no rows remain.
 */
CREATE OR REPLACE FUNCTION eniwarerep.process_rep_stale_Edge_datum()
  RETURNS void AS
$BODY$
DECLARE
	result_count INTEGER;
BEGIN
	LOOP
		SELECT * INTO result_count FROM eniwarerep.process_one_rep_stale_Edge_datum();
		IF result_count < 1 THEN
			RETURN;
		END IF;
	END LOOP;
END;$BODY$
LANGUAGE 'plpgsql' VOLATILE;
 
-- clean up old triggers
DROP FUNCTION IF EXISTS eniwarerep.populate_rep_consum_daily() CASCADE;
DROP FUNCTION IF EXISTS eniwarerep.populate_rep_consum_hourly() CASCADE;
DROP FUNCTION IF EXISTS eniwarerep.populate_rep_power_daily() CASCADE;
DROP FUNCTION IF EXISTS eniwarerep.populate_rep_power_hourly() CASCADE;
DROP FUNCTION IF EXISTS eniwarerep.populate_rep_price_daily() CASCADE;
DROP FUNCTION IF EXISTS eniwarerep.populate_rep_price_hourly() CASCADE;

DROP FUNCTION IF EXISTS eniwarerep.populate_rep_net_power_daily() CASCADE;
DROP FUNCTION IF EXISTS eniwarerep.populate_rep_net_power_hourly() CASCADE;
