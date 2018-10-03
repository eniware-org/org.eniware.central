CREATE OR REPLACE FUNCTION eniwarenet.maintain_loc_datum_most_recent(
		d_name TEXT, 
		d_loc_id BIGINT, 
		d_id BIGINT, 
		d_created TIMESTAMP WITH TIME ZONE)
  RETURNS void AS
$BODY$
DECLARE
	recent timestamptz := NULL;
BEGIN
	EXECUTE 'SELECT d.created FROM eniwarenet.sn_' || d_name || '_datum_most_recent r '
		|| 'INNER JOIN eniwarenet.sn_' || d_name || '_datum d ON d.id = r.datum_id '
		|| 'WHERE r.loc_id = $1'
	INTO recent
	USING d_loc_id;

	IF recent IS NULL THEN
		EXECUTE 'INSERT INTO eniwarenet.sn_' || d_name || '_datum_most_recent (loc_id, datum_id) VALUES '
			|| '($1, $2)'
		USING d_loc_id, d_id;
	ELSIF recent < d_created THEN
		EXECUTE 'UPDATE eniwarenet.sn_' || d_name ||'_datum_most_recent SET datum_id = $1 '
			|| 'WHERE loc_id = $2'
		USING d_id, d_loc_id;
	END IF;
END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE;

/* =========================================================================
   =========================================================================
   PRICE
   =========================================================================
   ========================================================================= */

CREATE TABLE eniwarenet.sn_price_datum_most_recent
(
  loc_id BIGINT NOT NULL,
  datum_id BIGINT NOT NULL,
  CONSTRAINT sn_price_datum_most_recent_pkey PRIMARY KEY (loc_id),
  CONSTRAINT sn_price_datum_most_recent_datum_fk FOREIGN KEY (datum_id)
      REFERENCES eniwarenet.sn_price_datum (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE OR REPLACE FUNCTION eniwarenet.populate_price_datum_most_recent()
  RETURNS "trigger" AS
$BODY$BEGIN
	PERFORM eniwarenet.maintain_loc_datum_most_recent('price', 
		NEW.loc_id, NEW.id, NEW.created);
	RETURN NEW;
END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE;

CREATE TRIGGER populate_price_datum_most_recent
  AFTER INSERT OR UPDATE
  ON eniwarenet.sn_price_datum
  FOR EACH ROW
  EXECUTE PROCEDURE eniwarenet.populate_price_datum_most_recent();

