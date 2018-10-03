
CREATE SEQUENCE eniwarenet.weather_seq;
SELECT setval('eniwarenet.weather_seq', (SELECT COALESCE(MAX(id), 1) FROM (
	SELECT MAX(id) AS id FROM eniwarenet.sn_day_datum
	UNION
	SELECT MAX(id) AS id FROM eniwarenet.sn_weather_datum
) AS ids), true);
ALTER TABLE eniwarenet.sn_day_datum ALTER COLUMN id SET DEFAULT nextval('eniwarenet.weather_seq');

CREATE SEQUENCE eniwarenet.hardware_control_seq;
SELECT setval('eniwarenet.hardware_control_seq', (SELECT COALESCE(MAX(id), 1) FROM eniwarenet.sn_hardware_control_datum), true);
ALTER TABLE eniwarenet.sn_hardware_control_datum ALTER COLUMN id SET DEFAULT nextval('eniwarenet.hardware_control_seq');

CREATE SEQUENCE eniwarenet.price_seq;
SELECT setval('eniwarenet.price_seq', (SELECT COALESCE(MAX(id), 1) FROM eniwarenet.sn_price_datum), true);
ALTER TABLE eniwarenet.sn_price_datum ALTER COLUMN id SET DEFAULT nextval('eniwarenet.price_seq');

CREATE SEQUENCE eniwarenet.power_seq;
SELECT setval('eniwarenet.power_seq', (SELECT COALESCE(MAX(id), 1) FROM eniwarenet.sn_power_datum), true);
ALTER TABLE eniwarenet.sn_power_datum ALTER COLUMN id SET DEFAULT nextval('eniwarenet.power_seq');

CREATE SEQUENCE eniwarenet.consum_seq;
SELECT setval('eniwarenet.consum_seq', (SELECT COALESCE(MAX(id), 1) FROM eniwarenet.sn_consum_datum), true);
ALTER TABLE eniwarenet.sn_consum_datum ALTER COLUMN id SET DEFAULT nextval('eniwarenet.consum_seq');

