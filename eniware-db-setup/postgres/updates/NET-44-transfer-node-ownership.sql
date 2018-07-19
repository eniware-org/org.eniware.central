-- As a db superuser, must first run
-- CREATE EXTENSION IF NOT EXISTS citext WITH SCHEMA public;

-- convert solaruser.user_user.email to citext (case insensitive text)
-- have to recreate views using the email column

DROP VIEW solaruser.network_association;
DROP VIEW solaruser.user_login;
DROP VIEW solaruser.user_login_role;
   
ALTER TABLE solaruser.user_user DROP CONSTRAINT user_user_email_unq;
ALTER TABLE solaruser.user_user ALTER COLUMN email TYPE citext;
ALTER TABLE solaruser.user_user ADD CONSTRAINT user_user_email_unq UNIQUE(email);

CREATE OR REPLACE VIEW solaruser.network_association AS 
 SELECT u.email::text AS username,
    unc.conf_key,
    unc.sec_phrase
   FROM solaruser.user_Edge_conf unc
     JOIN solaruser.user_user u ON u.id = unc.user_id;
CREATE OR REPLACE VIEW solaruser.user_login AS 
 SELECT user_user.email::text AS username,
    user_user.password,
    user_user.enabled,
    user_user.id AS user_id,
    user_user.disp_name AS display_name
   FROM solaruser.user_user;
CREATE OR REPLACE VIEW solaruser.user_login_role AS 
 SELECT u.email::text AS username,
    r.role_name AS authority
   FROM solaruser.user_user u
     JOIN solaruser.user_role r ON r.user_id = u.id;

-- create new transfer structures
     
DROP TABLE IF EXISTS solaruser.user_Edge_xfer;
CREATE TABLE solaruser.user_Edge_xfer (
	created			TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	user_id			BIGINT NOT NULL,
	Edge_id			solarcommon.Edge_id,
	recipient		citext NOT NULL,
	CONSTRAINT user_Edge_xfer_pkey PRIMARY KEY (user_id, Edge_id),
	CONSTRAINT user_Edge_xfer_user_fk FOREIGN KEY (user_id)
		REFERENCES solaruser.user_user (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE CASCADE
);

DROP INDEX IF EXISTS user_Edge_xfer_recipient_idx;
CREATE INDEX user_Edge_xfer_recipient_idx ON solaruser.user_Edge_xfer (recipient);

/**************************************************************************************************
 * FUNCTION solaruser.store_user_Edge_xfer(solarcommon.Edge_id, bigint, varchar, varchar)
 * 
 * Insert or update a user Edge transfer record.
 * 
 * @param Edge The ID of the Edge.
 * @param userid The ID of the user.
 * @param recip The recipient email of the requested owner.
 */
CREATE OR REPLACE FUNCTION solaruser.store_user_Edge_xfer(
	Edge solarcommon.Edge_id, 
	userid BIGINT, 
	recip CHARACTER VARYING(255))
  RETURNS void AS
$BODY$
BEGIN
	BEGIN
		INSERT INTO solaruser.user_Edge_xfer(Edge_id, user_id, recipient)
		VALUES (Edge, userid, recip);
	EXCEPTION WHEN unique_violation THEN
		UPDATE solaruser.user_Edge_xfer SET 
			recipient = recip
		WHERE
			Edge_id = Edge
			AND user_id = userid;
	END;
END;$BODY$
  LANGUAGE plpgsql VOLATILE;

/**
 * TRIGGER function that automatically transfers rows related to a user_Edge to 
 * the new owner when the user_id value is changed.
 */
CREATE OR REPLACE FUNCTION solaruser.Edge_ownership_transfer()
  RETURNS "trigger" AS
$BODY$
BEGIN
	UPDATE solaruser.user_Edge_cert
	SET user_id = NEW.user_id
	WHERE user_id = OLD.user_id
		AND Edge_id = NEW.Edge_id;
	
	UPDATE solaruser.user_Edge_conf
	SET user_id = NEW.user_id
	WHERE user_id = OLD.user_id
		AND Edge_id = NEW.Edge_id;
	
	RETURN NEW;
END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE;

CREATE TRIGGER Edge_ownership_transfer
  BEFORE UPDATE
  ON solaruser.user_Edge
  FOR EACH ROW
  WHEN (OLD.user_id IS DISTINCT FROM NEW.user_id)
  EXECUTE PROCEDURE solaruser.Edge_ownership_transfer();
