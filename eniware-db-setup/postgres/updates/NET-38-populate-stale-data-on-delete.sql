CREATE OR REPLACE FUNCTION eniwarerep.trigger_rep_stale_Edge_datum()
  RETURNS "trigger" AS
$BODY$BEGIN
	CASE TG_OP
		WHEN 'INSERT', 'UPDATE' THEN
			PERFORM eniwarerep.populate_rep_stale_datum(NEW.created, NEW.Edge_id, TG_TABLE_NAME::text);
			RETURN NEW;
		ELSE
			PERFORM eniwarerep.populate_rep_stale_datum(OLD.created, OLD.Edge_id, TG_TABLE_NAME::text);
			RETURN OLD;
	END CASE;
END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE;

CREATE OR REPLACE FUNCTION eniwarerep.trigger_rep_stale_loc_datum()
  RETURNS "trigger" AS
$BODY$BEGIN
	CASE TG_OP
		WHEN 'INSERT', 'UPDATE' THEN
			PERFORM eniwarerep.populate_rep_stale_datum(NEW.created, NEW.loc_id, TG_TABLE_NAME::text);
			RETURN NEW;
		ELSE
			PERFORM eniwarerep.populate_rep_stale_datum(OLD.created, OLD.loc_id, TG_TABLE_NAME::text);
			RETURN OLD;
	END CASE;
END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE;

DROP TRIGGER IF EXISTS populate_rep_stale_datum ON eniwarenet.sn_consum_datum;
CREATE TRIGGER populate_rep_stale_datum
  AFTER INSERT OR UPDATE OR DELETE
  ON eniwarenet.sn_consum_datum
  FOR EACH ROW
  EXECUTE PROCEDURE eniwarerep.trigger_rep_stale_Edge_datum();

DROP TRIGGER IF EXISTS populate_rep_stale_datum ON eniwarenet.sn_power_datum;
CREATE TRIGGER populate_rep_stale_datum
  AFTER INSERT OR UPDATE OR DELETE
  ON eniwarenet.sn_power_datum
  FOR EACH ROW
  EXECUTE PROCEDURE eniwarerep.trigger_rep_stale_Edge_datum();

DROP TRIGGER IF EXISTS populate_rep_stale_datum ON eniwarenet.sn_price_datum;
CREATE TRIGGER populate_rep_stale_datum
  AFTER INSERT OR UPDATE OR DELETE
  ON eniwarenet.sn_price_datum
  FOR EACH ROW
  EXECUTE PROCEDURE eniwarerep.trigger_rep_stale_loc_datum();
