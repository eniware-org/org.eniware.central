UPDATE eniwaredatum.da_datum
	SET jdata_i = jdata->'i', jdata_a = jdata->'a', jdata_s = jdata->'s',
		jdata_t = eniwarecommon.json_array_to_text_array(jdata->'t');

UPDATE eniwareagg.agg_datum_hourly
	SET jdata_i = jdata->'i', jdata_a = jdata->'a', jdata_s = jdata->'s',
		jdata_t = eniwarecommon.json_array_to_text_array(jdata->'t');

UPDATE eniwareagg.agg_datum_daily
	SET jdata_i = jdata->'i', jdata_a = jdata->'a', jdata_s = jdata->'s',
		jdata_t = eniwarecommon.json_array_to_text_array(jdata->'t');

UPDATE eniwareagg.agg_datum_monthly
	SET jdata_i = jdata->'i', jdata_a = jdata->'a', jdata_s = jdata->'s',
		jdata_t = eniwarecommon.json_array_to_text_array(jdata->'t');


UPDATE eniwaredatum.da_loc_datum
	SET jdata_i = jdata->'i', jdata_a = jdata->'a', jdata_s = jdata->'s',
		jdata_t = eniwarecommon.json_array_to_text_array(jdata->'t');

UPDATE eniwareagg.agg_loc_datum_hourly
	SET jdata_i = jdata->'i', jdata_a = jdata->'a', jdata_s = jdata->'s',
		jdata_t = eniwarecommon.json_array_to_text_array(jdata->'t');

UPDATE eniwareagg.agg_loc_datum_daily
	SET jdata_i = jdata->'i', jdata_a = jdata->'a', jdata_s = jdata->'s',
		jdata_t = eniwarecommon.json_array_to_text_array(jdata->'t');

UPDATE eniwareagg.agg_loc_datum_monthly
	SET jdata_i = jdata->'i', jdata_a = jdata->'a', jdata_s = jdata->'s',
		jdata_t = eniwarecommon.json_array_to_text_array(jdata->'t');

