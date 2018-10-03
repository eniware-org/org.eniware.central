ALTER TABLE eniwareuser.user_Edge_conf
ADD COLUMN country CHARACTER(2) NOT NULL DEFAULT 'NZ';

ALTER TABLE eniwareuser.user_Edge_conf
ADD COLUMN time_zone CHARACTER VARYING(64) NOT NULL DEFAULT 'Pacific/Auckland';
