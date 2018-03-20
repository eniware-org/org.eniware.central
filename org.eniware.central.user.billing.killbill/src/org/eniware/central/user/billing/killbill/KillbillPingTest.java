/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.eniware.central.domain.PingTest;
import org.eniware.central.domain.PingTestResult;
import org.eniware.central.support.CachedResult;
import org.eniware.central.user.billing.killbill.domain.HealthCheckResult;

/**
 * Health check for Kill Bill.
 * 
 * @author matt
 * @version 1.0
 */
public class KillbillPingTest implements PingTest {

	private final KillbillClient client;
	private int pingResultsCacheSeconds = 300;

	private CachedResult<PingTestResult> cachedResult;

	/**
	 * Constructor.
	 * 
	 * @param client
	 *        the Kill Bill client to use
	 */
	public KillbillPingTest(KillbillClient client) {
		super();
		this.client = client;
	}

	@Override
	public String getPingTestId() {
		return getClass().getName() + "-" + client.getUniqueId();
	}

	@Override
	public String getPingTestName() {
		return "Kill Bill Billing";
	}

	@Override
	public long getPingTestMaximumExecutionMilliseconds() {
		return 10000;
	}

	@Override
	public PingTestResult performPingTest() throws Exception {
		CachedResult<PingTestResult> cached = cachedResult;
		if ( cached != null && cached.isValid() ) {
			return cached.getResult();
		}

		Collection<HealthCheckResult> results = client.healthCheck();
		boolean healthy = true;
		String msg = "All health checks passed";
		if ( results.isEmpty() ) {
			healthy = false;
			msg = "Health check results not available";
		} else {
			HealthCheckResult firstFailed = results.stream().filter(c -> !c.isHealthy()).findAny()
					.orElse(null);
			if ( firstFailed != null ) {
				healthy = false;
				msg = "Check [" + firstFailed.getName() + "] not healthy";
				if ( firstFailed.getMessage() != null ) {
					msg += ": " + firstFailed.getMessage();
				}
			}
		}

		PingTestResult result = new PingTestResult(healthy, msg);
		cached = new CachedResult<PingTestResult>(result, pingResultsCacheSeconds, TimeUnit.SECONDS);
		cachedResult = cached;
		return result;
	}

	public int getPingResultsCacheSeconds() {
		return pingResultsCacheSeconds;
	}

	public void setPingResultsCacheSeconds(int pingResultsCacheSeconds) {
		this.pingResultsCacheSeconds = pingResultsCacheSeconds;
	}
}
