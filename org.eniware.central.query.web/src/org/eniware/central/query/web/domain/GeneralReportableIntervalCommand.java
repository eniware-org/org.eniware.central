/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.query.web.domain;

import org.joda.time.DateTime;

/**
 * Command for general reportable interval queries.
 * 
 * @version 1.3
 */
public class GeneralReportableIntervalCommand {

	private Long locationId;
	private Long[] EdgeIds;
	private String sourceId;
	private DateTime startDate;
	private DateTime endDate;
	private String metadataFilter;

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
	public Long getEdgeId() {
		return this.EdgeIds == null || this.EdgeIds.length < 1 ? null : this.EdgeIds[0];
	}

	/**
	 * Get the Edge IDs.
	 * 
	 * @return The Edge IDs.
	 * @since 1.2
	 */
	public Long[] getEdgeIds() {
		return EdgeIds;
	}

	/**
	 * Set the Edge IDs.
	 * 
	 * @param EdgeIds
	 *        The Edge IDs to set.
	 * @since 1.2
	 */
	public void setEdgeIds(Long[] EdgeIds) {
		this.EdgeIds = EdgeIds;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * Set the start date.
	 * 
	 * @param start
	 *        the start date
	 * @deprecated use {@link #setStartDate(DateTime)}
	 */
	@Deprecated
	public void setStart(DateTime start) {
		setStartDate(start);
	}

	/**
	 * Set the end date.
	 * 
	 * @param end
	 *        the end date
	 * @deprecated use {@link #setEndDate(DateTime)}
	 */
	@Deprecated
	public void setEnd(DateTime end) {
		setEndDate(end);
	}

	/**
	 * Get the start date.
	 * 
	 * @return the start date
	 * @since 1.3
	 */
	public DateTime getStartDate() {
		return startDate;
	}

	/**
	 * Set the start date.
	 * 
	 * @param startDate
	 *        the start date to set
	 * @since 1.3
	 */
	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}

	/**
	 * Get the end date.
	 * 
	 * @return the end date
	 * @since 1.3
	 */
	public DateTime getEndDate() {
		return endDate;
	}

	/**
	 * Set the end date.
	 * 
	 * @param endDate
	 *        the end date to set
	 * @since 1.3
	 */
	public void setEndDate(DateTime endDate) {
		this.endDate = endDate;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	/**
	 * Get the LDAP style metadata filter.
	 * 
	 * @return The configured filter.
	 * @since 1.2
	 */
	public String getMetadataFilter() {
		return metadataFilter;
	}

	/**
	 * Set the LDAP style metadata filter.
	 * 
	 * @param metadataFilter
	 *        The filter to set.
	 * @since 1.2
	 */
	public void setMetadataFilter(String metadataFilter) {
		this.metadataFilter = metadataFilter;
	}

}
