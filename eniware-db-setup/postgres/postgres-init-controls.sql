CREATE SEQUENCE eniwarenet.hardware_control_seq;

CREATE TABLE eniwarenet.sn_hardware (
	id				BIGINT NOT NULL DEFAULT nextval('eniwarenet.eniwarenet_seq'),
	created			TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	manufact		CHARACTER VARYING(256) NOT NULL,
	model			CHARACTER VARYING(256) NOT NULL,
	revision		INTEGER DEFAULT 0 NOT NULL,
  	fts_default 	tsvector,
	CONSTRAINT sn_hardware_pkey PRIMARY KEY (id),
	CONSTRAINT sn_hardware_unq UNIQUE (manufact, model, revision)
);

CREATE INDEX sn_hardware_fts_default_idx ON eniwarenet.sn_hardware
USING gin(fts_default);

DROP TRIGGER IF EXISTS maintain_fts ON eniwarenet.sn_hardware;
CREATE TRIGGER maintain_fts 
  BEFORE INSERT OR UPDATE ON eniwarenet.sn_hardware FOR EACH ROW EXECUTE PROCEDURE 
  tsvector_update_trigger(fts_default, 'pg_catalog.english', manufact, model);

CREATE TABLE eniwarenet.sn_hardware_control (
	id				BIGINT NOT NULL DEFAULT nextval('eniwarenet.eniwarenet_seq'),
	created			TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	hw_id			BIGINT NOT NULL,
	ctl_name		CHARACTER VARYING(128) NOT NULL,
	unit			CHARACTER VARYING(16),
	CONSTRAINT sn_hardware_control_pkey PRIMARY KEY (id),
	CONSTRAINT sn_hardware_control_unq UNIQUE (hw_id, ctl_name),
	CONSTRAINT sn_hardware_control_hardware_fk
		FOREIGN KEY (hw_id) REFERENCES eniwarenet.sn_hardware (id)
		ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE eniwareuser.user_Edge_hardware_control (
	id				BIGINT NOT NULL DEFAULT nextval('eniwareuser.eniwareuser_seq'),
	created			TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	Edge_id			BIGINT NOT NULL,
	source_id		CHARACTER VARYING(128) NOT NULL,
	hwc_id			BIGINT NOT NULL,
	disp_name		CHARACTER VARYING(128),
	CONSTRAINT user_Edge_hardware_control_pkey PRIMARY KEY (id),
	CONSTRAINT user_Edge_hardware_control_Edge_unq UNIQUE (Edge_id,source_id),
	CONSTRAINT user_Edge_hardware_control_Edge_fk
		FOREIGN KEY (Edge_id) REFERENCES eniwarenet.sn_Edge (Edge_id)
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT user_Edge_hardware_control_hardware_control_fk
		FOREIGN KEY (hwc_id) REFERENCES eniwarenet.sn_hardware_control (id)
		ON UPDATE NO ACTION ON DELETE NO ACTION
);

