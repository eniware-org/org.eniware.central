ALTER TABLE eniwaredatum.da_datum
	ADD COLUMN jdata_i jsonb,
	ADD COLUMN jdata_a jsonb,
	ADD COLUMN jdata_s jsonb,
	ADD COLUMN jdata_t text[];

ALTER TABLE eniwareagg.agg_datum_hourly
	ADD COLUMN jdata_i jsonb,
	ADD COLUMN jdata_a jsonb,
	ADD COLUMN jdata_s jsonb,
	ADD COLUMN jdata_t text[];

ALTER TABLE eniwareagg.agg_datum_daily
	ADD COLUMN jdata_i jsonb,
	ADD COLUMN jdata_a jsonb,
	ADD COLUMN jdata_s jsonb,
	ADD COLUMN jdata_t text[];

ALTER TABLE eniwareagg.agg_datum_monthly
	ADD COLUMN jdata_i jsonb,
	ADD COLUMN jdata_a jsonb,
	ADD COLUMN jdata_s jsonb,
	ADD COLUMN jdata_t text[];

ALTER TABLE eniwaredatum.da_loc_datum
	ADD COLUMN jdata_i jsonb,
	ADD COLUMN jdata_a jsonb,
	ADD COLUMN jdata_s jsonb,
	ADD COLUMN jdata_t text[];

ALTER TABLE eniwareagg.agg_loc_datum_hourly
	ADD COLUMN jdata_i jsonb,
	ADD COLUMN jdata_a jsonb,
	ADD COLUMN jdata_s jsonb,
	ADD COLUMN jdata_t text[];

ALTER TABLE eniwareagg.agg_loc_datum_daily
	ADD COLUMN jdata_i jsonb,
	ADD COLUMN jdata_a jsonb,
	ADD COLUMN jdata_s jsonb,
	ADD COLUMN jdata_t text[];

ALTER TABLE eniwareagg.agg_loc_datum_monthly
	ADD COLUMN jdata_i jsonb,
	ADD COLUMN jdata_a jsonb,
	ADD COLUMN jdata_s jsonb,
	ADD COLUMN jdata_t text[];

