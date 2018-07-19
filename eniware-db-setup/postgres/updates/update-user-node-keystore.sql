DELETE FROM solaruser.user_Edge_cert;
ALTER TABLE solaruser.user_Edge_cert 
	DROP CONSTRAINT user_Edge_cert_pkey,
	DROP CONSTRAINT user_cert_user_fk,
	DROP CONSTRAINT user_Edge_cert_unq,
	DROP COLUMN id,
	DROP COLUMN conf_key,
	ADD COLUMN request_id VARCHAR(32) NOT NULL;

ALTER TABLE solaruser.user_Edge_cert RENAME cert TO keystore;
ALTER TABLE solaruser.user_Edge_cert ALTER COLUMN keystore SET NOT NULL;

ALTER TABLE solaruser.user_Edge_cert
  ADD CONSTRAINT user_cert_user_fk FOREIGN KEY (user_id)
      REFERENCES solaruser.user_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE solaruser.user_Edge_cert
  ADD CONSTRAINT user_Edge_cert_pkey PRIMARY KEY (user_id, Edge_id);

CREATE OR REPLACE FUNCTION solaruser.store_user_Edge_cert(
	created solarcommon.ts, 
	Edge solarcommon.Edge_id, 
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
		INSERT INTO solaruser.user_Edge_cert(created, Edge_id, user_id, status, request_id, keystore)
		VALUES (ts, Edge, userid, stat, request, keydata);
	EXCEPTION WHEN unique_violation THEN
		UPDATE solaruser.user_Edge_cert SET 
			keystore = keydata, 
			status = stat,
			request_id = request
		WHERE
			Edge_id = Edge
			AND user_id = userid;
	END;
END;$BODY$
  LANGUAGE plpgsql VOLATILE;

