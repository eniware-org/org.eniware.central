/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.support;

import java.util.List;
import java.util.Set;

import org.eniware.central.datum.biz.DatumMetadataBiz;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilterMatch;
import org.eniware.central.datum.domain.GeneralNodeDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralNodeDatumMetadataFilterMatch;
import org.eniware.central.datum.domain.LocationSourcePK;
import org.eniware.central.datum.domain.NodeSourcePK;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.domain.GeneralDatumMetadata;

/**
 * Implementation of {@link DatumMetadataBiz} that delgates to another
 * {@link DatumMetadataBiz}. Designed for use with AOP.
 * 
 * @author matt
 * @version 1.2
 */
public class DelegatingDatumMetadataBiz implements DatumMetadataBiz {

	private final DatumMetadataBiz delegate;

	/**
	 * Construct with a delegate.
	 * 
	 * @param delegate
	 *        the delegate
	 */
	public DelegatingDatumMetadataBiz(DatumMetadataBiz delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void addGeneralNodeDatumMetadata(Long nodeId, String sourceId, GeneralDatumMetadata meta) {
		delegate.addGeneralNodeDatumMetadata(nodeId, sourceId, meta);
	}

	@Override
	public FilterResults<GeneralNodeDatumMetadataFilterMatch> findGeneralNodeDatumMetadata(
			GeneralNodeDatumMetadataFilter criteria, List<SortDescriptor> sortDescriptors,
			Integer offset, Integer max) {
		return delegate.findGeneralNodeDatumMetadata(criteria, sortDescriptors, offset, max);
	}

	@Override
	public void removeGeneralNodeDatumMetadata(Long nodeId, String sourceId) {
		delegate.removeGeneralNodeDatumMetadata(nodeId, sourceId);
	}

	@Override
	public void storeGeneralNodeDatumMetadata(Long nodeId, String sourceId, GeneralDatumMetadata meta) {
		delegate.storeGeneralNodeDatumMetadata(nodeId, sourceId, meta);
	}

	@Override
	public void addGeneralLocationDatumMetadata(Long locationId, String sourceId,
			GeneralDatumMetadata meta) {
		delegate.addGeneralLocationDatumMetadata(locationId, sourceId, meta);
	}

	@Override
	public void storeGeneralLocationDatumMetadata(Long locationId, String sourceId,
			GeneralDatumMetadata meta) {
		delegate.storeGeneralLocationDatumMetadata(locationId, sourceId, meta);
	}

	@Override
	public void removeGeneralLocationDatumMetadata(Long locationId, String sourceId) {
		delegate.removeGeneralLocationDatumMetadata(locationId, sourceId);
	}

	@Override
	public FilterResults<GeneralLocationDatumMetadataFilterMatch> findGeneralLocationDatumMetadata(
			GeneralLocationDatumMetadataFilter criteria, List<SortDescriptor> sortDescriptors,
			Integer offset, Integer max) {
		return delegate.findGeneralLocationDatumMetadata(criteria, sortDescriptors, offset, max);
	}

	@Override
	public Set<NodeSourcePK> getGeneralNodeDatumMetadataFilteredSources(Long[] nodeIds,
			String metadataFilter) {
		return delegate.getGeneralNodeDatumMetadataFilteredSources(nodeIds, metadataFilter);
	}

	@Override
	public Set<LocationSourcePK> getGeneralLocationDatumMetadataFilteredSources(Long[] locationIds,
			String metadataFilter) {
		return delegate.getGeneralLocationDatumMetadataFilteredSources(locationIds, metadataFilter);
	}

}
