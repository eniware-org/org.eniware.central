ALTER TABLE eniwareuser.user_user
  ADD COLUMN loc_id bigint;

ALTER TABLE eniwareuser.user_user
  ADD COLUMN jdata jsonb;

CREATE INDEX user_user_jdata_idx ON eniwareuser.user_user
	USING GIN (jdata jsonb_path_ops);

ALTER TABLE eniwareuser.user_user
  ADD CONSTRAINT user_user_loc_fk FOREIGN KEY (loc_id) REFERENCES eniwarenet.sn_loc (id)
  ON UPDATE NO ACTION ON DELETE NO ACTION;

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

CREATE OR REPLACE FUNCTION eniwareagg.find_audit_datum_interval(
	IN Edge eniwarecommon.Edge_id,
	IN src eniwarecommon.source_id DEFAULT NULL,
	OUT ts_start eniwarecommon.ts,
	OUT ts_end eniwarecommon.ts,
	OUT Edge_tz TEXT,
	OUT Edge_tz_offset INTEGER)
  RETURNS RECORD AS
$BODY$
BEGIN
	CASE
		WHEN src IS NULL THEN
			SELECT min(a.ts_start) FROM eniwareagg.aud_datum_hourly a WHERE Edge_id = Edge
			INTO ts_start;
		ELSE
			SELECT min(a.ts_start) FROM eniwareagg.aud_datum_hourly a WHERE Edge_id = Edge AND source_id = src
			INTO ts_start;
	END CASE;

	CASE
		WHEN src IS NULL THEN
			SELECT max(a.ts_start) FROM eniwareagg.aud_datum_hourly a WHERE Edge_id = Edge
			INTO ts_end;
		ELSE
			SELECT max(a.ts_start) FROM eniwareagg.aud_datum_hourly a WHERE Edge_id = Edge AND source_id = src
			INTO ts_end;
	END CASE;

	SELECT
		l.time_zone,
		CAST(EXTRACT(epoch FROM z.utc_offset) / 60 AS INTEGER)
	FROM eniwarenet.sn_Edge n
	INNER JOIN eniwarenet.sn_loc l ON l.id = n.loc_id
	INNER JOIN pg_timezone_names z ON z.name = l.time_zone
	WHERE n.Edge_id = Edge
	INTO Edge_tz, Edge_tz_offset;

	IF NOT FOUND THEN
		Edge_tz := 'UTC';
		Edge_tz_offset := 0;
	END IF;

END;$BODY$
  LANGUAGE plpgsql STABLE;

