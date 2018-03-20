/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.daum.biz.dao;

import java.util.List;
import java.util.Set;

import org.eniware.central.datum.biz.DatumMetadataBiz;
import org.eniware.central.datum.dao.GeneralLocationDatumMetadataDao;
import org.eniware.central.datum.dao.GeneralNodeDatumMetadataDao;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadata;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilterMatch;
import org.eniware.central.datum.domain.GeneralNodeDatumMetadata;
import org.eniware.central.datum.domain.GeneralNodeDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralNodeDatumMetadataFilterMatch;
import org.eniware.central.datum.domain.LocationSourcePK;
import org.eniware.central.datum.domain.NodeSourcePK;
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
 * <dt>generalNodeDatumMetadataDao</dt>
 * <dd>The {@link GeneralNodeDatumMetadataDao} to use.</dd>
 * </dl>
 * 
 * @author matt
 * @version 1.1
 */
public class DaoDatumMetadataBiz implements DatumMetadataBiz {

	private GeneralLocationDatumMetadataDao generalLocationDatumMetadataDao = null;
	private GeneralNodeDatumMetadataDao generalNodeDatumMetadataDao = null;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void addGeneralNodeDatumMetadata(Long nodeId, String sourceId, GeneralDatumMetadata meta) {
		assert nodeId != null;
		assert sourceId != null;
		assert meta != null;
		NodeSourcePK pk = new NodeSourcePK(nodeId, sourceId);
		GeneralNodeDatumMetadata gdm = generalNodeDatumMetadataDao.get(pk);
		GeneralDatumMetadata newMeta = meta;
		if ( gdm == null ) {
			gdm = new GeneralNodeDatumMetadata();
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
			generalNodeDatumMetadataDao.store(gdm);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void storeGeneralNodeDatumMetadata(Long nodeId, String sourceId, GeneralDatumMetadata meta) {
		assert nodeId != null;
		assert sourceId != null;
		assert meta != null;
		NodeSourcePK pk = new NodeSourcePK(nodeId, sourceId);
		GeneralNodeDatumMetadata gdm = generalNodeDatumMetadataDao.get(pk);
		if ( gdm == null ) {
			gdm = new GeneralNodeDatumMetadata();
			gdm.setCreated(new DateTime());
			gdm.setId(pk);
			gdm.setMeta(meta);
		} else {
			gdm.setMeta(meta);
		}
		generalNodeDatumMetadataDao.store(gdm);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void removeGeneralNodeDatumMetadata(Long nodeId, String sourceId) {
		GeneralNodeDatumMetadata meta = generalNodeDatumMetadataDao
				.get(new NodeSourcePK(nodeId, sourceId));
		if ( meta != null ) {
			generalNodeDatumMetadataDao.delete(meta);
		}
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public FilterResults<GeneralNodeDatumMetadataFilterMatch> findGeneralNodeDatumMetadata(
			GeneralNodeDatumMetadataFilter criteria, List<SortDescriptor> sortDescriptors,
			Integer offset, Integer max) {
		return generalNodeDatumMetadataDao.findFiltered(criteria, sortDescriptors, offset, max);
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
	public Set<NodeSourcePK> getGeneralNodeDatumMetadataFilteredSources(Long[] nodeIds,
			String metadataFilter) {
		return generalNodeDatumMetadataDao.getFilteredSources(nodeIds, metadataFilter);
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public Set<LocationSourcePK> getGeneralLocationDatumMetadataFilteredSources(Long[] locationIds,
			String metadataFilter) {
		return generalLocationDatumMetadataDao.getFilteredSources(locationIds, metadataFilter);
	}

	public GeneralNodeDatumMetadataDao getGeneralNodeDatumMetadataDao() {
		return generalNodeDatumMetadataDao;
	}

	public void setGeneralNodeDatumMetadataDao(GeneralNodeDatumMetadataDao generalNodeDatumMetadataDao) {
		this.generalNodeDatumMetadataDao = generalNodeDatumMetadataDao;
	}

	public GeneralLocationDatumMetadataDao getGeneralLocationDatumMetadataDao() {
		return generalLocationDatumMetadataDao;
	}

	public void setGeneralLocationDatumMetadataDao(
			GeneralLocationDatumMetadataDao generalLocationDatumMetadataDao) {
		this.generalLocationDatumMetadataDao = generalLocationDatumMetadataDao;
	}

}
