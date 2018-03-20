/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.biz.dao;

import java.util.List;

import org.eniware.central.biz.SolarNodeMetadataBiz;
import org.eniware.central.dao.SolarNodeMetadataDao;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SolarNodeMetadata;
import org.eniware.central.domain.SolarNodeMetadataFilter;
import org.eniware.central.domain.SolarNodeMetadataFilterMatch;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.domain.GeneralDatumMetadata;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO-based implementation of {@link SolarNodeMetadataBiz}.
 * 
 * @author matt
 * @version 1.0
 */
public class DaoSolarNodeMetadataBiz implements SolarNodeMetadataBiz {

	private final SolarNodeMetadataDao solarNodeMetadataDao;

	/**
	 * Constructor.
	 * 
	 * @param solarNodeMetadataDao
	 *        the node metadata DAO to use
	 */
	public DaoSolarNodeMetadataBiz(SolarNodeMetadataDao solarNodeMetadataDao) {
		super();
		this.solarNodeMetadataDao = solarNodeMetadataDao;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void addSolarNodeMetadata(Long nodeId, GeneralDatumMetadata meta) {
		assert nodeId != null;
		assert meta != null;
		SolarNodeMetadata snm = solarNodeMetadataDao.get(nodeId);
		GeneralDatumMetadata newMeta = meta;
		if ( snm == null ) {
			snm = new SolarNodeMetadata();
			snm.setCreated(new DateTime());
			snm.setId(nodeId);
			newMeta = meta;
		} else if ( snm.getMeta() != null && snm.getMeta().equals(meta) == false ) {
			newMeta = new GeneralDatumMetadata(snm.getMeta());
			newMeta.merge(meta, true);
		}
		if ( newMeta != null && newMeta.equals(snm.getMeta()) == false ) {
			// have changes, so persist
			snm.setMeta(newMeta);
			solarNodeMetadataDao.store(snm);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void storeSolarNodeMetadata(Long nodeId, GeneralDatumMetadata meta) {
		assert nodeId != null;
		assert meta != null;
		SolarNodeMetadata snm = solarNodeMetadataDao.get(nodeId);
		if ( snm == null ) {
			snm = new SolarNodeMetadata();
			snm.setCreated(new DateTime());
			snm.setId(nodeId);
			snm.setMeta(meta);
		} else {
			snm.setMeta(meta);
		}
		solarNodeMetadataDao.store(snm);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void removeSolarNodeMetadata(Long nodeId) {
		SolarNodeMetadata meta = solarNodeMetadataDao.get(nodeId);
		if ( meta != null ) {
			solarNodeMetadataDao.delete(meta);
		}
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public FilterResults<SolarNodeMetadataFilterMatch> findSolarNodeMetadata(
			SolarNodeMetadataFilter criteria, List<SortDescriptor> sortDescriptors, Integer offset,
			Integer max) {
		return solarNodeMetadataDao.findFiltered(criteria, sortDescriptors, offset, max);
	}

}
