ALTER TABLE eniwareuser.user_Edge
   ADD COLUMN private boolean NOT NULL DEFAULT FALSE;

CREATE TABLE eniwareuser.user_auth_token_Edge (
	auth_token CHARACTER(20) NOT NULL, 
	Edge_id BIGINT NOT NULL,
	CONSTRAINT user_auth_token_Edge_pkey PRIMARY KEY (auth_token, Edge_id),
	CONSTRAINT user_auth_token_Edge_token_fk FOREIGN KEY (auth_token)
		REFERENCES eniwareuser.user_auth_token(auth_token) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE CASCADE,
	CONSTRAINT user_auth_token_Edge_Edge_fk FOREIGN KEY (Edge_id)
		REFERENCES eniwarenet.sn_Edge (Edge_id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE OR REPLACE VIEW eniwareuser.user_auth_token_login  AS
	SELECT
		t.auth_token AS username,
		t.auth_secret AS password, 
		u.enabled AS enabled,
		u.id AS user_id,
		u.disp_name AS display_name,
		CAST(t.token_type AS character varying) AS token_type,
		ARRAY(SELECT n.Edge_id 
			FROM eniwareuser.user_auth_token_Edge n 
			WHERE n.auth_token = t.auth_token) AS Edge_ids
	FROM eniwareuser.user_auth_token t
	INNER JOIN eniwareuser.user_user u ON u.id = t.user_id
	WHERE 
		t.status = CAST('Active' AS eniwareuser.user_auth_token_status);

