CREATE OR REPLACE VIEW solaruser.million_metric_avg_hour_costs AS
 SELECT a.node_id,
    max(u.email) AS owner,
    round(avg(a.prop_count))::integer AS avg_hourly_prop_count,
    (avg(a.prop_count) * 24::numeric * 30::numeric / 1000000::numeric * 7::numeric)::numeric(6,2) AS month_cost7,
    (avg(a.prop_count) * 24::numeric * 30::numeric / 1000000::numeric * 10::numeric)::numeric(6,2) AS month_cost10
   FROM solaragg.aud_datum_hourly a
     JOIN solaruser.user_node un ON un.node_id = a.node_id
     JOIN solaruser.user_user u ON u.id = un.user_id
  GROUP BY a.node_id
  ORDER BY (round(avg(a.prop_count))::integer) DESC;

CREATE OR REPLACE VIEW solaruser.million_metric_monthly_costs AS
 SELECT date_trunc('month'::text, timezone('UTC'::text, a.ts_start))::date AS month,
    u.email AS owner,
    a.node_id AS node,
    sum(a.prop_count) AS total_prop_count,
    round(sum(a.prop_count)::double precision / (date_part('epoch'::text, date_trunc('month'::text, timezone('UTC'::text, a.ts_start))::date + '1 mon'::interval - date_trunc('month'::text, timezone('UTC'::text, a.ts_start))::date::timestamp without time zone) / 3600::double precision))::integer AS avg_hourly_prop_count,
    (sum(a.prop_count)::numeric / 1000000::numeric * 10::numeric)::numeric(6,2) AS cost
   FROM solaragg.aud_datum_hourly a
     JOIN solaruser.user_node un ON un.node_id = a.node_id::bigint
     JOIN solaruser.user_user u ON u.id = un.user_id
  GROUP BY ROLLUP(u.email, (date_trunc('month'::text, timezone('UTC'::text, a.ts_start))::date), a.node_id)
  ORDER BY u.email, (date_trunc('month'::text, timezone('UTC'::text, a.ts_start))::date), a.node_id;

CREATE OR REPLACE VIEW solaruser.user_alert_info AS
 SELECT al.id AS alert_id,
    al.user_id,
    al.node_id,
    al.alert_type,
    al.status AS alert_status,
    al.alert_opt,
    un.disp_name AS node_name,
    l.time_zone AS node_tz,
    u.disp_name AS user_name,
    u.email
   FROM solaruser.user_alert al
     JOIN solaruser.user_user u ON al.user_id = u.id
     LEFT JOIN solaruser.user_node un ON un.user_id = al.user_id AND un.node_id = al.node_id
     LEFT JOIN solarnet.sn_node n ON al.node_id = n.node_id
     LEFT JOIN solarnet.sn_loc l ON l.id = n.loc_id
  ORDER BY al.user_id, al.node_id;
