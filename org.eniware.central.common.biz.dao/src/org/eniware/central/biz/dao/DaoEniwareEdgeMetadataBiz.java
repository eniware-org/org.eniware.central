/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.biz.dao;

import java.util.List;

import org.eniware.central.biz.EniwareEdgeMetadataBiz;
import org.eniware.central.dao.EniwareEdgeMetadataDao;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.EniwareEdgeMetadata;
import org.eniware.central.domain.EniwareEdgeMetadataFilter;
import org.eniware.central.domain.EniwareEdgeMetadataFilterMatch;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.domain.GeneralDatumMetadata;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO-based implementation of {@link EniwareEdgeMetadataBiz}.
 * @version 1.0
 */
public class DaoEniwareEdgeMetadataBiz implements EniwareEdgeMetadataBiz {

	private final EniwareEdgeMetadataDao eniwareEdgeMetadataDao;

	/**
	 * Constructor.
	 * 
	 * @param eniwareEdgeMetadataDao
	 *        the Edge metadata DAO to use
	 */
	public DaoEniwareEdgeMetadataBiz(EniwareEdgeMetadataDao eniwareEdgeMetadataDao) {
		super();
		this.eniwareEdgeMetadataDao = eniwareEdgeMetadataDao;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void addEniwareEdgeMetadata(Long EdgeId, GeneralDatumMetadata meta) {
		assert EdgeId != null;
		assert meta != null;
		EniwareEdgeMetadata snm = eniwareEdgeMetadataDao.get(EdgeId);
		GeneralDatumMetadata newMeta = meta;
		if ( snm == null ) {
			snm = new EniwareEdgeMetadata();
			snm.setCreated(new DateTime());
			snm.setId(EdgeId);
			newMeta = meta;
		} else if ( snm.getMeta() != null && snm.getMeta().equals(meta) == false ) {
			newMeta = new GeneralDatumMetadata(snm.getMeta());
			newMeta.merge(meta, true);
		}
		if ( newMeta != null && newMeta.equals(snm.getMeta()) == false ) {
			// have changes, so persist
			snm.setMeta(newMeta);
			eniwareEdgeMetadataDao.store(snm);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void storeEniwareEdgeMetadata(Long EdgeId, GeneralDatumMetadata meta) {
		assert EdgeId != null;
		assert meta != null;
		EniwareEdgeMetadata snm = eniwareEdgeMetadataDao.get(EdgeId);
		if ( snm == null ) {
			snm = new EniwareEdgeMetadata();
			snm.setCreated(new DateTime());
			snm.setId(EdgeId);
			snm.setMeta(meta);
		} else {
			snm.setMeta(meta);
		}
		eniwareEdgeMetadataDao.store(snm);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void removeEniwareEdgeMetadata(Long EdgeId) {
		EniwareEdgeMetadata meta = eniwareEdgeMetadataDao.get(EdgeId);
		if ( meta != null ) {
			eniwareEdgeMetadataDao.delete(meta);
		}
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public FilterResults<EniwareEdgeMetadataFilterMatch> findEniwareEdgeMetadata(
			EniwareEdgeMetadataFilter criteria, List<SortDescriptor> sortDescriptors, Integer offset,
			Integer max) {
		return eniwareEdgeMetadataDao.findFiltered(criteria, sortDescriptors, offset, max);
	}

}
