INSERT INTO eniwarerep.rep_consum_datum_hourly
SELECT * FROM eniwarenet.rep_consum_datum_hourly;

INSERT INTO eniwarerep.rep_consum_datum_daily
SELECT * FROM eniwarenet.rep_consum_datum_daily;

INSERT INTO eniwarerep.rep_power_datum_hourly
SELECT * FROM eniwarenet.rep_power_datum_hourly;

INSERT INTO eniwarerep.rep_net_power_datum_hourly
SELECT * FROM eniwarenet.rep_net_power_datum_hourly;

INSERT INTO eniwarerep.rep_power_datum_daily
SELECT * FROM eniwarenet.rep_power_datum_daily;

INSERT INTO eniwarerep.rep_net_power_datum_daily
SELECT * FROM eniwarenet.rep_net_power_datum_daily;

INSERT INTO eniwarerep.rep_price_datum_hourly
SELECT * FROM eniwarenet.rep_price_datum_hourly;

INSERT INTO eniwarerep.rep_price_datum_daily
SELECT * FROM eniwarenet.rep_price_datum_daily;

DROP TABLE eniwarenet.rep_consum_datum_hourly CASCADE;
DROP TABLE eniwarenet.rep_consum_datum_daily CASCADE;
DROP TABLE eniwarenet.rep_power_datum_hourly CASCADE;
DROP TABLE eniwarenet.rep_net_power_datum_hourly CASCADE;
DROP TABLE eniwarenet.rep_power_datum_daily CASCADE;
DROP TABLE eniwarenet.rep_net_power_datum_daily CASCADE;
DROP TABLE eniwarenet.rep_price_datum_hourly CASCADE;
DROP TABLE eniwarenet.rep_price_datum_daily CASCADE;

DROP FUNCTION eniwarenet.find_rep_consum_datum(IN bigint, IN text, 
	IN timestamp without time zone, IN text, IN interval);
DROP FUNCTION eniwarenet.populate_rep_consum_datum_hourly(datum eniwarenet.sn_consum_datum);
DROP TRIGGER populate_rep_consum_hourly ON eniwarenet.sn_consum_datum;
DROP FUNCTION eniwarenet.populate_rep_consum_hourly();

DROP FUNCTION eniwarenet.populate_rep_consum_datum_daily(datum eniwarenet.sn_consum_datum);
DROP TRIGGER populate_rep_consum_daily ON eniwarenet.sn_consum_datum;
DROP FUNCTION eniwarenet.populate_rep_consum_daily();

DROP FUNCTION eniwarenet.find_rep_net_power_datum(IN timestamp without time zone, IN interval);
DROP FUNCTION eniwarenet.find_rep_power_datum(IN bigint, IN text, IN timestamp without time zone, IN text, IN interval);
DROP FUNCTION eniwarenet.populate_rep_power_datum_hourly(datum eniwarenet.sn_power_datum);
DROP TRIGGER populate_rep_power_hourly ON eniwarenet.sn_power_datum;
DROP FUNCTION eniwarenet.populate_rep_power_hourly();

DROP FUNCTION eniwarenet.populate_rep_net_power_datum_hourly(datum eniwarenet.sn_power_datum);
DROP TRIGGER populate_rep_net_power_hourly ON eniwarenet.sn_power_datum;
DROP FUNCTION eniwarenet.populate_rep_net_power_hourly();

DROP FUNCTION eniwarenet.populate_rep_power_datum_daily(datum eniwarenet.sn_power_datum);
DROP TRIGGER populate_rep_power_daily ON eniwarenet.sn_power_datum;
DROP FUNCTION eniwarenet.populate_rep_power_daily();

DROP FUNCTION eniwarenet.populate_rep_net_power_datum_daily(datum eniwarenet.sn_power_datum);
DROP TRIGGER populate_rep_net_power_daily ON eniwarenet.sn_power_datum;
DROP FUNCTION eniwarenet.populate_rep_net_power_daily();

DROP FUNCTION eniwarenet.find_rep_price_datum(IN bigint, IN timestamp with time zone, IN interval);
DROP FUNCTION eniwarenet.populate_rep_price_datum_hourly(datum eniwarenet.sn_price_datum);
DROP TRIGGER populate_rep_price_hourly ON eniwarenet.sn_price_datum;
DROP FUNCTION eniwarenet.populate_rep_price_hourly();

DROP FUNCTION eniwarenet.populate_rep_price_datum_daily(datum eniwarenet.sn_price_datum);
DROP TRIGGER populate_rep_price_daily ON eniwarenet.sn_price_datum;
DROP FUNCTION eniwarenet.populate_rep_price_daily();
