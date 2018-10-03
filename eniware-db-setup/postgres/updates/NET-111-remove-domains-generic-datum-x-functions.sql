DROP FUNCTION eniwaredatum.find_most_recent(eniwarecommon.Edge_id, eniwarecommon.source_ids);
CREATE OR REPLACE FUNCTION eniwaredatum.find_most_recent(
	Edge bigint,
	sources text[] DEFAULT NULL)
  RETURNS SETOF eniwaredatum.da_datum AS
$BODY$
	SELECT dd.* FROM eniwaredatum.da_datum dd
	INNER JOIN (
		-- to speed up query for sources (which can be very slow when queried directly on da_datum),
		-- we find the most recent hour time slot in agg_datum_hourly, and then join to da_datum with that narrow time range
		SELECT max(d.ts) as ts, d.source_id FROM eniwaredatum.da_datum d
		INNER JOIN (SELECT Edge_id, ts_start, source_id FROM eniwareagg.find_most_recent_hourly(Edge, sources)) AS days
			ON days.Edge_id = d.Edge_id
				AND days.ts_start <= d.ts
				AND days.ts_start + interval '1 hour' > d.ts
				AND days.source_id = d.source_id
		GROUP BY d.source_id
	) AS r ON r.ts = dd.ts AND r.source_id = dd.source_id AND dd.Edge_id = Edge
	ORDER BY dd.source_id ASC;
$BODY$
  LANGUAGE sql STABLE
  ROWS 20;

DROP FUNCTION eniwaredatum.find_most_recent(eniwarecommon.Edge_ids);
CREATE OR REPLACE FUNCTION eniwaredatum.find_most_recent(Edges bigint[])
  RETURNS SETOF eniwaredatum.da_datum AS
$BODY$
	SELECT r.*
	FROM (SELECT unnest(Edges) AS Edge_id) AS n,
	LATERAL (SELECT * FROM eniwaredatum.find_most_recent(n.Edge_id)) AS r
	ORDER BY r.Edge_id, r.source_id;
$BODY$
  LANGUAGE sql STABLE;

DROP FUNCTION eniwaredatum.store_datum(eniwarecommon.ts, eniwarecommon.Edge_id, eniwarecommon.source_id, eniwarecommon.ts, text);
CREATE OR REPLACE FUNCTION eniwaredatum.store_datum(
	cdate timestamp with time zone,
	Edge bigint,
	src text,
	pdate timestamp with time zone,
	jdata text)
  RETURNS void LANGUAGE plpgsql VOLATILE AS
$BODY$
DECLARE
	ts_crea timestamp with time zone := COALESCE(cdate, now());
	ts_post timestamp with time zone := COALESCE(pdate, now());
	jdata_json json := jdata::json;
	jdata_prop_count integer := eniwaredatum.datum_prop_count(jdata_json);
	ts_post_hour timestamp with time zone := date_trunc('hour', ts_post);
BEGIN
	INSERT INTO eniwaredatum.da_datum(ts, Edge_id, source_id, posted, jdata)
	VALUES (ts_crea, Edge, src, ts_post, jdata_json)
	ON CONFLICT (Edge_id, ts, source_id) DO UPDATE
	SET jdata = EXCLUDED.jdata, posted = EXCLUDED.posted;

	INSERT INTO eniwareagg.aud_datum_hourly (
		ts_start, Edge_id, source_id, prop_count)
	VALUES (ts_post_hour, Edge, src, jdata_prop_count)
	ON CONFLICT (Edge_id, ts_start, source_id) DO UPDATE
	SET prop_count = aud_datum_hourly.prop_count + EXCLUDED.prop_count;
END;
$BODY$;
