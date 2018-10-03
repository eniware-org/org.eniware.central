/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.support;

import java.util.List;
import java.util.Set;

import org.eniware.central.datum.biz.DatumMetadataBiz;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilterMatch;
import org.eniware.central.datum.domain.GeneralEdgeDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralEdgeDatumMetadataFilterMatch;
import org.eniware.central.datum.domain.LocationSourcePK;
import org.eniware.central.datum.domain.EdgeSourcePK;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.domain.GeneralDatumMetadata;

/**
 * Implementation of {@link DatumMetadataBiz} that delgates to another
 * {@link DatumMetadataBiz}. Designed for use with AOP.
 *
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
	public void addGeneralEdgeDatumMetadata(Long EdgeId, String sourceId, GeneralDatumMetadata meta) {
		delegate.addGeneralEdgeDatumMetadata(EdgeId, sourceId, meta);
	}

	@Override
	public FilterResults<GeneralEdgeDatumMetadataFilterMatch> findGeneralEdgeDatumMetadata(
			GeneralEdgeDatumMetadataFilter criteria, List<SortDescriptor> sortDescriptors,
			Integer offset, Integer max) {
		return delegate.findGeneralEdgeDatumMetadata(criteria, sortDescriptors, offset, max);
	}

	@Override
	public void removeGeneralEdgeDatumMetadata(Long EdgeId, String sourceId) {
		delegate.removeGeneralEdgeDatumMetadata(EdgeId, sourceId);
	}

	@Override
	public void storeGeneralEdgeDatumMetadata(Long EdgeId, String sourceId, GeneralDatumMetadata meta) {
		delegate.storeGeneralEdgeDatumMetadata(EdgeId, sourceId, meta);
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
	public Set<EdgeSourcePK> getGeneralEdgeDatumMetadataFilteredSources(Long[] EdgeIds,
			String metadataFilter) {
		return delegate.getGeneralEdgeDatumMetadataFilteredSources(EdgeIds, metadataFilter);
	}

	@Override
	public Set<LocationSourcePK> getGeneralLocationDatumMetadataFilteredSources(Long[] locationIds,
			String metadataFilter) {
		return delegate.getGeneralLocationDatumMetadataFilteredSources(locationIds, metadataFilter);
	}

}
