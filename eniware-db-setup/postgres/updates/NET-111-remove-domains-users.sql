ALTER TABLE eniwareuser.user_meta
  ALTER COLUMN created SET DATA TYPE timestamp with time zone,
  ALTER COLUMN updated SET DATA TYPE timestamp with time zone;

DROP FUNCTION eniwareuser.store_user_meta(eniwarecommon.ts, bigint, text);
CREATE OR REPLACE FUNCTION eniwareuser.store_user_meta(
	cdate timestamp with time zone,
	userid BIGINT,
	jdata text)
  RETURNS void LANGUAGE plpgsql VOLATILE AS
$BODY$
DECLARE
	udate timestamp with time zone := now();
	jdata_json json := jdata::json;
BEGIN
	INSERT INTO eniwareuser.user_meta(user_id, created, updated, jdata)
	VALUES (userid, cdate, udate, jdata_json)
	ON CONFLICT (user_id) DO UPDATE
	SET jdata = EXCLUDED.jdata, updated = EXCLUDED.updated;
END;
$BODY$;

DROP FUNCTION eniwareuser.store_user_Edge_cert(eniwarecommon.ts, eniwarecommon.Edge_id, bigint, character, text, bytea);
CREATE OR REPLACE FUNCTION eniwareuser.store_user_Edge_cert(
	created timestamp with time zone,
	Edge bigint,
	userid bigint,
	stat char,
	request text,
	keydata bytea)
  RETURNS void AS
$BODY$
DECLARE
	ts TIMESTAMP WITH TIME ZONE := (CASE WHEN created IS NULL THEN now() ELSE created END);
BEGIN
	BEGIN
		INSERT INTO eniwareuser.user_Edge_cert(created, Edge_id, user_id, status, request_id, keystore)
		VALUES (ts, Edge, userid, stat, request, keydata);
	EXCEPTION WHEN unique_violation THEN
		UPDATE eniwareuser.user_Edge_cert SET
			keystore = keydata,
			status = stat,
			request_id = request
		WHERE
			Edge_id = Edge
			AND user_id = userid;
	END;
END;$BODY$
  LANGUAGE plpgsql VOLATILE;

ALTER TABLE eniwareuser.user_Edge_xfer
	ALTER COLUMN Edge_id SET DATA TYPE bigint;

DROP FUNCTION eniwareuser.store_user_Edge_xfer(eniwarecommon.Edge_id, bigint, character varying);
CREATE OR REPLACE FUNCTION eniwareuser.store_user_Edge_xfer(
	Edge bigint,
	userid bigint,
	recip CHARACTER VARYING(255))
  RETURNS void AS
$BODY$
BEGIN
	BEGIN
		INSERT INTO eniwareuser.user_Edge_xfer(Edge_id, user_id, recipient)
		VALUES (Edge, userid, recip);
	EXCEPTION WHEN unique_violation THEN
		UPDATE eniwareuser.user_Edge_xfer SET
			recipient = recip
		WHERE
			Edge_id = Edge
			AND user_id = userid;
	END;
END;$BODY$
  LANGUAGE plpgsql VOLATILE;
