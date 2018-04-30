/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import org.omg.CORBA.Any;

/**
 * API for a service that be used to verify the status of some specific part of
 * the EniwareNetwork system.
 * @version 1.0
 */
public interface PingTest {

	/**
	 * Get some globally-unique ID for this test instance.
	 * 
	 * @return The globally-unique ID of this test instance.
	 */
	String getPingTestId();

	/**
	 * Get display name for this test.
	 * 
	 * @return The name of the test.
	 */
	String getPingTestName();

	/**
	 * Get the maximum number of milliseconds to wait for the ping test to
	 * execute before considering the test a failure.
	 * 
	 * @return The maximum execution milliseconds.
	 */
	long getPingTestMaximumExecutionMilliseconds();

	/**
	 * Perform the test, and return the results of the test.
	 * 
	 * @throws Any
	 *         exception.
	 * @return The test results.
	 */
	PingTestResult performPingTest() throws Exception;

}
