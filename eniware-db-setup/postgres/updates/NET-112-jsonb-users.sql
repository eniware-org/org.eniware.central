ALTER TABLE eniwareuser.user_meta
  ALTER COLUMN jdata SET DATA TYPE jsonb;

CREATE OR REPLACE FUNCTION eniwareuser.store_user_meta(
	cdate timestamp with time zone,
	userid BIGINT,
	jdata text)
  RETURNS void LANGUAGE plpgsql VOLATILE AS
$BODY$
DECLARE
	udate timestamp with time zone := now();
	jdata_json jsonb := jdata::jsonb;
BEGIN
	INSERT INTO eniwareuser.user_meta(user_id, created, updated, jdata)
	VALUES (userid, cdate, udate, jdata_json)
	ON CONFLICT (user_id) DO UPDATE
	SET jdata = EXCLUDED.jdata, updated = EXCLUDED.updated;
END;
$BODY$;

DROP FUNCTION eniwareuser.find_most_recent_datum_for_user(bigint[]);
CREATE OR REPLACE FUNCTION eniwareuser.find_most_recent_datum_for_user(users bigint[])
  RETURNS SETOF eniwaredatum.da_datum_data AS
$BODY$
	SELECT r.*
	FROM (SELECT Edge_id FROM eniwareuser.user_Edge WHERE user_id = ANY(users)) AS n,
	LATERAL (SELECT * FROM eniwaredatum.find_most_recent(n.Edge_id)) AS r
	ORDER BY r.Edge_id, r.source_id;
$BODY$
  LANGUAGE sql STABLE;
