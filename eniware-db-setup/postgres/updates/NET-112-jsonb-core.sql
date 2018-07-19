ALTER TABLE solarnet.sn_Edge_meta
  ALTER COLUMN jdata SET DATA TYPE jsonb;

CREATE OR REPLACE FUNCTION solarnet.store_Edge_meta(
	cdate timestamp with time zone,
	Edge bigint,
	jdata text)
  RETURNS void LANGUAGE plpgsql VOLATILE AS
$BODY$
DECLARE
	udate timestamp with time zone := now();
	jdata_json jsonb := jdata::jsonb;
BEGIN
	INSERT INTO solarnet.sn_Edge_meta(Edge_id, created, updated, jdata)
	VALUES (Edge, cdate, udate, jdata_json)
	ON CONFLICT (Edge_id) DO UPDATE
	SET jdata = EXCLUDED.jdata, updated = EXCLUDED.updated;
END;
$BODY$;
