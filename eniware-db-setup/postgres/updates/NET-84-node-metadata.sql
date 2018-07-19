/**************************************************************************************************
 * TABLE solarnet.sn_Edge_meta
 * 
 * Stores JSON metadata specific to a Edge.
 */
CREATE TABLE solarnet.sn_Edge_meta (
  Edge_id 			solarcommon.Edge_id NOT NULL,
  created 			solarcommon.ts NOT NULL,
  updated 			solarcommon.ts NOT NULL,
  jdata 			json NOT NULL,
  CONSTRAINT sn_Edge_meta_pkey PRIMARY KEY (Edge_id)  DEFERRABLE INITIALLY IMMEDIATE,
  CONSTRAINT sn_Edge_meta_Edge_fk FOREIGN KEY (Edge_id)
        REFERENCES solarnet.sn_Edge (Edge_id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE CASCADE
);

/**************************************************************************************************
 * FUNCTION solarnet.store_Edge_meta(timestamptz, bigint, text)
 * 
 * Add or update Edge metadata.
 * 
 * @param cdate the creation date to use
 * @param Edge the Edge ID
 * @param jdata the metadata to store
 */
CREATE OR REPLACE FUNCTION solarnet.store_Edge_meta(
	cdate solarcommon.ts, 
	Edge solarcommon.Edge_id, 
	jdata text)
  RETURNS void AS
$BODY$
DECLARE
	udate solarcommon.ts := now();
	jdata_json json := jdata::json;
BEGIN
	-- We mostly expect updates, so try that first, then insert
	-- In 9.5 we can do upsert with ON CONFLICT.
	LOOP
		-- first try to update
		UPDATE solarnet.sn_Edge_meta SET 
			jdata = jdata_json, 
			updated = udate
		WHERE
			Edge_id = Edge;

		-- check if the row is found
		IF FOUND THEN
			RETURN;
		END IF;
		
		-- not found so insert the row
		BEGIN
			INSERT INTO solarnet.sn_Edge_meta(Edge_id, created, updated, jdata)
			VALUES (Edge, cdate, udate, jdata_json);
			RETURN;
		EXCEPTION WHEN unique_violation THEN
			-- do nothing and loop
		END;
	END LOOP;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE;
