/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eniware.central.domain.Aggregation;
import org.eniware.central.domain.Location;
import org.eniware.central.domain.SolarLocation;
import org.eniware.central.domain.SolarNodeMetadataFilter;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.support.MutableSortDescriptor;
import org.joda.time.DateTime;

/**
 * Implementation of {@link LocationDatumFilter}, {@link NodeDatumFilter}, and
 * {@link AggregateNodeDatumFilter}, and {@link GeneralNodeDatumFilter}.
 
 * @version 1.8
 */
public class DatumFilterCommand implements LocationDatumFilter, NodeDatumFilter,
		AggregateNodeDatumFilter, GeneralLocationDatumFilter, AggregateGeneralLocationDatumFilter,
		GeneralNodeDatumFilter, AggregateGeneralNodeDatumFilter, GeneralLocationDatumMetadataFilter,
		GeneralNodeDatumMetadataFilter, SolarNodeMetadataFilter {

	private final SolarLocation location;
	private DateTime startDate;
	private DateTime endDate;
	private boolean mostRecent = false;
	private String type; // e.g. Power, Consumption, etc.
	private List<MutableSortDescriptor> sorts;
	private Integer offset = 0;
	private Integer max;
	private String dataPath; // bean path expression to a data value, e.g. "i.watts"

	private Long[] locationIds;
	private Long[] nodeIds;
	private String[] sourceIds;
	private Long[] userIds;
	private String[] tags;
	private Aggregation aggregation;

	/**
	 * Default constructor.
	 */
	public DatumFilterCommand() {
		super();
		location = new SolarLocation();
	}

	/**
	 * Construct from a Location filter.
	 * 
	 * @param loc
	 *        the location
	 */
	public DatumFilterCommand(Location loc) {
		super();
		if ( loc instanceof SolarLocation ) {
			location = (SolarLocation) loc;
		} else {
			location = new SolarLocation(loc);
		}
	}

	@Override
	public Map<String, ?> getFilter() {
		Map<String, Object> filter = new LinkedHashMap<String, Object>();
		if ( location.getId() != null ) {
			filter.put("locationId", location.getId());
		}
		if ( startDate != null ) {
			filter.put("start", startDate);
		}
		if ( endDate != null ) {
			filter.put("end", endDate);
		}
		if ( location != null ) {
			filter.putAll(location.getFilter());
		}
		if ( nodeIds != null ) {
			filter.put("nodeIds", nodeIds);
		}
		if ( sourceIds != null ) {
			filter.put("sourceIds", sourceIds);
		}
		if ( startDate != null ) {
			filter.put("start", startDate);
		}
		if ( endDate != null ) {
			filter.put("end", endDate);
		}
		if ( aggregation != null ) {
			filter.put("aggregation", aggregation.toString());
		}
		return filter;
	}

	public boolean isHasLocationCriteria() {
		return (location != null && location.getFilter().size() > 0);
	}

	public void setLocationId(Long id) {
		location.setId(id);
	}

	@Override
	public Long getLocationId() {
		if ( location.getId() != null ) {
			return location.getId();
		}
		if ( locationIds != null && locationIds.length > 0 ) {
			return locationIds[0];
		}
		return null;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public DateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}

	@Override
	public DateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(DateTime endDate) {
		this.endDate = endDate;
	}

	@Override
	public String getType() {
		return type;
	}

	public void setType(String datumType) {
		this.type = datumType;
	}

	public List<MutableSortDescriptor> getSorts() {
		return sorts;
	}

	public void setSorts(List<MutableSortDescriptor> sorts) {
		this.sorts = sorts;
	}

	public List<SortDescriptor> getSortDescriptors() {
		if ( sorts == null ) {
			return Collections.emptyList();
		}
		return new ArrayList<SortDescriptor>(sorts);
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	/**
	 * Set a single node ID.
	 * 
	 * <p>
	 * This is a convenience method for requests that use a single node ID at a
	 * time. The node ID is still stored on the {@code nodeIds} array, just as
	 * the first value. Calling this method replaces any existing
	 * {@code nodeIds} value with a new array containing just the ID passed into
	 * this method.
	 * </p>
	 * 
	 * @param nodeId
	 *        the ID of the node
	 */
	public void setNodeId(Long nodeId) {
		this.nodeIds = new Long[] { nodeId };
	}

	/**
	 * Get the first node ID.
	 * 
	 * <p>
	 * This returns the first available node ID from the {@code nodeIds} array,
	 * or <em>null</em> if not available.
	 * </p>
	 * 
	 * @return the first node ID
	 */
	@Override
	public Long getNodeId() {
		return this.nodeIds == null || this.nodeIds.length < 1 ? null : this.nodeIds[0];
	}

	/**
	 * Set a single source ID.
	 * 
	 * <p>
	 * This is a convenience method for requests that use a single source ID at
	 * a time. The source ID is still stored on the {@code sourceIds} array,
	 * just as the first value. Calling this method replaces any existing
	 * {@code sourceIds} value with a new array containing just the ID passed
	 * into this method.
	 * </p>
	 * 
	 * @param nodeId
	 *        the ID of the node
	 */
	public void setSourceId(String sourceId) {
		if ( sourceId == null ) {
			this.sourceIds = null;
		} else {
			this.sourceIds = new String[] { sourceId };
		}
	}

	/**
	 * Get the first source ID.
	 * 
	 * <p>
	 * This returns the first available source ID from the {@code sourceIds}
	 * array, or <em>null</em> if not available.
	 * </p>
	 * 
	 * @return the first node ID
	 */
	@Override
	public String getSourceId() {
		return this.sourceIds == null || this.sourceIds.length < 1 ? null : this.sourceIds[0];
	}

	@Override
	public Long[] getNodeIds() {
		return nodeIds;
	}

	public void setNodeIds(Long[] nodeIds) {
		this.nodeIds = nodeIds;
	}

	@Override
	public String[] getSourceIds() {
		return sourceIds;
	}

	public void setSourceIds(String[] sourceIds) {
		this.sourceIds = sourceIds;
	}

	@Override
	public String getTag() {
		return this.tags == null || this.tags.length < 1 ? null : this.tags[0];
	}

	@Override
	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	@Override
	public Aggregation getAggregation() {
		return aggregation;
	}

	public void setAggregation(Aggregation aggregation) {
		this.aggregation = aggregation;
	}

	/**
	 * Calls {@link #setAggregation(Aggregation)} for backwards API
	 * compatibility.
	 * 
	 * @param aggregate
	 *        the aggregation to set
	 */
	public void setAggregate(Aggregation aggregate) {
		setAggregation(aggregate);
	}

	@Override
	public boolean isMostRecent() {
		return mostRecent;
	}

	public void setMostRecent(boolean mostRecent) {
		this.mostRecent = mostRecent;
	}

	@Override
	public String getDataPath() {
		return dataPath;
	}

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}

	@Override
	public String[] getDataPathElements() {
		String path = this.dataPath;
		if ( path == null ) {
			return null;
		}
		return path.split("\\.");
	}

	@Override
	public Long[] getLocationIds() {
		if ( locationIds != null ) {
			return locationIds;
		}
		if ( location != null && location.getId() != null ) {
			return new Long[] { location.getId() };
		}
		return null;
	}

	public void setLocationIds(Long[] locationIds) {
		this.locationIds = locationIds;
	}

	/**
	 * Set a single user ID.
	 * 
	 * <p>
	 * This is a convenience method for requests that use a single user ID at a
	 * time. The user ID is still stored on the {@code userIds} array, just as
	 * the first value. Calling this method replaces any existing
	 * {@code userIds} value with a new array containing just the ID passed into
	 * this method.
	 * </p>
	 * 
	 * @param userId
	 *        the ID of the user
	 */
	public void setUserId(Long userId) {
		this.userIds = new Long[] { userId };
	}

	/**
	 * Get the first user ID.
	 * 
	 * <p>
	 * This returns the first available user ID from the {@code userIds} array,
	 * or <em>null</em> if not available.
	 * </p>
	 * 
	 * @return the first user ID
	 */
	@Override
	public Long getUserId() {
		return this.userIds == null || this.userIds.length < 1 ? null : this.userIds[0];
	}

	@Override
	public Long[] getUserIds() {
		return userIds;
	}

	public void setUserIds(Long[] userIds) {
		this.userIds = userIds;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.8
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aggregation == null) ? 0 : aggregation.hashCode());
		result = prime * result + ((dataPath == null) ? 0 : dataPath.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + Arrays.hashCode(locationIds);
		result = prime * result + ((max == null) ? 0 : max.hashCode());
		result = prime * result + (mostRecent ? 1231 : 1237);
		result = prime * result + Arrays.hashCode(nodeIds);
		result = prime * result + ((offset == null) ? 0 : offset.hashCode());
		result = prime * result + ((sorts == null) ? 0 : sorts.hashCode());
		result = prime * result + Arrays.hashCode(sourceIds);
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + Arrays.hashCode(tags);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + Arrays.hashCode(userIds);
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.8
	 */
	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( !(obj instanceof DatumFilterCommand) ) {
			return false;
		}
		DatumFilterCommand other = (DatumFilterCommand) obj;
		if ( aggregation != other.aggregation ) {
			return false;
		}
		if ( dataPath == null ) {
			if ( other.dataPath != null ) {
				return false;
			}
		} else if ( !dataPath.equals(other.dataPath) ) {
			return false;
		}
		if ( endDate == null ) {
			if ( other.endDate != null ) {
				return false;
			}
		} else if ( !endDate.isEqual(other.endDate) ) {
			return false;
		}
		if ( location == null ) {
			if ( other.location != null ) {
				return false;
			}
		} else if ( !location.equals(other.location) ) {
			return false;
		}
		if ( !Arrays.equals(locationIds, other.locationIds) ) {
			return false;
		}
		if ( max == null ) {
			if ( other.max != null ) {
				return false;
			}
		} else if ( !max.equals(other.max) ) {
			return false;
		}
		if ( mostRecent != other.mostRecent ) {
			return false;
		}
		if ( !Arrays.equals(nodeIds, other.nodeIds) ) {
			return false;
		}
		if ( offset == null ) {
			if ( other.offset != null ) {
				return false;
			}
		} else if ( !offset.equals(other.offset) ) {
			return false;
		}
		if ( sorts == null ) {
			if ( other.sorts != null ) {
				return false;
			}
		} else if ( !sorts.equals(other.sorts) ) {
			return false;
		}
		if ( !Arrays.equals(sourceIds, other.sourceIds) ) {
			return false;
		}
		if ( startDate == null ) {
			if ( other.startDate != null ) {
				return false;
			}
		} else if ( !startDate.isEqual(other.startDate) ) {
			return false;
		}
		if ( !Arrays.equals(tags, other.tags) ) {
			return false;
		}
		if ( type == null ) {
			if ( other.type != null ) {
				return false;
			}
		} else if ( !type.equals(other.type) ) {
			return false;
		}
		if ( !Arrays.equals(userIds, other.userIds) ) {
			return false;
		}
		return true;
	}

}
