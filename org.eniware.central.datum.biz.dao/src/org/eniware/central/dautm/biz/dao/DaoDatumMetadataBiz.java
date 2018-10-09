/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dautm.biz.dao;

import java.util.List;
import java.util.Set;

import org.eniware.central.datum.biz.DatumMetadataBiz;
import org.eniware.central.datum.dao.GeneralLocationDatumMetadataDao;
import org.eniware.central.datum.dao.GeneralEdgeDatumMetadataDao;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadata;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilterMatch;
import org.eniware.central.datum.domain.GeneralEdgeDatumMetadata;
import org.eniware.central.datum.domain.GeneralEdgeDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralEdgeDatumMetadataFilterMatch;
import org.eniware.central.datum.domain.LocationSourcePK;
import org.eniware.central.datum.domain.EdgeSourcePK;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.domain.GeneralDatumMetadata;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO-based implementation of {@link DatumMetadataBiz}.
 * 
 * <p>
 * The configurable properties of this class are:
 * </p>
 * 
 * <dl class="class-properties">
 * <dt>generalLocationDatumMetadataDao</dt>
 * <dd>The {@link GeneralLocationDatumMetadataDao} to use.</dd>
 * 
 * <dt>generalEdgeDatumMetadataDao</dt>
 * <dd>The {@link GeneralEdgeDatumMetadataDao} to use.</dd>
 * </dl>
 *
 * @version 1.1
 */
public class DaoDatumMetadataBiz implements DatumMetadataBiz {

	private GeneralLocationDatumMetadataDao generalLocationDatumMetadataDao = null;
	private GeneralEdgeDatumMetadataDao generalEdgeDatumMetadataDao = null;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void addGeneralEdgeDatumMetadata(Long EdgeId, String sourceId, GeneralDatumMetadata meta) {
		assert EdgeId != null;
		assert sourceId != null;
		assert meta != null;
		EdgeSourcePK pk = new EdgeSourcePK(EdgeId, sourceId);
		GeneralEdgeDatumMetadata gdm = generalEdgeDatumMetadataDao.get(pk);
		GeneralDatumMetadata newMeta = meta;
		if ( gdm == null ) {
			gdm = new GeneralEdgeDatumMetadata();
			gdm.setCreated(new DateTime());
			gdm.setId(pk);
			newMeta = meta;
		} else if ( gdm.getMeta() != null && gdm.getMeta().equals(meta) == false ) {
			newMeta = new GeneralDatumMetadata(gdm.getMeta());
			newMeta.merge(meta, true);
		}
		if ( newMeta != null && newMeta.equals(gdm.getMeta()) == false ) {
			// have changes, so persist
			gdm.setMeta(newMeta);
			generalEdgeDatumMetadataDao.store(gdm);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void storeGeneralEdgeDatumMetadata(Long EdgeId, String sourceId, GeneralDatumMetadata meta) {
		assert EdgeId != null;
		assert sourceId != null;
		assert meta != null;
		EdgeSourcePK pk = new EdgeSourcePK(EdgeId, sourceId);
		GeneralEdgeDatumMetadata gdm = generalEdgeDatumMetadataDao.get(pk);
		if ( gdm == null ) {
			gdm = new GeneralEdgeDatumMetadata();
			gdm.setCreated(new DateTime());
			gdm.setId(pk);
			gdm.setMeta(meta);
		} else {
			gdm.setMeta(meta);
		}
		generalEdgeDatumMetadataDao.store(gdm);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void removeGeneralEdgeDatumMetadata(Long EdgeId, String sourceId) {
		GeneralEdgeDatumMetadata meta = generalEdgeDatumMetadataDao
				.get(new EdgeSourcePK(EdgeId, sourceId));
		if ( meta != null ) {
			generalEdgeDatumMetadataDao.delete(meta);
		}
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public FilterResults<GeneralEdgeDatumMetadataFilterMatch> findGeneralEdgeDatumMetadata(
			GeneralEdgeDatumMetadataFilter criteria, List<SortDescriptor> sortDescriptors,
			Integer offset, Integer max) {
		return generalEdgeDatumMetadataDao.findFiltered(criteria, sortDescriptors, offset, max);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void addGeneralLocationDatumMetadata(Long locationId, String sourceId,
			GeneralDatumMetadata meta) {
		assert locationId != null;
		assert sourceId != null;
		assert meta != null;
		LocationSourcePK pk = new LocationSourcePK(locationId, sourceId);
		GeneralLocationDatumMetadata gdm = generalLocationDatumMetadataDao.get(pk);
		GeneralDatumMetadata newMeta = meta;
		if ( gdm == null ) {
			gdm = new GeneralLocationDatumMetadata();
			gdm.setCreated(new DateTime());
			gdm.setId(pk);
			newMeta = meta;
		} else if ( gdm.getMeta() != null && gdm.getMeta().equals(meta) == false ) {
			newMeta = new GeneralDatumMetadata(gdm.getMeta());
			newMeta.merge(meta, true);
		}
		if ( newMeta != null && newMeta.equals(gdm.getMeta()) == false ) {
			// have changes, so persist
			gdm.setMeta(newMeta);
			generalLocationDatumMetadataDao.store(gdm);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void storeGeneralLocationDatumMetadata(Long locationId, String sourceId,
			GeneralDatumMetadata meta) {
		assert locationId != null;
		assert sourceId != null;
		assert meta != null;
		LocationSourcePK pk = new LocationSourcePK(locationId, sourceId);
		GeneralLocationDatumMetadata gdm = generalLocationDatumMetadataDao.get(pk);
		if ( gdm == null ) {
			gdm = new GeneralLocationDatumMetadata();
			gdm.setCreated(new DateTime());
			gdm.setId(pk);
			gdm.setMeta(meta);
		} else {
			gdm.setMeta(meta);
		}
		generalLocationDatumMetadataDao.store(gdm);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void removeGeneralLocationDatumMetadata(Long locationId, String sourceId) {
		GeneralLocationDatumMetadata meta = generalLocationDatumMetadataDao
				.get(new LocationSourcePK(locationId, sourceId));
		if ( meta != null ) {
			generalLocationDatumMetadataDao.delete(meta);
		}
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public FilterResults<GeneralLocationDatumMetadataFilterMatch> findGeneralLocationDatumMetadata(
			GeneralLocationDatumMetadataFilter criteria, List<SortDescriptor> sortDescriptors,
			Integer offset, Integer max) {
		return generalLocationDatumMetadataDao.findFiltered(criteria, sortDescriptors, offset, max);
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public Set<EdgeSourcePK> getGeneralEdgeDatumMetadataFilteredSources(Long[] EdgeIds,
			String metadataFilter) {
		return generalEdgeDatumMetadataDao.getFilteredSources(EdgeIds, metadataFilter);
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public Set<LocationSourcePK> getGeneralLocationDatumMetadataFilteredSources(Long[] locationIds,
			String metadataFilter) {
		return generalLocationDatumMetadataDao.getFilteredSources(locationIds, metadataFilter);
	}

	public GeneralEdgeDatumMetadataDao getGeneralEdgeDatumMetadataDao() {
		return generalEdgeDatumMetadataDao;
	}

	public void setGeneralEdgeDatumMetadataDao(GeneralEdgeDatumMetadataDao generalEdgeDatumMetadataDao) {
		this.generalEdgeDatumMetadataDao = generalEdgeDatumMetadataDao;
	}

	public GeneralLocationDatumMetadataDao getGeneralLocationDatumMetadataDao() {
		return generalLocationDatumMetadataDao;
	}

	public void setGeneralLocationDatumMetadataDao(
			GeneralLocationDatumMetadataDao generalLocationDatumMetadataDao) {
		this.generalLocationDatumMetadataDao = generalLocationDatumMetadataDao;
	}

}
