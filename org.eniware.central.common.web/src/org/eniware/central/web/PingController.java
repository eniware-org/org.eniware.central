/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.web;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eniware.central.domain.PingTest;
import org.eniware.central.domain.PingTestResult;
import org.eniware.central.domain.PingTestResultDisplay;
import org.eniware.web.domain.Response;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * A web controller for running a set of {@link PingTest} tests and returning
 * the results.

 * @version 1.0
 */
@RequestMapping("/ping")
public class PingController {

	private List<PingTest> pingTests = null;

	private final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(1);
	private final ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0,
			TimeUnit.MILLISECONDS, queue);

	private PingResults executeTests() {
		final Date now = new Date();
		Map<String, PingTestResultDisplay> results = null;
		if ( pingTests != null ) {
			results = new LinkedHashMap<String, PingTestResultDisplay>(pingTests.size());
			for ( final PingTest t : pingTests ) {
				final Date start = new Date();
				PingTestResult pingTestResult = null;
				Future<PingTestResult> f = null;
				try {
					f = executorService.submit(new Callable<PingTestResult>() {

						@Override
						public PingTestResult call() throws Exception {
							return t.performPingTest();
						}
					});
					pingTestResult = f.get(t.getPingTestMaximumExecutionMilliseconds(),
							TimeUnit.MILLISECONDS);
				} catch ( TimeoutException e ) {
					if ( f != null ) {
						f.cancel(true);
					}
					pingTestResult = new PingTestResult(false, "Timeout: no result provided within "
							+ t.getPingTestMaximumExecutionMilliseconds() + "ms");
				} catch ( Throwable e ) {
					Throwable root = e;
					while ( root.getCause() != null ) {
						root = root.getCause();
					}
					pingTestResult = new PingTestResult(false, "Exception: " + root.toString());
				} finally {
					results.put(t.getPingTestId(), new PingTestResultDisplay(t, pingTestResult, start));
				}
			}
		}
		return new PingResults(now, results);
	}

	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Response<PingResults> executePingTest() {
		return Response.response(executeTests());
	}

	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public String executePingTest(Model model) {
		PingResults results = executeTests();
		model.addAttribute("results", results);
		return "ping";
	}

	/**
	 * An overall test results class.
	 */
	public static class PingResults {

		private final Date date;
		private final Map<String, PingTestResultDisplay> results;
		private final boolean allGood;

		/**
		 * Construct with values.
		 * 
		 * @param date
		 *        The date the tests were executed at.
		 * @param results
		 *        The test results (or <em>null</em> if none available).
		 */
		public PingResults(Date date, Map<String, PingTestResultDisplay> results) {
			super();
			this.date = date;
			boolean allOK = true;
			if ( results == null ) {
				this.results = Collections.emptyMap();
				allOK = false;
			} else {
				this.results = results;
				for ( PingTestResultDisplay r : results.values() ) {
					if ( !r.isSuccess() ) {
						allOK = false;
						break;
					}
				}
			}
			allGood = allOK;
		}

		/**
		 * Get a map of test ID to test results.
		 * 
		 * @return All test results.
		 */
		public Map<String, PingTestResultDisplay> getResults() {
			return results;
		}

		/**
		 * Get the date the tests were executed.
		 * 
		 * @return The date.
		 */
		public Date getDate() {
			return date;
		}

		/**
		 * Return <em>true</em> if there are test results available and all the
		 * results return <em>true</em> for {@link PingTestResult#isSuccess()}.
		 * 
		 * @return Boolean flag.
		 */
		public boolean isAllGood() {
			return allGood;
		}

	}

	public List<PingTest> getPingTests() {
		return pingTests;
	}

	public void setPingTests(List<PingTest> pingTests) {
		this.pingTests = pingTests;
	}

}
