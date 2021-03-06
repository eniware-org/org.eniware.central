/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import org.eniware.util.SerializeIgnore;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Extension of {@link GeneralEdgeDatum} with some additional properties geared
 * towards reporting.
 *
 * @version 1.0
 */
public class ReportingGeneralEdgeDatum extends GeneralEdgeDatum implements
		ReportingGeneralEdgeDatumMatch {

	private static final long serialVersionUID = -6844746894977879054L;

	private LocalDateTime localDateTime;

	@Override
	public LocalDate getLocalDate() {
		if ( localDateTime == null ) {
			return null;
		}
		return localDateTime.toLocalDate();
	}

	@Override
	public LocalTime getLocalTime() {
		if ( localDateTime == null ) {
			return null;
		}
		return localDateTime.toLocalTime();
	}

	@JsonIgnore
	@SerializeIgnore
	public LocalDateTime getLocalDateTime() {
		return localDateTime;
	}

	public void setLocalDateTime(LocalDateTime localDateTime) {
		this.localDateTime = localDateTime;
	}

}
