/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.support;

import java.util.List;

import org.eniware.central.biz.SolarNodeMetadataBiz;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SolarNodeMetadataFilter;
import org.eniware.central.domain.SolarNodeMetadataFilterMatch;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.domain.GeneralDatumMetadata;

/**
 * Implementation of {@link SolarNodeMetadataBiz} that delegates to another
 * {@link SolarNodeMetadataBiz}. Designed for use with AOP.
 * @version 1.0
 * @since 1.32
 */
public class DelegatingSolarNodeMetadataBiz implements SolarNodeMetadataBiz {

	private final SolarNodeMetadataBiz delegate;

	/**
	 * Construct with a delegate.
	 * 
	 * @param delegate
	 *        the delegate
	 */
	public DelegatingSolarNodeMetadataBiz(SolarNodeMetadataBiz delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void addSolarNodeMetadata(Long nodeId, GeneralDatumMetadata meta) {
		delegate.addSolarNodeMetadata(nodeId, meta);
	}

	@Override
	public void storeSolarNodeMetadata(Long nodeId, GeneralDatumMetadata meta) {
		delegate.storeSolarNodeMetadata(nodeId, meta);
	}

	@Override
	public void removeSolarNodeMetadata(Long nodeId) {
		delegate.removeSolarNodeMetadata(nodeId);
	}

	@Override
	public FilterResults<SolarNodeMetadataFilterMatch> findSolarNodeMetadata(
			SolarNodeMetadataFilter criteria, List<SortDescriptor> sortDescriptors, Integer offset,
			Integer max) {
		return delegate.findSolarNodeMetadata(criteria, sortDescriptors, offset, max);
	}

}
