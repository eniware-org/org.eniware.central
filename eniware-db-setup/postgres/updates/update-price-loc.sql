ALTER TABLE eniwarenet.sn_price_loc ADD COLUMN loc_id BIGINT;
UPDATE eniwarenet.sn_price_loc SET loc_id = 
	(SELECT id FROM eniwarenet.sn_loc WHERE country = '--' LIMIT 1);

ALTER TABLE eniwarenet.sn_price_loc
  ADD CONSTRAINT sn_price_loc_loc_fk FOREIGN KEY (loc_id)
      REFERENCES eniwarenet.sn_loc (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE eniwarenet.sn_price_loc DROP COLUMN time_zone;
ALTER TABLE eniwarenet.sn_price_loc ALTER COLUMN loc_id SET NOT NULL;
