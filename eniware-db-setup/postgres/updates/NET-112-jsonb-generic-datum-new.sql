CREATE VIEW eniwaredatum.da_datum_data AS
    SELECT d.ts, d.Edge_id, d.source_id, d.posted, eniwaredatum.jdata_from_datum(d) AS jdata
    FROM eniwaredatum.da_datum d;

CREATE VIEW eniwareagg.agg_datum_hourly_data AS
  SELECT d.ts_start, d.local_date, d.Edge_id, d.source_id, eniwareagg.jdata_from_datum(d) AS jdata
  FROM eniwareagg.agg_datum_hourly d;

CREATE VIEW eniwareagg.agg_datum_daily_data AS
  SELECT d.ts_start, d.local_date, d.Edge_id, d.source_id, eniwareagg.jdata_from_datum(d) AS jdata
  FROM eniwareagg.agg_datum_daily d;

CREATE VIEW eniwareagg.agg_datum_monthly_data AS
  SELECT d.ts_start, d.local_date, d.Edge_id, d.source_id, eniwareagg.jdata_from_datum(d) AS jdata
  FROM eniwareagg.agg_datum_monthly d;

