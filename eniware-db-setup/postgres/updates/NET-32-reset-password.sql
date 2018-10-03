DROP VIEW eniwareuser.user_login;
CREATE OR REPLACE VIEW eniwareuser.user_login AS
	SELECT
		email AS username, 
		password AS password, 
		enabled AS enabled,
		id AS user_id,
		disp_name AS display_name
	FROM eniwareuser.user_user;
