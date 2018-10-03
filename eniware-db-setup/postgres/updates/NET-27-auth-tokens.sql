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
	CONSTRAINT user_auth_token_pkey PRIMARY KEY (auth_token),
	CONSTRAINT user_auth_token_user_fk FOREIGN KEY (user_id)
		REFERENCES eniwareuser.user_user (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE OR REPLACE VIEW eniwareuser.user_auth_token_login  AS
	SELECT
		t.auth_token AS username,
		t.auth_secret AS password, 
		u.enabled AS enabled,
		u.id AS user_id,
		u.disp_name AS display_name,
		CAST(t.token_type AS character varying) AS token_type
	FROM eniwareuser.user_auth_token t
	INNER JOIN eniwareuser.user_user u ON u.id = t.user_id
	WHERE 
		t.status = CAST('Active' AS eniwareuser.user_auth_token_status);

CREATE VIEW eniwareuser.user_auth_token_role AS
	SELECT
		t.auth_token AS username,
		'ROLE_' || upper(CAST(t.token_type AS character varying)) AS authority
	FROM eniwareuser.user_auth_token t;
		
ALTER TABLE eniwareuser.user_Edge_conf ADD COLUMN sec_phrase CHARACTER VARYING(128);
UPDATE eniwareuser.user_Edge_conf SET sec_phrase = 'changeit';
ALTER TABLE eniwareuser.user_Edge_conf ALTER COLUMN sec_phrase SET NOT NULL;

ALTER TABLE eniwareuser.user_Edge_conf
ALTER COLUMN Edge_id DROP NOT NULL;

ALTER TABLE eniwareuser.user_Edge_conf
DROP COLUMN conf_val;

CREATE OR REPLACE VIEW eniwareuser.network_association  AS
	SELECT
		u.email AS username,
		unc.conf_key AS conf_key,
		unc.sec_phrase AS sec_phrase
	FROM eniwareuser.user_Edge_conf unc
	INNER JOIN eniwareuser.user_user u ON u.id = unc.user_id;

CREATE TABLE eniwareuser.user_Edge_cert (
	id				BIGINT NOT NULL DEFAULT nextval('eniwareuser.eniwareuser_seq'),
	created			TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	user_id			BIGINT NOT NULL,
	conf_key		CHARACTER(64) NOT NULL,
	Edge_id			BIGINT NOT NULL,
	status			CHAR(1) NOT NULL,
	cert			bytea,
	CONSTRAINT user_Edge_cert_pkey PRIMARY KEY (id),
	CONSTRAINT user_cert_user_fk FOREIGN KEY (user_id)
		REFERENCES eniwareuser.user_user (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT user_Edge_cert_unq UNIQUE (user_id, conf_key)
);
