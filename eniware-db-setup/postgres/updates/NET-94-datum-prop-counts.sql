/**
 * Count the properties in a datum JSON object.
 *
 * @param jdata				the datum JSON
 *
 * @returns The property count.
 */
CREATE OR REPLACE FUNCTION eniwaredatum.datum_prop_count(IN jdata json)
  RETURNS INTEGER
  LANGUAGE plv8
  IMMUTABLE AS
$BODY$
'use strict';
var count = 0, prop, val;
if ( jdata ) {
	for ( prop in jdata ) {
		val = jdata[prop];
		if ( Array.isArray(val) ) {
			count += val.length;
		} else {
			count += Object.keys(val).length;
		}
	}
}
return count;
$BODY$;

CREATE TABLE eniwareagg.aud_datum_hourly (
  ts_start timestamp with time zone NOT NULL,
  Edge_id eniwarecommon.Edge_id NOT NULL,
  source_id eniwarecommon.source_id NOT NULL,
  prop_count integer NOT NULL,
  CONSTRAINT aud_datum_hourly_pkey PRIMARY KEY (Edge_id, ts_start, source_id) DEFERRABLE INITIALLY IMMEDIATE
);

CREATE OR REPLACE FUNCTION eniwaredatum.store_datum(
	cdate eniwarecommon.ts,
	Edge eniwarecommon.Edge_id,
	src eniwarecommon.source_id,
	pdate eniwarecommon.ts,
	jdata text)
  RETURNS void AS
$BODY$
DECLARE
	ts_crea eniwarecommon.ts := COALESCE(cdate, now());
	ts_post eniwarecommon.ts := COALESCE(pdate, now());
	jdata_json json := jdata::json;
	jdata_prop_count integer := eniwaredatum.datum_prop_count(jdata_json);
	ts_post_hour timestamp with time zone := date_trunc('hour', ts_post);
BEGIN
	BEGIN
		INSERT INTO eniwaredatum.da_datum(ts, Edge_id, source_id, posted, jdata)
		VALUES (ts_crea, Edge, src, ts_post, jdata_json);
	EXCEPTION WHEN unique_violation THEN
		-- We mostly expect inserts, but we allow updates
		UPDATE eniwaredatum.da_datum SET 
			jdata = jdata_json, 
			posted = ts_post
		WHERE
			Edge_id = Edge
			AND ts = ts_crea
			AND source_id = src;
	END;

	-- for auditing we mostly expect updates
	<<update_audit>>
	LOOP
		UPDATE eniwareagg.aud_datum_hourly 
		SET prop_count = prop_count + jdata_prop_count
		WHERE
			Edge_id = Edge
			AND source_id = src
			AND ts_start = ts_post_hour;

		EXIT update_audit WHEN FOUND;

		INSERT INTO eniwareagg.aud_datum_hourly (
			ts_start, Edge_id, source_id, prop_count)
		VALUES (
			ts_post_hour,
			Edge,
			src,
			jdata_prop_count
		);
		EXIT update_audit;
	END LOOP update_audit;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TABLE eniwareagg.aud_loc_datum_hourly (
  ts_start timestamp with time zone NOT NULL,
  loc_id eniwarecommon.loc_id NOT NULL,
  source_id eniwarecommon.source_id NOT NULL,
  prop_count integer NOT NULL,
  CONSTRAINT aud_loc_datum_hourly_pkey PRIMARY KEY (loc_id, ts_start, source_id) DEFERRABLE INITIALLY IMMEDIATE
);

CREATE OR REPLACE FUNCTION eniwaredatum.store_loc_datum(
	cdate eniwarecommon.ts,
	loc eniwarecommon.loc_id,
	src eniwarecommon.source_id,
	pdate eniwarecommon.ts,
	jdata text)
  RETURNS void AS
$BODY$
DECLARE
	ts_crea eniwarecommon.ts := COALESCE(cdate, now());
	ts_post eniwarecommon.ts := COALESCE(pdate, now());
	jdata_json json := jdata::json;
	jdata_prop_count integer := eniwaredatum.datum_prop_count(jdata_json);
	ts_post_hour timestamp with time zone := date_trunc('hour', ts_post);
BEGIN
	BEGIN
		INSERT INTO eniwaredatum.da_loc_datum(ts, loc_id, source_id, posted, jdata)
		VALUES (ts_crea, loc, src, ts_post, jdata_json);
	EXCEPTION WHEN unique_violation THEN
		-- We mostly expect inserts, but we allow updates
		UPDATE eniwaredatum.da_loc_datum SET
			jdata = jdata_json,
			posted = ts_post
		WHERE
			loc_id = loc
			AND ts = ts_crea
			AND source_id = src;
	END;
	
	-- for auditing we mostly expect updates
	<<update_audit>>
	LOOP
		UPDATE eniwareagg.aud_loc_datum_hourly 
		SET prop_count = prop_count + jdata_prop_count
		WHERE
			loc_id = loc
			AND source_id = src
			AND ts_start = ts_post_hour;

		EXIT update_audit WHEN FOUND;

		INSERT INTO eniwareagg.aud_loc_datum_hourly (
			ts_start, loc_id, source_id, prop_count)
		VALUES (
			ts_post_hour,
			loc,
			src,
			jdata_prop_count
		);
		EXIT update_audit;
	END LOOP update_audit;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE;
