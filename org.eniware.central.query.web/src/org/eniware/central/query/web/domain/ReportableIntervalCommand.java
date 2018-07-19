/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.query.web.domain;

import org.eniware.central.query.domain.ReportableIntervalType;
import org.joda.time.LocalDate;

/**
 * ReportableIntervalCommand object.
 */
public final class ReportableIntervalCommand {

	private Long EdgeId;
	private ReportableIntervalType[] types;
	private LocalDate start;
	private LocalDate end;

	public ReportableIntervalType[] getTypes() {
		return types;
	}

	public void setTypes(ReportableIntervalType[] types) {
		this.types = types;
	}

	public void setType(ReportableIntervalType type) {
		this.types = new ReportableIntervalType[] { type };
	}

	public Long getEdgeId() {
		return EdgeId;
	}

	public void setEdgeId(Long EdgeId) {
		this.EdgeId = EdgeId;
	}

	public LocalDate getStart() {
		return start;
	}

	public void setStart(LocalDate start) {
		this.start = start;
	}

	public LocalDate getEnd() {
		return end;
	}

	public void setEnd(LocalDate end) {
		this.end = end;
	}

}
