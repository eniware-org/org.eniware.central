ALTER TABLE eniwareagg.aud_datum_hourly
	ALTER COLUMN prop_count SET DEFAULT 0,
	ADD COLUMN datum_q_count integer NOT NULL DEFAULT 0;

CREATE OR REPLACE FUNCTION eniwareagg.aud_inc_datum_query_count(
	qdate timestamp with time zone,
	Edge bigint,
	source text,
	dcount integer)
	RETURNS void LANGUAGE sql VOLATILE AS
$BODY$
	INSERT INTO eniwareagg.aud_datum_hourly(ts_start, Edge_id, source_id, datum_q_count)
	VALUES (date_trunc('hour', qdate), Edge, source, dcount)
	ON CONFLICT (Edge_id, ts_start, source_id) DO UPDATE
	SET datum_q_count = aud_datum_hourly.datum_q_count + EXCLUDED.datum_q_count;
$BODY$;
