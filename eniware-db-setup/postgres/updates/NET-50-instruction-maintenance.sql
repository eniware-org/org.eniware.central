/**************************************************************************************************
 * FUNCTION eniwarenet.purge_completed_instructions(timestamp with time zone)
 * 
 * Delete instructions that have reached the Declined or Completed state, and whose 
 * instruction date is older than the given date.
 * 
 * @param older_date The maximum date to delete instructions for.
 * @return The number of instructions deleted.
 */
CREATE OR REPLACE FUNCTION eniwarenet.purge_completed_instructions(older_date timestamp with time zone)
  RETURNS BIGINT AS
$BODY$
DECLARE
	num_rows BIGINT := 0;
BEGIN
	DELETE FROM eniwarenet.sn_Edge_instruction
	WHERE instr_date < older_date
		AND deliver_state IN (
			'Declined'::eniwarenet.instruction_delivery_state, 
			'Completed'::eniwarenet.instruction_delivery_state);
	GET DIAGNOSTICS num_rows = ROW_COUNT;
	RETURN num_rows;
END;$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE SEQUENCE eniwarenet.instruction_seq;

SELECT setval('eniwarenet.instruction_seq', (SELECT COALESCE(MAX(id), 1) FROM eniwarenet.sn_Edge_instruction), true);
ALTER TABLE eniwarenet.sn_Edge_instruction ALTER COLUMN id SET DEFAULT nextval('eniwarenet.instruction_seq');
