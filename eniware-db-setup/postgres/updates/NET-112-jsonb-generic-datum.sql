ALTER TABLE eniwaredatum.da_meta
  ALTER COLUMN jdata SET DATA TYPE jsonb;

DROP VIEW eniwareagg.da_datum_avail_hourly;
DROP VIEW eniwareagg.da_datum_avail_daily;
DROP VIEW eniwareagg.da_datum_avail_monthly;

CREATE OR REPLACE FUNCTION eniwaredatum.jdata_from_datum(datum eniwaredatum.da_datum)
	RETURNS jsonb
	LANGUAGE SQL IMMUTABLE AS
$$
	SELECT eniwarecommon.jdata_from_components(datum.jdata_i, datum.jdata_a, datum.jdata_s, datum.jdata_t);
$$;

CREATE OR REPLACE FUNCTION eniwareagg.jdata_from_datum(datum eniwareagg.agg_datum_hourly)
	RETURNS jsonb
	LANGUAGE SQL IMMUTABLE AS
$$
	SELECT eniwarecommon.jdata_from_components(datum.jdata_i, datum.jdata_a, datum.jdata_s, datum.jdata_t);
$$;

CREATE OR REPLACE FUNCTION eniwareagg.jdata_from_datum(datum eniwareagg.agg_datum_daily)
	RETURNS jsonb
	LANGUAGE SQL IMMUTABLE AS
$$
	SELECT eniwarecommon.jdata_from_components(datum.jdata_i, datum.jdata_a, datum.jdata_s, datum.jdata_t);
$$;

CREATE OR REPLACE FUNCTION eniwareagg.jdata_from_datum(datum eniwareagg.agg_datum_monthly)
	RETURNS jsonb
	LANGUAGE SQL IMMUTABLE AS
$$
	SELECT eniwarecommon.jdata_from_components(datum.jdata_i, datum.jdata_a, datum.jdata_s, datum.jdata_t);
$$;

CREATE OR REPLACE FUNCTION eniwaredatum.store_meta(
	cdate timestamp with time zone,
	Edge bigint,
	src text,
	jdata text)
  RETURNS void LANGUAGE plpgsql VOLATILE AS
$BODY$
DECLARE
	udate timestamp with time zone := now();
	jdata_json jsonb := jdata::jsonb;
BEGIN
	INSERT INTO eniwaredatum.da_meta(Edge_id, source_id, created, updated, jdata)
	VALUES (Edge, src, cdate, udate, jdata_json)
	ON CONFLICT (Edge_id, source_id) DO UPDATE
	SET jdata = EXCLUDED.jdata, updated = EXCLUDED.updated;
END;
$BODY$;

DROP FUNCTION eniwaredatum.datum_prop_count(json);
CREATE OR REPLACE FUNCTION eniwaredatum.datum_prop_count(IN jdata jsonb)
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
