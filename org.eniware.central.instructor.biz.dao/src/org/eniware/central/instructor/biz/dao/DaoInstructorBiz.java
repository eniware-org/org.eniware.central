/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
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
	private EdgeInstructionDao nodeInstructionDao;

	private List<Instruction> asResultList(FilterResults<EntityMatch> matches) {
		List<Instruction> results = new ArrayList<Instruction>(matches.getReturnedResultCount());
		for ( EntityMatch match : matches.getResults() ) {
			if ( match instanceof Instruction ) {
				results.add((Instruction) match);
			} else {
				results.add(nodeInstructionDao.get(match.getId()));
			}
		}
		return results;
	}

	private List<EdgeInstruction> asNodeInstructionList(FilterResults<EntityMatch> matches) {
		List<EdgeInstruction> results = new ArrayList<EdgeInstruction>(matches.getReturnedResultCount());
		for ( EntityMatch match : matches.getResults() ) {
			if ( match instanceof EdgeInstruction ) {
				results.add((EdgeInstruction) match);
			} else {
				results.add(nodeInstructionDao.get(match.getId()));
			}
		}
		return results;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public EdgeInstruction getInstruction(Long instructionId) {
		return nodeInstructionDao.get(instructionId);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<EdgeInstruction> getInstructions(Set<Long> instructionIds) {
		Long[] ids = instructionIds.toArray(new Long[instructionIds.size()]);
		SimpleInstructionFilter filter = new SimpleInstructionFilter();
		filter.setInstructionIds(ids);
		FilterResults<EntityMatch> matches = nodeInstructionDao.findFiltered(filter, null, null, null);
		return asNodeInstructionList(matches);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<Instruction> getActiveInstructionsForNode(Long nodeId) {
		SimpleInstructionFilter filter = new SimpleInstructionFilter();
		filter.setNodeId(nodeId);
		filter.setState(InstructionState.Queued);
		FilterResults<EntityMatch> matches = nodeInstructionDao.findFiltered(filter, null, null, null);
		return asResultList(matches);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<EdgeInstruction> getActiveInstructionsForNodes(Set<Long> nodeIds) {
		Long[] ids = nodeIds.toArray(new Long[nodeIds.size()]);
		SimpleInstructionFilter filter = new SimpleInstructionFilter();
		filter.setNodeIds(ids);
		filter.setState(InstructionState.Queued);
		FilterResults<EntityMatch> matches = nodeInstructionDao.findFiltered(filter, null, null, null);
		return asNodeInstructionList(matches);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<Instruction> getPendingInstructionsForNode(Long nodeId) {
		SimpleInstructionFilter filter = new SimpleInstructionFilter();
		filter.setNodeId(nodeId);
		filter.setStateSet(EnumSet.of(InstructionState.Queued, InstructionState.Received,
				InstructionState.Executing));
		FilterResults<EntityMatch> matches = nodeInstructionDao.findFiltered(filter, null, null, null);
		return asResultList(matches);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<EdgeInstruction> getPendingInstructionsForNodes(Set<Long> nodeIds) {
		Long[] ids = nodeIds.toArray(new Long[nodeIds.size()]);
		SimpleInstructionFilter filter = new SimpleInstructionFilter();
		filter.setNodeIds(ids);
		filter.setStateSet(EnumSet.of(InstructionState.Queued, InstructionState.Received,
				InstructionState.Executing));
		FilterResults<EntityMatch> matches = nodeInstructionDao.findFiltered(filter, null, null, null);
		return asNodeInstructionList(matches);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public EdgeInstruction queueInstruction(Long nodeId, Instruction instruction) {
		EdgeInstruction instr = new EdgeInstruction(instruction.getTopic(),
				instruction.getInstructionDate(), nodeId);
		if ( instr.getInstructionDate() == null ) {
			instr.setInstructionDate(new DateTime());
		}
		instr.setState(InstructionState.Queued);
		if ( instruction.getParameters() != null ) {
			for ( InstructionParameter param : instruction.getParameters() ) {
				instr.addParameter(param.getName(), param.getValue());
			}
		}
		Long id = nodeInstructionDao.store(instr);
		return nodeInstructionDao.get(id);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<EdgeInstruction> queueInstructions(Set<Long> nodeIds, Instruction instruction) {
		List<EdgeInstruction> results = new ArrayList<EdgeInstruction>(nodeIds.size());
		for ( Long nodeId : nodeIds ) {
			EdgeInstruction copy = new EdgeInstruction(instruction.getTopic(),
					instruction.getInstructionDate(), nodeId);
			copy.setParameters(instruction.getParameters());
			EdgeInstruction instr = queueInstruction(nodeId, copy);
			results.add(instr);
		}
		return results;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void updateInstructionState(Long instructionId, InstructionState state) {
		EdgeInstruction instr = nodeInstructionDao.get(instructionId);
		if ( instr != null ) {
			if ( !state.equals(instr.getState()) ) {
				instr.setState(state);
				nodeInstructionDao.store(instr);
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
		EdgeInstruction instr = nodeInstructionDao.get(instructionId);
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
				nodeInstructionDao.store(instr);
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
