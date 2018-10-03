/* === USER ================================================================ */

CREATE SCHEMA eniwareuser;

CREATE SEQUENCE eniwareuser.eniwareuser_seq;

/**
 * user_user: main table for user information.
 */
CREATE TABLE eniwareuser.user_user (
	id					BIGINT NOT NULL DEFAULT nextval('eniwareuser.eniwareuser_seq'),
	created				TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	disp_name			CHARACTER VARYING(128) NOT NULL,
	email				citext NOT NULL,
	password			CHARACTER VARYING(128) NOT NULL,
	enabled				BOOLEAN NOT NULL DEFAULT TRUE,
	loc_id				BIGINT,
	jdata				jsonb,
	CONSTRAINT user_user_pkey PRIMARY KEY (id),
	CONSTRAINT user_user_email_unq UNIQUE (email),
	CONSTRAINT user_user_loc_fk FOREIGN KEY (loc_id)
		REFERENCES eniwarenet.sn_loc (id)
		ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE INDEX user_user_jdata_idx ON eniwareuser.user_user
	USING GIN (jdata jsonb_path_ops);

/**
 * Add or update the internal data for a user.
 *
 * @param user_id The ID of the user to update.
 * @param json_obj The JSON object to add. All <code>null</code> values will be removed from the resulting object.
 */
CREATE OR REPLACE FUNCTION eniwareuser.store_user_data(
	user_id bigint,
	json_obj jsonb)
  RETURNS void LANGUAGE sql VOLATILE AS
$BODY$
	UPDATE eniwareuser.user_user
	SET jdata = jsonb_strip_nulls(COALESCE(jdata, '{}'::jsonb) || json_obj)
	WHERE id = user_id
$BODY$;

/**
 * user_meta: JSON metadata specific to a user.
 */
CREATE TABLE eniwareuser.user_meta (
  user_id 			BIGINT NOT NULL,
  created 			timestamp with time zone NOT NULL,
  updated 			timestamp with time zone NOT NULL,
  jdata				jsonb NOT NULL,
  CONSTRAINT user_meta_pkey PRIMARY KEY (user_id),
  CONSTRAINT user_meta_user_fk FOREIGN KEY (user_id)
        REFERENCES eniwareuser.user_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE CASCADE
);

/******************************************************************************
 * FUNCTION eniwareuser.store_meta(timestamptz, bigint, text)
 *
 * Add or update user metadata.
 *
 * @param cdate the creation date to use
 * @param userid the user ID
 * @param jdata the metadata to store
 */
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

 /**
 * user_role: user granted login roles
 */
CREATE TABLE eniwareuser.user_role (
	user_id			BIGINT NOT NULL,
	role_name		CHARACTER VARYING(128) NOT NULL,
	CONSTRAINT user_role_pkey PRIMARY KEY (user_id, role_name),
	CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id)
		REFERENCES eniwareuser.user_user (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE CASCADE
);

/**
 * user_login: view used by UI for login authentication purposes
 */
CREATE VIEW eniwareuser.user_login AS
	SELECT
		email::text AS username,
		password AS password,
		enabled AS enabled,
		id AS user_id,
		disp_name AS display_name
	FROM eniwareuser.user_user;

/**
 * user_login_role: view used by UI for login authorization purposes
 */
CREATE VIEW eniwareuser.user_login_role AS
	SELECT u.email::text AS username, r.role_name AS authority
	FROM eniwareuser.user_user u
	INNER JOIN eniwareuser.user_role r ON r.user_id = u.id;

/* === USER AUTH TOKEN ===================================================== */

CREATE TYPE eniwareuser.user_auth_token_status AS ENUM
	('Active', 'Disabled');

CREATE TYPE eniwareuser.user_auth_token_type AS ENUM
	('User', 'ReadEdgeData');

CREATE TABLE eniwareuser.user_auth_token (
	auth_token		CHARACTER(20) NOT NULL,
	created			TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	user_id			BIGINT NOT NULL,
	auth_secret		CHARACTER VARYING(32) NOT NULL,
	status			eniwareuser.user_auth_token_status NOT NULL,
	token_type		eniwareuser.user_auth_token_type NOT NULL,
	jpolicy			json,
	CONSTRAINT user_auth_token_pkey PRIMARY KEY (auth_token),
	CONSTRAINT user_auth_token_user_fk FOREIGN KEY (user_id)
		REFERENCES eniwareuser.user_user (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE OR REPLACE VIEW eniwareuser.user_auth_token_login AS
	SELECT t.auth_token AS username,
		t.auth_secret AS password,
		u.enabled,
		u.id AS user_id,
		u.disp_name AS display_name,
		t.token_type::character varying AS token_type,
		t.jpolicy
	 FROM eniwareuser.user_auth_token t
		 JOIN eniwareuser.user_user u ON u.id = t.user_id
	WHERE t.status = 'Active'::eniwareuser.user_auth_token_status;

CREATE VIEW eniwareuser.user_auth_token_role AS
	SELECT
		t.auth_token AS username,
		'ROLE_'::text || upper(t.token_type::character varying::text) AS authority
	FROM eniwareuser.user_auth_token t
	UNION
	SELECT
		t.auth_token AS username,
		r.role_name AS authority
	FROM eniwareuser.user_auth_token t
	JOIN eniwareuser.user_role r ON r.user_id = t.user_id AND t.token_type = 'User'::eniwareuser.user_auth_token_type;

CREATE OR REPLACE FUNCTION eniwareuser.snws2_signing_key(sign_date date, secret text)
RETURNS bytea AS $$
	SELECT hmac('snws2_request', hmac(to_char(sign_date, 'YYYYMMDD'), 'SNWS2' || secret, 'sha256'), 'sha256');
$$ LANGUAGE SQL STRICT IMMUTABLE;

CREATE OR REPLACE FUNCTION eniwareuser.snws2_signing_key_hex(sign_date date, secret text)
RETURNS text AS $$
	SELECT encode(eniwareuser.snws2_signing_key(sign_date, secret), 'hex');
$$ LANGUAGE SQL STRICT IMMUTABLE;

/* === USER Edge =========================================================== */

CREATE TABLE eniwareuser.user_Edge (
	Edge_id			BIGINT NOT NULL,
	user_id			BIGINT NOT NULL,
	created			TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	disp_name		CHARACTER VARYING(128),
	description		CHARACTER VARYING(512),
	private 		BOOLEAN NOT NULL DEFAULT FALSE,
	archived		BOOLEAN NOT NULL DEFAULT FALSE,
	CONSTRAINT user_Edge_pkey PRIMARY KEY (Edge_id),
	CONSTRAINT user_Edge_user_fk FOREIGN KEY (user_id)
		REFERENCES eniwareuser.user_user (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT user_Edge_Edge_fk FOREIGN KEY (Edge_id)
		REFERENCES eniwarenet.sn_Edge (Edge_id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION
);

/* Add index on user_Edge to assist finding all Edges for a given user. */
CREATE INDEX user_Edge_user_idx ON eniwareuser.user_Edge (user_id);

/* === USER Edge CONF ======================================================
 * Note the Edge_id is NOT a foreign key to the Edge table, because the ID
 * is assigned before the Edge is created (and may never be created if not
 * confirmed by the user).
 */

CREATE TABLE eniwareuser.user_Edge_conf (
	id				BIGINT NOT NULL DEFAULT nextval('eniwareuser.eniwareuser_seq'),
	user_id			BIGINT NOT NULL,
	Edge_id			BIGINT,
	created			TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	conf_key		CHARACTER VARYING(1024) NOT NULL,
	conf_date		TIMESTAMP WITH TIME ZONE,
	sec_phrase 		CHARACTER VARYING(128) NOT NULL,
	country			CHARACTER(2) NOT NULL,
	time_zone		CHARACTER VARYING(64) NOT NULL,
	CONSTRAINT user_Edge_conf_pkey PRIMARY KEY (id),
	CONSTRAINT user_Edge_conf_user_fk FOREIGN KEY (user_id)
		REFERENCES eniwareuser.user_user (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT user_Edge_conf_unq UNIQUE (user_id, conf_key)
);


/* === NETWORK ASSOCIATION VIEW ============================================
 * Supporting view for the network association process.
 */

CREATE VIEW eniwareuser.network_association  AS
	SELECT
		u.email::text AS username,
		unc.conf_key AS conf_key,
		unc.sec_phrase AS sec_phrase
	FROM eniwareuser.user_Edge_conf unc
	INNER JOIN eniwareuser.user_user u ON u.id = unc.user_id;


/* === USER Edge CERT ======================================================
 * Holds user Edge certificates.
 */

CREATE TABLE eniwareuser.user_Edge_cert (
	created			TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	user_id			BIGINT NOT NULL,
	Edge_id			BIGINT NOT NULL,
	status			CHAR(1) NOT NULL,
	request_id		VARCHAR(32) NOT NULL,
	keystore		bytea,
	CONSTRAINT user_Edge_cert_pkey PRIMARY KEY (user_id, Edge_id),
	CONSTRAINT user_cert_user_fk FOREIGN KEY (user_id)
		REFERENCES eniwareuser.user_user (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE CASCADE
);

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

/* === USER Edge TRANSFER ======================================================
 * Holds ownership transfer requests for user Edges.
 */

CREATE TABLE eniwareuser.user_Edge_xfer (
	created			TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	user_id			BIGINT NOT NULL,
	Edge_id			BIGINT NOT NULL,
	recipient		citext NOT NULL,
	CONSTRAINT user_Edge_xfer_pkey PRIMARY KEY (user_id, Edge_id),
	CONSTRAINT user_Edge_xfer_user_fk FOREIGN KEY (user_id)
		REFERENCES eniwareuser.user_user (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE INDEX user_Edge_xfer_recipient_idx ON eniwareuser.user_Edge_xfer (recipient);

/**************************************************************************************************
 * FUNCTION eniwareuser.store_user_Edge_xfer(bigint, bigint, varchar, varchar)
 *
 * Insert or update a user Edge transfer record.
 *
 * @param Edge The ID of the Edge.
 * @param userid The ID of the user.
 * @param recip The recipient email of the requested owner.
 */
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

/**
 * Return most recent datum records for all available sources for all Edges owned by a given user ID.
 *
 * @param users An array of user IDs to return results for.
 * @returns Set of eniwaredatum.da_datum records.
 */
CREATE OR REPLACE FUNCTION eniwareuser.find_most_recent_datum_for_user(users bigint[])
  RETURNS SETOF eniwaredatum.da_datum_data AS
$BODY$
	SELECT r.*
	FROM (SELECT Edge_id FROM eniwareuser.user_Edge WHERE user_id = ANY(users)) AS n,
	LATERAL (SELECT * FROM eniwaredatum.find_most_recent(n.Edge_id)) AS r
	ORDER BY r.Edge_id, r.source_id;
$BODY$
  LANGUAGE sql STABLE;

/**
 * TRIGGER function that automatically transfers rows related to a user_Edge to
 * the new owner when the user_id value is changed. Expected record is eniwareuser.uesr_Edge.
 */
CREATE OR REPLACE FUNCTION eniwareuser.Edge_ownership_transfer()
  RETURNS "trigger" AS
$BODY$
BEGIN
	UPDATE eniwareuser.user_Edge_cert
	SET user_id = NEW.user_id
	WHERE user_id = OLD.user_id
		AND Edge_id = NEW.Edge_id;

	UPDATE eniwareuser.user_Edge_conf
	SET user_id = NEW.user_id
	WHERE user_id = OLD.user_id
		AND Edge_id = NEW.Edge_id;

	RETURN NEW;
END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE;

CREATE TRIGGER Edge_ownership_transfer
  BEFORE UPDATE
  ON eniwareuser.user_Edge
  FOR EACH ROW
  WHEN (OLD.user_id IS DISTINCT FROM NEW.user_id)
  EXECUTE PROCEDURE eniwareuser.Edge_ownership_transfer();
