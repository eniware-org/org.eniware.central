-- Remove eniwarerep schema. 
DROP SCHEMA IF EXISTS eniwarerep CASCADE;

-- Remove legacy datum tables
DROP TABLE IF EXISTS eniwarenet.sn_consum_datum_most_recent;
DROP TABLE IF EXISTS eniwarenet.sn_consum_datum CASCADE;
DROP TABLE IF EXISTS eniwarenet.sn_day_datum CASCADE;
DROP TABLE IF EXISTS eniwarenet.sn_hardware_control_datum_most_recent;
DROP TABLE IF EXISTS eniwarenet.sn_hardware_control_datum CASCADE;
DROP TABLE IF EXISTS eniwarenet.sn_power_datum_most_recent;
DROP TABLE IF EXISTS eniwarenet.sn_power_datum CASCADE;
DROP TABLE IF EXISTS eniwarenet.sn_price_datum_most_recent;
DROP TABLE IF EXISTS eniwarenet.sn_price_datum CASCADE;
DROP TABLE IF EXISTS eniwarenet.sn_weather_datum CASCADE;

-- Unused sequences
DROP SEQUENCE IF EXISTS eniwarenet.consum_seq;
DROP SEQUENCE IF EXISTS eniwarenet.hardware_control_seq;
DROP SEQUENCE IF EXISTS eniwarenet.power_seq;
DROP SEQUENCE IF EXISTS eniwarenet.price_seq;
DROP SEQUENCE IF EXISTS eniwarenet.weather_seq;

-- Unused functions
DROP FUNCTION IF EXISTS eniwarenet.calc_avg_watt_hours(integer, integer, double precision, double precision, interval);
DROP FUNCTION IF EXISTS eniwarenet.calc_price_per_watt_hours(real, text);
DROP FUNCTION IF EXISTS eniwarenet.maintain_datum_most_recent(text, bigint, text, bigint, timestamp with time zone);
DROP FUNCTION IF EXISTS eniwarenet.maintain_loc_datum_most_recent(text, bigint, bigint, timestamp with time zone);

-- Trigger functions
DROP FUNCTION IF EXISTS eniwaredatum.mig_consum_datum();
DROP FUNCTION IF EXISTS eniwaredatum.mig_day_datum();
DROP FUNCTION IF EXISTS eniwaredatum.mig_hardware_control_datum();
DROP FUNCTION IF EXISTS eniwaredatum.mig_power_datum();
DROP FUNCTION IF EXISTS eniwaredatum.mig_price_datum();
DROP FUNCTION IF EXISTS eniwaredatum.mig_weather_datum();
DROP FUNCTION IF EXISTS eniwarenet.populate_consum_datum_most_recent();
DROP FUNCTION IF EXISTS eniwarenet.populate_hardware_control_datum_most_recent();
DROP FUNCTION IF EXISTS eniwarenet.populate_hl_temperature();
DROP FUNCTION IF EXISTS eniwarenet.populate_near_sky_condition();
DROP FUNCTION IF EXISTS eniwarenet.populate_power_datum_most_recent();
DROP FUNCTION IF EXISTS eniwarenet.populate_prev_consum_datum();
DROP FUNCTION IF EXISTS eniwarenet.populate_prev_power_datum();
DROP FUNCTION IF EXISTS eniwarenet.populate_prev_price_datum();
DROP FUNCTION IF EXISTS eniwarenet.populate_price_datum_most_recent();
