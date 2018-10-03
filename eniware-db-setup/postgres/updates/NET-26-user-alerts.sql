/* Add index on user_Edge to assist finding all Edges for a given user. */
CREATE INDEX user_Edge_user_idx ON eniwareuser.user_Edge (user_id);

/**
 * Return most recent datum records for all available sources for all Edges owned by a given user ID.
 * 
 * @param users An array of user IDs to return results for.
 * @returns Set of eniwaredatum.da_datum records.
 */
CREATE OR REPLACE FUNCTION eniwareuser.find_most_recent_datum_for_user(users bigint[])
  RETURNS SETOF eniwaredatum.da_datum AS
$BODY$
	SELECT r.* 
	FROM (SELECT Edge_id FROM eniwareuser.user_Edge WHERE user_id = ANY(users)) AS n,
	LATERAL (SELECT * FROM eniwaredatum.find_most_recent(n.Edge_id)) AS r
	ORDER BY r.Edge_id, r.source_id;
$BODY$
  LANGUAGE sql STABLE;

/**
 * Return most recent datum records for all available sources for a given set of Edge IDs.
 * 
 * @param Edges An array of Edge IDs to return results for.
 * @returns Set of eniwaredatum.da_datum records.
 */
CREATE OR REPLACE FUNCTION eniwaredatum.find_most_recent(Edges eniwarecommon.Edge_ids)
  RETURNS SETOF eniwaredatum.da_datum AS
$BODY$
	SELECT r.* 
	FROM (SELECT unnest(Edges) AS Edge_id) AS n,
	LATERAL (SELECT * FROM eniwaredatum.find_most_recent(n.Edge_id)) AS r
	ORDER BY r.Edge_id, r.source_id;
$BODY$
  LANGUAGE sql STABLE;

/* === USER ALERTS ===================================================== */

CREATE TYPE eniwareuser.user_alert_status AS ENUM 
	('Active', 'Disabled', 'Suppressed');

CREATE TYPE eniwareuser.user_alert_type AS ENUM 
	('EdgeStaleData');

CREATE TYPE eniwareuser.user_alert_sit_status AS ENUM 
	('Active', 'Resolved');

CREATE SEQUENCE eniwareuser.user_alert_seq;

CREATE TABLE eniwareuser.user_alert (
	id				BIGINT NOT NULL DEFAULT nextval('eniwareuser.user_alert_seq'),
	created			TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	user_id			BIGINT NOT NULL,
	Edge_id			BIGINT,
	valid_to		TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	alert_type		eniwareuser.user_alert_type NOT NULL,
	status			eniwareuser.user_alert_status NOT NULL,
	alert_opt		json,
	CONSTRAINT user_alert_pkey PRIMARY KEY (id),
	CONSTRAINT user_alert_user_fk FOREIGN KEY (user_id)
		REFERENCES eniwareuser.user_user (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE CASCADE,
	CONSTRAINT user_alert_Edge_fk FOREIGN KEY (Edge_id)
		REFERENCES eniwarenet.sn_Edge (Edge_id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION
);

/* Add index on Edge_id so we can batch process in sets of Edges. */
CREATE INDEX user_alert_Edge_idx ON eniwareuser.user_alert (Edge_id);

/* Add index on valid_to so we can batch process only those alerts that need validation. */
CREATE INDEX user_alert_valid_idx ON eniwareuser.user_alert (valid_to);

/* Add index on user_id so we can show all alerts to user. */
CREATE INDEX user_alert_user_idx ON eniwareuser.user_alert (user_id);


CREATE TABLE eniwareuser.user_alert_sit (
	id				BIGINT NOT NULL DEFAULT nextval('eniwareuser.user_alert_seq'),
	created			TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	alert_id		BIGINT NOT NULL,
	status			eniwareuser.user_alert_sit_status NOT NULL,
	notified		TIMESTAMP WITH TIME ZONE,
	CONSTRAINT user_alert_sit_pkey PRIMARY KEY (id),
	CONSTRAINT user_alert_sit_alert_fk FOREIGN KEY (alert_id)
		REFERENCES eniwareuser.user_alert (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE CASCADE
);

/* Add index on alert_id, created so we can quickly get most recent for given alert. */
CREATE INDEX user_alert_sit_alert_created_idx ON eniwareuser.user_alert_sit (alert_id, created DESC);

/* Add a partial index on notified to support purging resolved situations. */
CREATE INDEX user_alert_sit_notified_idx ON eniwareuser.user_alert_sit (notified)
WHERE (notified is not null);

/**************************************************************************************************
 * FUNCTION eniwareuser.purge_resolved_situations(timestamp with time zone)
 * 
 * Delete user_alert_sit rows that have reached the Resolved state, and whose 
 * notified date is older than the given date.
 * 
 * @param older_date The maximum date to delete situations for.
 * @return The number of situations deleted.
 */
CREATE OR REPLACE FUNCTION eniwareuser.purge_resolved_situations(older_date timestamp with time zone)
  RETURNS BIGINT AS
$BODY$
DECLARE
	num_rows BIGINT := 0;
BEGIN
	DELETE FROM eniwareuser.user_alert_sit
	WHERE notified < older_date
		AND status = 'Resolved'::eniwareuser.user_alert_sit_status;
	GET DIAGNOSTICS num_rows = ROW_COUNT;
	RETURN num_rows;
END;$BODY$
  LANGUAGE plpgsql VOLATILE;
