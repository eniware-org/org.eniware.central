/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;
import org.eniware.central.domain.Aggregation;
import org.eniware.util.Cachable;
import org.joda.time.DateTime;

/**
 * Command object for specifying datum query criteria.
 
 * @version 1.2
 * @deprecated use {@link DatumFilterCommand}
 */
@Deprecated
public class DatumQueryCommand implements Cachable, AggregateEdgeDatumFilter {

	private Long[] EdgeIds;
	private String[] sourceIds;
	private Long[] locationIds;
	private DateTime startDate;
	private DateTime endDate;
	private Aggregation aggregate;
	private boolean mostRecent = false;
	private Integer precision; // specify aggregate precision, e.g. *5* minutes
	private Integer resultOffset; // result starting offset
	private Integer resultMax; // result page size
	private String datumType; // e.g. Power, Consumption, etc.
	private Map<String, Object> properties = new LinkedHashMap<String, Object>();

	@Override
	public String getCacheKey() {
		String data = Arrays.toString(EdgeIds) + Arrays.toString(sourceIds)
				+ Arrays.toString(locationIds) + startDate + endDate + aggregate + mostRecent + precision
				+ resultOffset + resultMax + datumType + properties;
		return DigestUtils.sha1Hex(data);
	}

	@Override
	public Long getTti() {
		return null;
	}

	@Override
	public Long getTtl() {
		return null;
	}

	@Override
	public Map<String, ?> getFilter() {
		Map<String, Object> filter = new LinkedHashMap<String, Object>();
		if ( EdgeIds != null ) {
			filter.put("EdgeIds", EdgeIds);
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
		if ( aggregate != null ) {
			filter.put("aggregate", aggregate.toString());
		}
		return filter;
	}

	/**
	 * Test if a specific Aggregation is set.
	 * 
	 * <p>
	 * This method will return <em>false</em> if no {@code aggregate} value has
	 * been set.
	 * </p>
	 * 
	 * @param agg
	 *        the aggregation to test
	 * @return boolean
	 */
	public boolean isAggregateAtMost(Aggregation agg) {
		return this.aggregate != null && this.aggregate.compareLevel(agg) <= 0;
	}

	/**
	 * Set a single Edge ID.
	 * 
	 * <p>
	 * This is a convenience method for requests that use a single Edge ID at a
	 * time. The Edge ID is still stored on the {@code EdgeIds} array, just as
	 * the first value. Calling this method replaces any existing
	 * {@code EdgeIds} value with a new array containing just the ID passed into
	 * this method.
	 * </p>
	 * 
	 * @param EdgeId
	 *        the ID of the Edge
	 */
	public void setEdgeId(Long EdgeId) {
		this.EdgeIds = new Long[] { EdgeId };
	}

	/**
	 * Get the first Edge ID.
	 * 
	 * <p>
	 * This returns the first available Edge ID from the {@code EdgeIds} array,
	 * or <em>null</em> if not available.
	 * </p>
	 * 
	 * @return the first Edge ID
	 */
	@Override
	public Long getEdgeId() {
		return this.EdgeIds == null || this.EdgeIds.length < 1 ? null : this.EdgeIds[0];
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
	 * @param EdgeId
	 *        the ID of the Edge
	 */
	public void setSourceId(String sourceId) {
		this.sourceIds = new String[] { sourceId };
	}

	/**
	 * Get the first location ID.
	 * 
	 * <p>
	 * This returns the first available source ID from the {@code locationIds}
	 * array, or <em>null</em> if not available.
	 * </p>
	 * 
	 * @return the first Edge ID
	 */
	public Long getLocationId() {
		return this.locationIds == null || this.locationIds.length < 1 ? null : this.locationIds[0];
	}

	/**
	 * Set a single location ID.
	 * 
	 * <p>
	 * This is a convenience method for requests that use a single location ID
	 * at a time. The location ID is still stored on the {@code locationIds}
	 * array, just as the first value. Calling this method replaces any existing
	 * {@code locationIds} value with a new array containing just the ID passed
	 * into this method.
	 * </p>
	 * 
	 * @param EdgeId
	 *        the ID of the Edge
	 */
	public void setLocationId(Long locationId) {
		this.locationIds = new Long[] { locationId };
	}

	/**
	 * Get the first source ID.
	 * 
	 * <p>
	 * This returns the first available source ID from the {@code sourceIds}
	 * array, or <em>null</em> if not available.
	 * </p>
	 * 
	 * @return the first Edge ID
	 */
	@Override
	public String getSourceId() {
		return this.sourceIds == null || this.sourceIds.length < 1 ? null : this.sourceIds[0];
	}

	/**
	 * Set a property value.
	 * 
	 * <p>
	 * This will replace any existing property value for the same key.
	 * </p>
	 * 
	 * @param key
	 *        the key
	 * @param value
	 *        the value
	 */
	public void setProperty(String key, Object value) {
		if ( this.properties == null ) {
			this.properties = new LinkedHashMap<String, Object>();
		}
		this.properties.put(key, value);
	}

	/**
	 * Set the datum type.
	 * 
	 * <p>
	 * This is an alias for {@link #setDatumType(String)}.
	 * </p>
	 * 
	 * @see #setDatumType(String)
	 * @param datumType
	 */
	public void setType(String datumType) {
		setDatumType(datumType);
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	@Override
	public Long[] getEdgeIds() {
		return EdgeIds;
	}

	@Override
	public String[] getSourceIds() {
		return sourceIds;
	}

	public void setSourceIds(String[] sourceIds) {
		this.sourceIds = sourceIds;
	}

	public void setEdgeIds(Long[] EdgeIds) {
		this.EdgeIds = EdgeIds;
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

	public Integer getPrecision() {
		return precision;
	}

	public void setPrecision(Integer precision) {
		this.precision = precision;
	}

	@Override
	public Aggregation getAggregation() {
		return aggregate;
	}

	public Aggregation getAggregate() {
		return aggregate;
	}

	public void setAggregate(Aggregation aggregate) {
		this.aggregate = aggregate;
	}

	public Long[] getLocationIds() {
		return locationIds;
	}

	public void setLocationIds(Long[] locationIds) {
		this.locationIds = locationIds;
	}

	public boolean isMostRecent() {
		return mostRecent;
	}

	public void setMostRecent(boolean mostRecent) {
		this.mostRecent = mostRecent;
	}

	public Integer getResultOffset() {
		return resultOffset;
	}

	public void setResultOffset(Integer resultOffset) {
		this.resultOffset = resultOffset;
	}

	public Integer getResultMax() {
		return resultMax;
	}

	public void setResultMax(Integer resultMax) {
		this.resultMax = resultMax;
	}

	public String getDatumType() {
		return datumType;
	}

	public void setDatumType(String datumType) {
		this.datumType = datumType;
	}

	@Override
	public String getType() {
		return getDatumType();
	}

}
