/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.instructor.biz.dao;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eniware.central.domain.EntityMatch;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.instructor.biz.InstructorBiz;
import org.eniware.central.instructor.dao.EdgeInstructionDao;
import org.eniware.central.instructor.domain.Instruction;
import org.eniware.central.instructor.domain.InstructionParameter;
import org.eniware.central.instructor.domain.InstructionState;
import org.eniware.central.instructor.domain.EdgeInstruction;
import org.eniware.central.instructor.support.SimpleInstructionFilter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO based implementation of {@link InstructorBiz}.
 * 
 * @version 1.4
 */
@Service
public class DaoInstructorBiz implements InstructorBiz {

	@Autowired
	private EdgeInstructionDao EdgeInstructionDao;

	private List<Instruction> asResultList(FilterResults<EntityMatch> matches) {
		List<Instruction> results = new ArrayList<Instruction>(matches.getReturnedResultCount());
		for ( EntityMatch match : matches.getResults() ) {
			if ( match instanceof Instruction ) {
				results.add((Instruction) match);
			} else {
				results.add(EdgeInstructionDao.get(match.getId()));
			}
		}
		return results;
	}

	private List<EdgeInstruction> asEdgeInstructionList(FilterResults<EntityMatch> matches) {
		List<EdgeInstruction> results = new ArrayList<EdgeInstruction>(matches.getReturnedResultCount());
		for ( EntityMatch match : matches.getResults() ) {
			if ( match instanceof EdgeInstruction ) {
				results.add((EdgeInstruction) match);
			} else {
				results.add(EdgeInstructionDao.get(match.getId()));
			}
		}
		return results;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public EdgeInstruction getInstruction(Long instructionId) {
		return EdgeInstructionDao.get(instructionId);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<EdgeInstruction> getInstructions(Set<Long> instructionIds) {
		Long[] ids = instructionIds.toArray(new Long[instructionIds.size()]);
		SimpleInstructionFilter filter = new SimpleInstructionFilter();
		filter.setInstructionIds(ids);
		FilterResults<EntityMatch> matches = EdgeInstructionDao.findFiltered(filter, null, null, null);
		return asEdgeInstructionList(matches);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<Instruction> getActiveInstructionsForEdge(Long EdgeId) {
		SimpleInstructionFilter filter = new SimpleInstructionFilter();
		filter.setEdgeId(EdgeId);
		filter.setState(InstructionState.Queued);
		FilterResults<EntityMatch> matches = EdgeInstructionDao.findFiltered(filter, null, null, null);
		return asResultList(matches);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<EdgeInstruction> getActiveInstructionsForEdges(Set<Long> EdgeIds) {
		Long[] ids = EdgeIds.toArray(new Long[EdgeIds.size()]);
		SimpleInstructionFilter filter = new SimpleInstructionFilter();
		filter.setEdgeIds(ids);
		filter.setState(InstructionState.Queued);
		FilterResults<EntityMatch> matches = EdgeInstructionDao.findFiltered(filter, null, null, null);
		return asEdgeInstructionList(matches);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<Instruction> getPendingInstructionsForEdge(Long EdgeId) {
		SimpleInstructionFilter filter = new SimpleInstructionFilter();
		filter.setEdgeId(EdgeId);
		filter.setStateSet(EnumSet.of(InstructionState.Queued, InstructionState.Received,
				InstructionState.Executing));
		FilterResults<EntityMatch> matches = EdgeInstructionDao.findFiltered(filter, null, null, null);
		return asResultList(matches);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<EdgeInstruction> getPendingInstructionsForEdges(Set<Long> EdgeIds) {
		Long[] ids = EdgeIds.toArray(new Long[EdgeIds.size()]);
		SimpleInstructionFilter filter = new SimpleInstructionFilter();
		filter.setEdgeIds(ids);
		filter.setStateSet(EnumSet.of(InstructionState.Queued, InstructionState.Received,
				InstructionState.Executing));
		FilterResults<EntityMatch> matches = EdgeInstructionDao.findFiltered(filter, null, null, null);
		return asEdgeInstructionList(matches);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public EdgeInstruction queueInstruction(Long EdgeId, Instruction instruction) {
		EdgeInstruction instr = new EdgeInstruction(instruction.getTopic(),
				instruction.getInstructionDate(), EdgeId);
		if ( instr.getInstructionDate() == null ) {
			instr.setInstructionDate(new DateTime());
		}
		instr.setState(InstructionState.Queued);
		if ( instruction.getParameters() != null ) {
			for ( InstructionParameter param : instruction.getParameters() ) {
				instr.addParameter(param.getName(), param.getValue());
			}
		}
		Long id = EdgeInstructionDao.store(instr);
		return EdgeInstructionDao.get(id);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<EdgeInstruction> queueInstructions(Set<Long> EdgeIds, Instruction instruction) {
		List<EdgeInstruction> results = new ArrayList<EdgeInstruction>(EdgeIds.size());
		for ( Long EdgeId : EdgeIds ) {
			EdgeInstruction copy = new EdgeInstruction(instruction.getTopic(),
					instruction.getInstructionDate(), EdgeId);
			copy.setParameters(instruction.getParameters());
			EdgeInstruction instr = queueInstruction(EdgeId, copy);
			results.add(instr);
		}
		return results;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void updateInstructionState(Long instructionId, InstructionState state) {
		EdgeInstruction instr = EdgeInstructionDao.get(instructionId);
		if ( instr != null ) {
			if ( !state.equals(instr.getState()) ) {
				instr.setState(state);
				EdgeInstructionDao.store(instr);
			}
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void updateInstructionsState(Set<Long> instructionIds, InstructionState state) {
		for ( Long id : instructionIds ) {
			updateInstructionState(id, state);
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void updateInstructionState(Long instructionId, InstructionState state,
			Map<String, ?> resultParameters) {
		EdgeInstruction instr = EdgeInstructionDao.get(instructionId);
		if ( instr != null ) {
			if ( !state.equals(instr.getState()) ) {
				instr.setState(state);
				if ( resultParameters != null ) {
					Map<String, Object> params = instr.getResultParameters();
					if ( params == null ) {
						params = new LinkedHashMap<String, Object>();
					}
					params.putAll(resultParameters);
					instr.setResultParameters(params);
				}
				EdgeInstructionDao.store(instr);
			}
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void updateInstructionsState(Set<Long> instructionIds, InstructionState state,
			Map<Long, Map<String, ?>> resultParameters) {
		for ( Long id : instructionIds ) {
			Map<String, ?> params = (resultParameters != null ? resultParameters.get(id) : null);
			updateInstructionState(id, state, params);
		}
	}

}
