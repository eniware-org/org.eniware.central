\echo Removing domains from Edge meta...

ALTER TABLE eniwarenet.sn_Edge_meta
  ALTER COLUMN Edge_id SET DATA TYPE bigint,
  ALTER COLUMN created SET DATA TYPE timestamp with time zone,
  ALTER COLUMN updated SET DATA TYPE timestamp with time zone;

DROP FUNCTION eniwarenet.store_Edge_meta(eniwarecommon.ts, eniwarecommon.Edge_id, text);
CREATE OR REPLACE FUNCTION eniwarenet.store_Edge_meta(
	cdate timestamp with time zone,
	Edge bigint,
	jdata text)
  RETURNS void LANGUAGE plpgsql VOLATILE AS
$BODY$
DECLARE
	udate timestamp with time zone := now();
	jdata_json json := jdata::json;
BEGIN
	INSERT INTO eniwarenet.sn_Edge_meta(Edge_id, created, updated, jdata)
	VALUES (Edge, cdate, udate, jdata_json)
	ON CONFLICT (Edge_id) DO UPDATE
	SET jdata = EXCLUDED.jdata, updated = EXCLUDED.updated;
END;
$BODY$;
