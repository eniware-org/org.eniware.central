CREATE OR REPLACE VIEW eniwareuser.million_metric_avg_hour_costs AS
 SELECT a.Edge_id,
    max(u.email) AS owner,
    round(avg(a.prop_count))::integer AS avg_hourly_prop_count,
    (avg(a.prop_count) * 24::numeric * 30::numeric / 1000000::numeric * 7::numeric)::numeric(6,2) AS month_cost7,
    (avg(a.prop_count) * 24::numeric * 30::numeric / 1000000::numeric * 10::numeric)::numeric(6,2) AS month_cost10
   FROM eniwareagg.aud_datum_hourly a
     JOIN eniwareuser.user_Edge un ON un.Edge_id = a.Edge_id
     JOIN eniwareuser.user_user u ON u.id = un.user_id
  GROUP BY a.Edge_id
  ORDER BY (round(avg(a.prop_count))::integer) DESC;

CREATE OR REPLACE VIEW eniwareuser.million_metric_monthly_costs AS
 SELECT date_trunc('month'::text, timezone('UTC'::text, a.ts_start))::date AS month,
    u.email AS owner,
    a.Edge_id AS Edge,
    sum(a.prop_count) AS total_prop_count,
    round(sum(a.prop_count)::double precision / (date_part('epoch'::text, date_trunc('month'::text, timezone('UTC'::text, a.ts_start))::date + '1 mon'::interval - date_trunc('month'::text, timezone('UTC'::text, a.ts_start))::date::timestamp without time zone) / 3600::double precision))::integer AS avg_hourly_prop_count,
    (sum(a.prop_count)::numeric / 1000000::numeric * 10::numeric)::numeric(6,2) AS cost
   FROM eniwareagg.aud_datum_hourly a
     JOIN eniwareuser.user_Edge un ON un.Edge_id = a.Edge_id::bigint
     JOIN eniwareuser.user_user u ON u.id = un.user_id
  GROUP BY ROLLUP(u.email, (date_trunc('month'::text, timezone('UTC'::text, a.ts_start))::date), a.Edge_id)
  ORDER BY u.email, (date_trunc('month'::text, timezone('UTC'::text, a.ts_start))::date), a.Edge_id;

CREATE OR REPLACE VIEW eniwareuser.user_alert_info AS
 SELECT al.id AS alert_id,
    al.user_id,
    al.Edge_id,
    al.alert_type,
    al.status AS alert_status,
    al.alert_opt,
    un.disp_name AS Edge_name,
    l.time_zone AS Edge_tz,
    u.disp_name AS user_name,
    u.email
   FROM eniwareuser.user_alert al
     JOIN eniwareuser.user_user u ON al.user_id = u.id
     LEFT JOIN eniwareuser.user_Edge un ON un.user_id = al.user_id AND un.Edge_id = al.Edge_id
     LEFT JOIN eniwarenet.sn_Edge n ON al.Edge_id = n.Edge_id
     LEFT JOIN eniwarenet.sn_loc l ON l.id = n.loc_id
  ORDER BY al.user_id, al.Edge_id;
