DROP INDEX eniwarenet.power_datum_local_created_idx;
DROP TRIGGER populate_local_created ON eniwarenet.sn_power_datum;
DROP FUNCTION eniwarerep.find_rep_net_power_datum(IN timestamp without time zone, IN interval);
DROP FUNCTION eniwarenet.populate_local_created();
ALTER TABLE eniwarenet.sn_power_datum DROP COLUMN local_created;
DROP FUNCTION eniwarerep.populate_rep_net_power_datum_hourly(datum eniwarenet.sn_power_datum);
DROP FUNCTION eniwarerep.populate_rep_net_power_datum_daily(datum eniwarenet.sn_power_datum);
