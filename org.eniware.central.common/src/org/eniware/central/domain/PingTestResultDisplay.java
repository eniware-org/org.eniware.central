/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import java.util.Date;

/**
 * Extension of {@link PingTestResult} to support the UI layer.
 * 
 * @author matt
 * @version 1.0
 */
public class PingTestResultDisplay extends PingTestResult {

	private final String pingTestId;
	private final String pingTestName;
	private final Date start;
	private final Date end;

	/**
	 * Construct from a test and a result.
	 * 
	 * @param test
	 *        The test.
	 * @param result
	 *        The result.
	 * @param start
	 *        The time the test started.
	 */
	public PingTestResultDisplay(PingTest test, PingTestResult result, Date start) {
		super(result.isSuccess(), result.getMessage(), result.getProperties());
		this.pingTestId = test.getPingTestId();
		this.pingTestName = test.getPingTestName();
		this.start = start;
		this.end = new Date();
	}

	public String getPingTestId() {
		return pingTestId;
	}

	public String getPingTestName() {
		return pingTestName;
	}

	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

}
