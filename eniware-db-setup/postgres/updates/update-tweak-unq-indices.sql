ALTER TABLE eniwarenet.sn_consum_datum DROP CONSTRAINT IF EXISTS consum_datum_Edge_unq;
ALTER TABLE eniwarenet.sn_consum_datum
  ADD CONSTRAINT consum_datum_Edge_unq UNIQUE(Edge_id, created, source_id)
  DEFERRABLE INITIALLY IMMEDIATE;
DROP INDEX IF EXISTS eniwarenet.consum_datum_Edge_idx;
  
ALTER TABLE eniwarenet.sn_day_datum DROP CONSTRAINT IF EXISTS sn_day_datum_loc_unq;
ALTER TABLE eniwarenet.sn_day_datum
  ADD CONSTRAINT sn_day_datum_loc_unq UNIQUE(loc_id, day)
  DEFERRABLE INITIALLY IMMEDIATE;
DROP INDEX IF EXISTS eniwarenet.day_datum_created_idx;

ALTER TABLE eniwarenet.sn_hardware_control_datum DROP CONSTRAINT IF EXISTS sn_hardware_control_datum_Edge_unq;
ALTER TABLE eniwarenet.sn_hardware_control_datum
  ADD CONSTRAINT sn_hardware_control_datum_Edge_unq UNIQUE(Edge_id, created, source_id)
  DEFERRABLE INITIALLY IMMEDIATE;
DROP INDEX IF EXISTS eniwarenet.sn_hardware_control_datum_Edge_idx;

ALTER TABLE eniwarenet.sn_power_datum DROP CONSTRAINT IF EXISTS power_datum_Edge_unq;
ALTER TABLE eniwarenet.sn_power_datum
  ADD CONSTRAINT power_datum_Edge_unq UNIQUE(Edge_id, created, source_id)
  DEFERRABLE INITIALLY IMMEDIATE;
DROP INDEX IF EXISTS eniwarenet.power_datum_Edge_idx;

ALTER TABLE eniwarenet.sn_price_datum DROP CONSTRAINT IF EXISTS price_datum_unq;
ALTER TABLE eniwarenet.sn_price_datum
  ADD CONSTRAINT price_datum_unq UNIQUE(loc_id, created)
  DEFERRABLE INITIALLY IMMEDIATE;
DROP INDEX IF EXISTS eniwarenet.price_datum_loc_idx;

ALTER TABLE eniwarenet.sn_weather_datum DROP CONSTRAINT IF EXISTS sn_weather_datum_loc_unq;
ALTER TABLE eniwarenet.sn_weather_datum
  ADD CONSTRAINT sn_weather_datum_loc_unq UNIQUE(loc_id, info_date)
  DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE eniwarerep.rep_consum_datum_daily DROP CONSTRAINT IF EXISTS rep_consum_datum_daily_pkey;
ALTER TABLE eniwarerep.rep_consum_datum_daily
  ADD CONSTRAINT rep_consum_datum_daily_pkey PRIMARY KEY(Edge_id, created_day, source_id);
DROP INDEX eniwarerep.rep_consum_datum_daily_Edge_idx;

ALTER TABLE eniwarerep.rep_consum_datum_hourly DROP CONSTRAINT IF EXISTS rep_consum_datum_hourly_pkey;
ALTER TABLE eniwarerep.rep_consum_datum_hourly
  ADD CONSTRAINT rep_consum_datum_hourly_pkey PRIMARY KEY(Edge_id, created_hour, source_id);
DROP INDEX eniwarerep.rep_consum_datum_hourly_Edge_idx;

ALTER TABLE eniwarerep.rep_power_datum_daily DROP CONSTRAINT IF EXISTS rep_power_datum_daily_pkey;
ALTER TABLE eniwarerep.rep_power_datum_daily
  ADD CONSTRAINT rep_power_datum_daily_pkey PRIMARY KEY(Edge_id, created_day, source_id);
DROP INDEX eniwarerep.rep_power_datum_daily_Edge_idx;

ALTER TABLE eniwarerep.rep_power_datum_hourly DROP CONSTRAINT IF EXISTS rep_power_datum_hourly_pkey;
ALTER TABLE eniwarerep.rep_power_datum_hourly
  ADD CONSTRAINT rep_power_datum_hourly_pkey PRIMARY KEY(Edge_id, created_hour, source_id);
DROP INDEX eniwarerep.rep_power_datum_hourly_Edge_idx;

ALTER TABLE eniwarerep.rep_price_datum_daily DROP CONSTRAINT IF EXISTS rep_price_datum_daily_pkey;
ALTER TABLE eniwarerep.rep_price_datum_daily
  ADD CONSTRAINT rep_price_datum_daily_pkey PRIMARY KEY(loc_id, created_day);
DROP INDEX eniwarerep.rep_price_datum_daily_Edge_idx;

ALTER TABLE eniwarerep.rep_price_datum_hourly DROP CONSTRAINT IF EXISTS rep_price_datum_hourly_pkey;
ALTER TABLE eniwarerep.rep_price_datum_hourly
  ADD CONSTRAINT rep_price_datum_hourly_pkey PRIMARY KEY(loc_id, created_hour);
DROP INDEX eniwarerep.rep_price_datum_hourly_Edge_idx;

CLUSTER eniwarenet.sn_consum_datum USING consum_datum_Edge_unq;
CLUSTER eniwarenet.sn_day_datum USING sn_day_datum_loc_unq;
CLUSTER eniwarenet.sn_hardware_control_datum USING sn_hardware_control_datum_Edge_unq;
CLUSTER eniwarenet.sn_power_datum USING power_datum_Edge_unq;
CLUSTER eniwarenet.sn_price_datum USING price_datum_unq;
CLUSTER eniwarenet.sn_weather_datum USING sn_weather_datum_loc_unq;

CLUSTER eniwarerep.rep_consum_datum_daily USING rep_consum_datum_daily_pkey;
CLUSTER eniwarerep.rep_consum_datum_hourly USING rep_consum_datum_hourly_pkey;
CLUSTER eniwarerep.rep_power_datum_daily USING rep_power_datum_daily_pkey;
CLUSTER eniwarerep.rep_power_datum_hourly USING rep_power_datum_hourly_pkey;
CLUSTER eniwarerep.rep_price_datum_daily USING rep_price_datum_daily_pkey;
CLUSTER eniwarerep.rep_price_datum_hourly USING rep_price_datum_hourly_pkey;
