ALTER TABLE eniwarenet.sn_consum_datum ADD COLUMN watts INTEGER;
ALTER TABLE eniwarenet.sn_consum_datum DISABLE TRIGGER USER;
UPDATE eniwarenet.sn_consum_datum SET watts = round(amps * voltage);
ALTER TABLE eniwarenet.sn_consum_datum ENABLE TRIGGER USER;
ALTER TABLE eniwarenet.sn_consum_datum DROP COLUMN amps;
ALTER TABLE eniwarenet.sn_consum_datum DROP COLUMN voltage;

ALTER TABLE eniwarerep.rep_consum_datum_hourly ADD COLUMN watts integer;
ALTER TABLE eniwarerep.rep_consum_datum_hourly DISABLE TRIGGER USER;
UPDATE eniwarerep.rep_consum_datum_hourly SET watts = round(amps * voltage);
ALTER TABLE eniwarerep.rep_consum_datum_hourly ENABLE TRIGGER USER;
ALTER TABLE eniwarerep.rep_consum_datum_hourly DROP COLUMN amps;
ALTER TABLE eniwarerep.rep_consum_datum_hourly DROP COLUMN voltage;

ALTER TABLE eniwarerep.rep_consum_datum_daily ADD COLUMN watts integer;
ALTER TABLE eniwarerep.rep_consum_datum_daily DISABLE TRIGGER USER;
UPDATE eniwarerep.rep_consum_datum_daily SET watts = round(amps * voltage);
ALTER TABLE eniwarerep.rep_consum_datum_daily ENABLE TRIGGER USER;
ALTER TABLE eniwarerep.rep_consum_datum_daily DROP COLUMN amps;
ALTER TABLE eniwarerep.rep_consum_datum_daily DROP COLUMN voltage;

ALTER TABLE eniwarenet.sn_power_datum ADD COLUMN watts INTEGER;
ALTER TABLE eniwarenet.sn_power_datum DISABLE TRIGGER USER;
UPDATE eniwarenet.sn_power_datum SET watts = round(pv_amps * pv_volts);
ALTER TABLE eniwarenet.sn_power_datum ENABLE TRIGGER USER;
ALTER TABLE eniwarenet.sn_power_datum DROP COLUMN pv_amps;
ALTER TABLE eniwarenet.sn_power_datum DROP COLUMN pv_volts;

ALTER TABLE eniwarerep.rep_power_datum_hourly ADD COLUMN watts INTEGER;
ALTER TABLE eniwarerep.rep_power_datum_hourly DISABLE TRIGGER USER;
UPDATE eniwarerep.rep_power_datum_hourly SET watts = round(pv_amps * pv_volts);
ALTER TABLE eniwarerep.rep_power_datum_hourly ENABLE TRIGGER USER;
ALTER TABLE eniwarerep.rep_power_datum_hourly DROP COLUMN pv_amps;
ALTER TABLE eniwarerep.rep_power_datum_hourly DROP COLUMN pv_volts;

ALTER TABLE eniwarerep.rep_power_datum_daily ADD COLUMN watts INTEGER;
ALTER TABLE eniwarerep.rep_power_datum_daily DISABLE TRIGGER USER;
UPDATE eniwarerep.rep_power_datum_daily SET watts = round(pv_amps * pv_volts);
ALTER TABLE eniwarerep.rep_power_datum_daily ENABLE TRIGGER USER;
ALTER TABLE eniwarerep.rep_power_datum_daily DROP COLUMN pv_amps;
ALTER TABLE eniwarerep.rep_power_datum_daily DROP COLUMN pv_volts;

DROP FUNCTION eniwarenet.calc_avg_watt_hours(real, real, real, real, 
	double precision, double precision, interval);
	
CREATE OR REPLACE FUNCTION eniwarenet.calc_avg_watt_hours(integer, integer, 
	double precision, double precision, interval)
  RETURNS double precision AS
$BODY$
	SELECT CASE 
			WHEN 
				/* Wh readings available, so use difference in Wh */
				$3 IS NOT NULL AND $4 IS NOT NULL AND $3 > $4
				THEN $3 - $4
			WHEN 
				/* Assume day reset on inverter, so Wh for day reset */
				$3 IS NOT NULL AND $4 IS NOT NULL AND $3 < $4
				THEN $3
			ELSE 
				/* Wh not available, so calculate Wh using (watts * dt) */
				ABS(($1 + $2) / 2) * ((extract('epoch' from $5)) / 3600)
		END
$BODY$
  LANGUAGE sql IMMUTABLE;

DROP FUNCTION eniwarerep.find_rep_consum_datum(bigint, text, 
	timestamp without time zone, text, interval);

CREATE OR REPLACE FUNCTION eniwarerep.find_rep_consum_datum(IN bigint, IN text, 
	IN timestamp without time zone, IN text, IN interval)
RETURNS TABLE(
	created 		timestamp with time zone, 
	avg_watts 		double precision, 
	watt_hours		double precision,
	price_per_Wh	double precision,
	cost_amt		double precision,
	currency		character varying(10)
) AS
$BODY$
	SELECT DISTINCT ON (c.created)
		c2.created as created,
		CAST((c.watts + c2.watts) / 2 as double precision) as avg_watts,
		eniwarenet.calc_avg_watt_hours(c.watts, c2.watts, 
			c.watt_hour, c2.watt_hour, (c.created - c2.created)) as watt_hours,
		eniwarenet.calc_price_per_watt_hours(p.price, p.unit) as price_per_Wh,
		eniwarenet.calc_price_per_watt_hours(p.price, p.unit) * 
			eniwarenet.calc_avg_watt_hours(c.watts, c2.watts, 
			c.watt_hour, c2.watt_hour, (c.created - c2.created)) as cost_amt,
		p.currency
	FROM eniwarenet.sn_consum_datum c
	LEFT OUTER JOIN eniwarenet.sn_consum_datum c2 ON c2.id = c.prev_datum
	LEFT OUTER JOIN (
		SELECT p.created, p.price, l.id, l.unit, l.currency
		FROM eniwarenet.sn_price_datum p
		INNER JOIN eniwarenet.sn_price_loc l ON l.id = p.loc_id
		WHERE p.created between (($3  - interval '1 hour') at time zone $4) 
				and (($3 + $5) at time zone $4)
		) AS p ON p.created BETWEEN c.created - interval '1 hour' AND c.created
			AND p.id = c.price_loc_id
	WHERE 
		c2.Edge_id = $1
		AND c2.source_id = $2
		AND c2.created >= $3 at time zone $4
		AND c2.created < $3 at time zone $4 + $5
	ORDER BY c.created, p.created DESC
$BODY$
LANGUAGE 'sql' STABLE;

	
CREATE OR REPLACE FUNCTION eniwarerep.populate_rep_consum_datum_hourly(datum eniwarenet.sn_consum_datum)
  RETURNS void AS
$BODY$
DECLARE
	chour timestamp;
	Edge_tz text;
	data record;
BEGIN
	SELECT l.time_zone 
	FROM eniwarenet.sn_Edge n
	INNER JOIN eniwarenet.sn_loc l ON l.id = n.loc_id
	WHERE n.Edge_id = datum.Edge_id
	INTO Edge_tz;
	
	SELECT date_trunc('hour', c.created at time zone Edge_tz)
	FROM eniwarenet.sn_consum_datum c
	WHERE c.id = datum.prev_datum
	INTO chour;

	IF NOT FOUND THEN
		--RAISE NOTICE 'Datum % has no previous datum.', datum;
		RETURN;
	END IF;

	SELECT 
		date_trunc('hour', sub.created at time zone Edge_tz) as created_hour,
		datum.Edge_id as Edge_id,
		datum.source_id as source_id,
		avg(sub.avg_watts) as watts,
		sum(sub.watt_hours) as watt_hours,
		sum(sub.cost_amt) as cost_amt,
		min(sub.currency) as cost_currency
	FROM eniwarerep.find_rep_consum_datum(datum.Edge_id, datum.source_id, chour, Edge_tz, interval '1 hour') AS sub
	GROUP BY date_trunc('hour', sub.created at time zone Edge_tz)
	ORDER BY date_trunc('hour', sub.created at time zone Edge_tz)
	INTO data;
	--RAISE NOTICE 'Got data: %', data;
	
	IF NOT FOUND THEN
		RAISE NOTICE 'Datum % has insufficient data.', datum;
		RETURN;
	END IF;
	
	<<insert_update>>
	LOOP
		UPDATE eniwarerep.rep_consum_datum_hourly SET
			watts = data.watts, 
			watt_hours = data.watt_hours,
			cost_amt = data.cost_amt,
			cost_currency = data.cost_currency
		WHERE created_hour = data.created_hour
			AND Edge_id = data.Edge_id
			AND source_id = data.source_id;
		
		EXIT insert_update WHEN FOUND;

		INSERT INTO eniwarerep.rep_consum_datum_hourly (
			created_hour, Edge_id, source_id, watts, 
			watt_hours, cost_amt, cost_currency)
		VALUES (data.created_hour, data.Edge_id, data.source_id, 	
			data.watts, 
			data.watt_hours, data.cost_amt, data.cost_currency);

		EXIT insert_update;

	END LOOP insert_update;
END;$BODY$
LANGUAGE 'plpgsql' VOLATILE;

CREATE OR REPLACE FUNCTION eniwarerep.populate_rep_consum_datum_daily(datum eniwarenet.sn_consum_datum)
  RETURNS void AS
$BODY$
DECLARE
	chour timestamp;
	Edge_tz text;
	data record;
BEGIN
	SELECT l.time_zone 
	FROM eniwarenet.sn_Edge n
	INNER JOIN eniwarenet.sn_loc l ON l.id = n.loc_id
	WHERE n.Edge_id = datum.Edge_id
	INTO Edge_tz;
	
	SELECT date_trunc('day', c.created at time zone Edge_tz)
	FROM eniwarenet.sn_consum_datum c
	WHERE c.id = datum.prev_datum
	INTO chour;

	IF NOT FOUND THEN
		--RAISE NOTICE 'Datum % has no previous datum.', datum;
		RETURN;
	END IF;

	SELECT 
		date(sub.created at time zone Edge_tz) as created_day,
		datum.Edge_id as Edge_id,
		datum.source_id as source_id,
		avg(sub.avg_watts) as watts,
		sum(sub.watt_hours) as watt_hours,
		sum(sub.cost_amt) as cost_amt,
		min(sub.currency) as cost_currency
	FROM eniwarerep.find_rep_consum_datum(datum.Edge_id, datum.source_id, chour, Edge_tz, interval '1 day') AS sub
	GROUP BY date(sub.created at time zone Edge_tz)
	ORDER BY date(sub.created at time zone Edge_tz)
	INTO data;
	--RAISE NOTICE 'Got data: %', data;
	
	<<insert_update>>
	LOOP
		UPDATE eniwarerep.rep_consum_datum_daily SET
			watts = data.watts, 
			watt_hours = data.watt_hours,
			cost_amt = data.cost_amt,
			cost_currency = data.cost_currency
		WHERE created_day = data.created_day
			AND Edge_id = data.Edge_id
			AND source_id = data.source_id;
		
		EXIT insert_update WHEN FOUND;

		INSERT INTO eniwarerep.rep_consum_datum_daily (
			created_day, Edge_id, source_id, watts, 
			watt_hours, cost_amt, cost_currency)
		VALUES (data.created_day, data.Edge_id, data.source_id, 	
			data.watts, 
			data.watt_hours, data.cost_amt, data.cost_currency);

		EXIT insert_update;
	END LOOP insert_update;
END;$BODY$
LANGUAGE 'plpgsql' VOLATILE;

CREATE OR REPLACE FUNCTION eniwarerep.find_rep_net_power_datum(IN timestamp without time zone, IN interval)
  RETURNS TABLE(created timestamp without time zone, watt_hours double precision) AS
$BODY$
	SELECT
		c2.created at time zone l.time_zone as created,
		eniwarenet.calc_avg_watt_hours(c.watts, c2.watts, 
			c.watt_hour, c2.watt_hour, (c.created - c2.created)) as watt_hours
	FROM eniwarenet.sn_power_datum c
	INNER JOIN eniwarenet.sn_Edge n ON n.Edge_id = c.Edge_id
	INNER JOIN eniwarenet.sn_loc l ON l.id = n.loc_id
	LEFT OUTER JOIN eniwarenet.sn_power_datum c2 ON c2.id = c.prev_datum
	WHERE 
		c2.local_created >= $1
		and c2.local_created < $1 + $2
	ORDER BY c.created, c.Edge_id
$BODY$
LANGUAGE 'sql' STABLE;

DROP FUNCTION eniwarerep.find_rep_power_datum(bigint, text, timestamp without time zone, text, interval);

CREATE OR REPLACE FUNCTION eniwarerep.find_rep_power_datum(IN bigint, IN text, IN timestamp without time zone, IN text, IN interval)
  RETURNS TABLE(
	created 		timestamp with time zone, 
	avg_watts 		double precision, 
	avg_bat_volts	double precision,
	watt_hours		double precision,
	price_per_Wh	double precision,
	cost_amt		double precision,
	currency		character varying(10)
) AS
$BODY$
	SELECT DISTINCT ON (c.created)
		c2.created as created,
		CAST((c.watts + c2.watts) / 2 as double precision) as avg_watts,
		(c.bat_volts + c2.bat_volts) / 2 as avg_bat_volts,
		eniwarenet.calc_avg_watt_hours(c.watts, c2.watts, 
			c.watt_hour, c2.watt_hour, (c.created - c2.created)) as watt_hours,
		eniwarenet.calc_price_per_watt_hours(p.price, p.unit) as price_per_Wh,
		eniwarenet.calc_price_per_watt_hours(p.price, p.unit) * 
			eniwarenet.calc_avg_watt_hours(c.watts, c2.watts, 
			c.watt_hour, c2.watt_hour, (c.created - c2.created)) as cost_amt,
		p.currency
	FROM eniwarenet.sn_power_datum c
	LEFT OUTER JOIN eniwarenet.sn_power_datum c2 ON c2.id = c.prev_datum
	LEFT OUTER JOIN (
		SELECT p.created, l.id, p.price, l.unit, l.currency
		FROM eniwarenet.sn_price_datum p
		INNER JOIN eniwarenet.sn_price_loc l ON l.id = p.loc_id
		WHERE p.created between (($3  - interval '1 hour') at time zone $4) 
			and (($3 + $5) at time zone $4)
		) AS p ON p.created BETWEEN c.created - interval '1 hour' AND c.created
			AND p.id = c.price_loc_id
	WHERE 
		c2.Edge_id = $1
		AND c2.source_id = $2
		AND c2.created >= $3 at time zone $4
		AND c2.created < $3 at time zone $4 + $5
	ORDER BY c.created, p.created DESC
$BODY$
LANGUAGE 'sql' STABLE;

CREATE OR REPLACE FUNCTION eniwarerep.populate_rep_power_datum_hourly(datum eniwarenet.sn_power_datum)
  RETURNS void AS
$BODY$
DECLARE
	chour timestamp;
	Edge_tz text;
	data record;
BEGIN
	SELECT l.time_zone 
	FROM eniwarenet.sn_Edge n
	INNER JOIN eniwarenet.sn_loc l ON l.id = n.loc_id
	WHERE n.Edge_id = datum.Edge_id
	INTO Edge_tz;
	
	SELECT date_trunc('hour', c.created at time zone Edge_tz)
	FROM eniwarenet.sn_power_datum c
	WHERE c.id = datum.prev_datum
	INTO chour;

	IF NOT FOUND THEN
		--RAISE NOTICE 'Datum % has no previous datum.', datum;
		RETURN;
	END IF;

	SELECT 
		date_trunc('hour', sub.created at time zone Edge_tz) as created_hour,
		datum.Edge_id as Edge_id,
		datum.source_id as source_id,
		avg(sub.avg_watts) as watts,
		avg(sub.avg_bat_volts) as bat_volts,
		sum(sub.watt_hours) as watt_hours,
		sum(sub.cost_amt) as cost_amt,
		min(sub.currency) as cost_currency
	FROM eniwarerep.find_rep_power_datum(datum.Edge_id, datum.source_id, chour, Edge_tz, interval '1 hour') AS sub
	GROUP BY date_trunc('hour', sub.created at time zone Edge_tz)
	ORDER BY date_trunc('hour', sub.created at time zone Edge_tz)
	INTO data;
	--RAISE NOTICE 'Got data: %', data;
	
	IF NOT FOUND THEN
		RAISE NOTICE 'Datum % has insufficient data.', datum;
		RETURN;
	END IF;
	
	<<insert_update>>
	LOOP
		UPDATE eniwarerep.rep_power_datum_hourly SET
			watts = data.watts, 
			bat_volts = data.bat_volts,
			watt_hours = data.watt_hours,
			cost_amt = data.cost_amt,
			cost_currency = data.cost_currency
		WHERE created_hour = data.created_hour
			AND Edge_id = data.Edge_id
			AND source_id = data.source_id;
		
		EXIT insert_update WHEN FOUND;

		INSERT INTO eniwarerep.rep_power_datum_hourly (
			created_hour, Edge_id, source_id, watts, bat_volts, 
			watt_hours, cost_amt, cost_currency)
		VALUES (data.created_hour, data.Edge_id, data.source_id,	
			data.watts, data.bat_volts, 
			data.watt_hours, data.cost_amt, data.cost_currency);

		EXIT insert_update;

	END LOOP insert_update;
END;$BODY$
LANGUAGE 'plpgsql' VOLATILE;

CREATE OR REPLACE FUNCTION eniwarerep.populate_rep_power_datum_daily(datum eniwarenet.sn_power_datum)
  RETURNS void AS
$BODY$
DECLARE
	chour timestamp;
	Edge_tz text;
	data record;
BEGIN
	SELECT l.time_zone 
	FROM eniwarenet.sn_Edge n
	INNER JOIN eniwarenet.sn_loc l ON l.id = n.loc_id
	WHERE n.Edge_id = datum.Edge_id
	INTO Edge_tz;
	
	SELECT date_trunc('day', c.created at time zone Edge_tz)
	FROM eniwarenet.sn_power_datum c
	WHERE c.id = datum.prev_datum
	INTO chour;

	IF NOT FOUND THEN
		--RAISE NOTICE 'Datum % has no previous datum.', datum;
		RETURN;
	END IF;

	SELECT 
		date(sub.created at time zone Edge_tz) as created_day,
		datum.Edge_id as Edge_id,
		datum.source_id as source_id,
		avg(sub.avg_watts) as watts,
		avg(sub.avg_bat_volts) as bat_volts,
		sum(sub.watt_hours) as watt_hours,
		sum(sub.cost_amt) as cost_amt,
		min(sub.currency) as cost_currency
	FROM eniwarerep.find_rep_power_datum(datum.Edge_id, datum.source_id, chour, Edge_tz, interval '1 day') AS sub
	GROUP BY date(sub.created at time zone Edge_tz)
	ORDER BY date(sub.created at time zone Edge_tz)
	INTO data;
	--RAISE NOTICE 'Got data: %', data;
	
	<<insert_update>>
	LOOP
		UPDATE eniwarerep.rep_power_datum_daily SET
			watts = data.watts, 
			bat_volts = data.bat_volts,
			watt_hours = data.watt_hours,
			cost_amt = data.cost_amt,
			cost_currency = data.cost_currency
		WHERE created_day = data.created_day
			AND Edge_id = data.Edge_id
			AND source_id = data.source_id;
		
		EXIT insert_update WHEN FOUND;

		INSERT INTO eniwarerep.rep_power_datum_daily (
			created_day, Edge_id, source_id, watts, bat_volts, 
			watt_hours, cost_amt, cost_currency)
		VALUES (data.created_day, data.Edge_id, data.source_id,
			data.watts, data.bat_volts, 
			data.watt_hours, data.cost_amt, data.cost_currency);

		EXIT insert_update;

	END LOOP insert_update;
END;$BODY$
LANGUAGE 'plpgsql' VOLATILE;

