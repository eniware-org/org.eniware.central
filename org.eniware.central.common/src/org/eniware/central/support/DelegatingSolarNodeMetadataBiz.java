/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.support;

import java.util.List;

import org.eniware.central.biz.EniwareEdgeMetadataBiz;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.EniwareEdgeMetadataFilter;
import org.eniware.central.domain.EniwareEdgeMetadataFilterMatch;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.domain.GeneralDatumMetadata;

/**
 * Implementation of {@link EniwareEdgeMetadataBiz} that delegates to another
 * {@link EniwareEdgeMetadataBiz}. Designed for use with AOP.
 * @version 1.0
 * @since 1.32
 */
public class DelegatingEniwareEdgeMetadataBiz implements EniwareEdgeMetadataBiz {

	private final EniwareEdgeMetadataBiz delegate;

	/**
	 * Construct with a delegate.
	 * 
	 * @param delegate
	 *        the delegate
	 */
	public DelegatingEniwareEdgeMetadataBiz(EniwareEdgeMetadataBiz delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void addEniwareEdgeMetadata(Long nodeId, GeneralDatumMetadata meta) {
		delegate.addEniwareEdgeMetadata(nodeId, meta);
	}

	@Override
	public void storeEniwareEdgeMetadata(Long nodeId, GeneralDatumMetadata meta) {
		delegate.storeEniwareEdgeMetadata(nodeId, meta);
	}

	@Override
	public void removeEniwareEdgeMetadata(Long nodeId) {
		delegate.removeEniwareEdgeMetadata(nodeId);
	}

	@Override
	public FilterResults<EniwareEdgeMetadataFilterMatch> findEniwareEdgeMetadata(
			EniwareEdgeMetadataFilter criteria, List<SortDescriptor> sortDescriptors, Integer offset,
			Integer max) {
		return delegate.findEniwareEdgeMetadata(criteria, sortDescriptors, offset, max);
	}

}
