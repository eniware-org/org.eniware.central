/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

/**
 * Helper object when mapping legacy {link Datum} objects into
 * {@link GeneralEdgeDatum} or {@link GeneralLocationDatum} objects.
 
 * @version 1.0
 */
public class DatumMappingInfo {

	private Long id;
	private String sourceId;
	private String timeZoneId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

}
