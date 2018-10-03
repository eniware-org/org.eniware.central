-- location
ALTER TABLE eniwarenet.sn_loc ADD COLUMN fts_default tsvector;

DROP TRIGGER IF EXISTS maintain_fts ON eniwarenet.sn_loc;
CREATE TRIGGER maintain_fts
  BEFORE INSERT OR UPDATE ON eniwarenet.sn_loc
  FOR EACH ROW EXECUTE PROCEDURE 
  tsvector_update_trigger(fts_default, 'pg_catalog.english', 
  	loc_name, country, region, state_prov, locality, postal_code, address);

UPDATE eniwarenet.sn_loc SET id = id;
CREATE INDEX sn_loc_fts_default_idx ON eniwarenet.sn_loc USING gin(fts_default);

-- weather source
ALTER TABLE eniwarenet.sn_weather_source ADD COLUMN fts_default tsvector;

DROP TRIGGER IF EXISTS maintain_fts ON eniwarenet.sn_weather_source;
CREATE TRIGGER maintain_fts
  BEFORE INSERT OR UPDATE ON eniwarenet.sn_weather_source
  FOR EACH ROW EXECUTE PROCEDURE 
  tsvector_update_trigger(fts_default, 'pg_catalog.english', sname);

UPDATE eniwarenet.sn_weather_source SET id = id;
CREATE INDEX sn_weather_source_fts_default_idx ON eniwarenet.sn_weather_source USING gin(fts_default);

-- weather location
ALTER TABLE eniwarenet.sn_weather_loc ADD COLUMN fts_default tsvector;

DROP TRIGGER IF EXISTS maintain_fts ON eniwarenet.sn_weather_loc;
CREATE TRIGGER maintain_fts
  BEFORE INSERT OR UPDATE ON eniwarenet.sn_weather_loc
  FOR EACH ROW EXECUTE PROCEDURE 
  tsvector_update_trigger(fts_default, 'pg_catalog.english', source_data);

UPDATE eniwarenet.sn_weather_loc SET id = id;
CREATE INDEX sn_weather_loc_fts_default_idx ON eniwarenet.sn_weather_loc USING gin(fts_default);

-- price source
ALTER TABLE eniwarenet.sn_price_source ADD COLUMN fts_default tsvector;

DROP TRIGGER IF EXISTS maintain_fts ON eniwarenet.sn_price_source;
CREATE TRIGGER maintain_fts
  BEFORE INSERT OR UPDATE ON eniwarenet.sn_price_source
  FOR EACH ROW EXECUTE PROCEDURE 
  tsvector_update_trigger(fts_default, 'pg_catalog.english', sname);

UPDATE eniwarenet.sn_price_source SET id = id;
CREATE INDEX sn_price_source_fts_default_idx ON eniwarenet.sn_price_source USING gin(fts_default);

ALTER TABLE eniwarenet.sn_price_loc ADD COLUMN fts_default tsvector;

DROP TRIGGER IF EXISTS maintain_fts ON eniwarenet.sn_price_loc;
CREATE TRIGGER maintain_fts
  BEFORE INSERT OR UPDATE ON eniwarenet.sn_price_loc
  FOR EACH ROW EXECUTE PROCEDURE 
  tsvector_update_trigger(fts_default, 'pg_catalog.english', loc_name, source_data, currency);

UPDATE eniwarenet.sn_price_loc SET id = id;
CREATE INDEX sn_price_loc_fts_default_idx ON eniwarenet.sn_price_loc USING gin(fts_default);
