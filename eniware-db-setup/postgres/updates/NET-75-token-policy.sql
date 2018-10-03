DROP VIEW eniwareuser.user_auth_token_login;

ALTER TABLE eniwareuser.user_auth_token
	ADD COLUMN jpolicy json;

UPDATE eniwareuser.user_auth_token SET jpolicy = (
	('{"EdgeIds":' || array_to_json(ARRAY(SELECT n.Edge_id 
				FROM eniwareuser.user_auth_token_Edge n 
				WHERE n.auth_token = user_auth_token.auth_token)) || '}')::json
	)
WHERE (SELECT count(*) FROM eniwareuser.user_auth_token_Edge n WHERE n.auth_token = user_auth_token.auth_token) > 0;

DROP TABLE eniwareuser.user_auth_token_Edge;

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

