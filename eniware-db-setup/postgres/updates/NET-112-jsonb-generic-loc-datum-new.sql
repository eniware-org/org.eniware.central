CREATE VIEW eniwaredatum.da_loc_datum_data AS
    SELECT d.ts, d.loc_id, d.source_id, d.posted, eniwaredatum.jdata_from_datum(d) AS jdata
    FROM eniwaredatum.da_loc_datum d;

CREATE VIEW eniwareagg.agg_loc_datum_hourly_data AS
    SELECT d.ts_start, d.local_date, d.loc_id, d.source_id, eniwareagg.jdata_from_datum(d) AS jdata
    FROM eniwareagg.agg_loc_datum_hourly d;

CREATE VIEW eniwareagg.agg_loc_datum_daily_data AS
    SELECT d.ts_start, d.local_date, d.loc_id, d.source_id, eniwareagg.jdata_from_datum(d) AS jdata
    FROM eniwareagg.agg_loc_datum_daily d;

CREATE VIEW eniwareagg.agg_loc_datum_monthly_data AS
    SELECT d.ts_start, d.local_date, d.loc_id, d.source_id, eniwareagg.jdata_from_datum(d) AS jdata
    FROM eniwareagg.agg_loc_datum_monthly d;

