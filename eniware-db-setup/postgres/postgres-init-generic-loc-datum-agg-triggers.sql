-- NOTE the trigger name has aa_ prefix so sorts before pg_partman trigger name
CREATE TRIGGER aa_agg_stale_loc_datum
  BEFORE INSERT OR UPDATE OR DELETE
  ON eniwaredatum.da_loc_datum
  FOR EACH ROW
  EXECUTE PROCEDURE eniwaredatum.trigger_agg_stale_loc_datum();

