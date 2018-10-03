ALTER TABLE eniwarenet.sn_power_datum ADD COLUMN watt_hour bigint;
ALTER TABLE eniwarenet.sn_power_datum DISABLE TRIGGER USER;
UPDATE eniwarenet.sn_power_datum SET watt_hour = round(kwatt_hours * 1000);
ALTER TABLE eniwarenet.sn_power_datum ENABLE TRIGGER USER;
ALTER TABLE eniwarenet.sn_power_datum DROP COLUMN kwatt_hours;

CREATE OR REPLACE FUNCTION eniwarerep.find_rep_net_power_datum(IN timestamp without time zone, IN interval)
  RETURNS TABLE(created timestamp without time zone, watt_hours double precision) AS
$BODY$
	SELECT
		c2.created at time zone l.time_zone as created,
		eniwarenet.calc_avg_watt_hours(c.pv_amps, c2.pv_amps, c.pv_volts, c2.pv_volts, 
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

CREATE OR REPLACE FUNCTION eniwarerep.find_rep_power_datum(IN bigint, IN text, IN timestamp without time zone, IN text, IN interval)
  RETURNS TABLE(
	created 		timestamp with time zone, 
	avg_pv_amps 	double precision, 
	avg_pv_volts 	double precision, 
	avg_bat_volts 	double precision,
	watt_hours		double precision,
	price_per_Wh	double precision,
	cost_amt		double precision,
	currency		character varying(10)
) AS
$BODY$
	SELECT DISTINCT ON (c.created)
		c2.created as created,
		(c.pv_amps + c2.pv_amps) / 2 as avg_pv_amps,
		(c.pv_volts + c2.pv_volts) / 2 as avg_pv_volts,
		(c.bat_volts + c2.bat_volts) / 2 as avg_bat_volts,
		eniwarenet.calc_avg_watt_hours(c.pv_amps, c2.pv_amps, c.pv_volts, c2.pv_volts, 
			c.watt_hour, c2.watt_hour, (c.created - c2.created)) as watt_hours,
		eniwarenet.calc_price_per_watt_hours(p.price, p.unit) as price_per_Wh,
		eniwarenet.calc_price_per_watt_hours(p.price, p.unit) * 
			eniwarenet.calc_avg_watt_hours(c.pv_amps, c2.pv_amps, c.pv_volts, c2.pv_volts, 
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
