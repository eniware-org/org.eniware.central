/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */
package org.eniware.central.jobs.web.api;

import static org.eniware.web.domain.Response.response;

import java.util.Collection;
import java.util.Iterator;

import org.eniware.central.jobs.web.domain.JobFilter;
import org.eniware.central.scheduler.JobInfo;
import org.eniware.central.scheduler.SchedulerManager;
import org.eniware.central.scheduler.SchedulerStatus;
import org.eniware.central.web.support.WebServiceControllerSupport;
import org.eniware.web.domain.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for job scheduler management.
 * 
 * @author matt
 * @version 1.0
 */
@RestController("v1SchedulerController")
@RequestMapping(value = { "/api/v1/sec/scheduler" })
public class SchedulerAdminController extends WebServiceControllerSupport {

	private final SchedulerManager schedulerManager;

	/**
	 * Constructor.
	 * 
	 * @param schedulerManager
	 *        the manager to use
	 */
	@Autowired
	public SchedulerAdminController(SchedulerManager schedulerManager) {
		super();
		this.schedulerManager = schedulerManager;
	}

	/**
	 * Get the scheduler's current status.
	 * 
	 * @return the status
	 */
	@ResponseBody
	@RequestMapping(value = "/status", method = RequestMethod.GET)
	public Response<SchedulerStatus> currentStatus() {
		return response(schedulerManager.currentStatus());
	}

	/**
	 * Pause a specific job.
	 * 
	 * @param groupId
	 *        the group ID of the job to pause
	 * @param id
	 *        the ID of the job to pause
	 * @return the response
	 */
	@ResponseBody
	@RequestMapping(value = "/jobs/pause", method = RequestMethod.POST)
	public Response<Void> pauseJob(
			@RequestParam(value = "groupId", required = false) final String groupId,
			@RequestParam("id") final String id) {
		schedulerManager.pauseJob(groupId, id);
		return response(null);
	}

	/**
	 * Resume a specific paused job.
	 * 
	 * @param groupId
	 *        the group ID of the job to resume
	 * @param id
	 *        the ID of the job to resume
	 * @return the response
	 */
	@ResponseBody
	@RequestMapping(value = "/jobs/resume", method = RequestMethod.POST)
	public Response<Void> resumeJob(
			@RequestParam(value = "groupId", required = false) final String groupId,
			@RequestParam("id") final String id) {
		schedulerManager.resumeJob(groupId, id);
		return response(null);
	}

	/**
	 * Update the scheduler's status.
	 * 
	 * @param desiredStatus
	 *        the desired status of the scheduler
	 * @return the response
	 */
	@ResponseBody
	@RequestMapping(value = "/status", method = RequestMethod.POST)
	public Response<Void> updateStatus(@RequestParam("status") final SchedulerStatus desiredStatus) {
		schedulerManager.updateStatus(desiredStatus);
		return response(null);
	}

	/**
	 * Get the scheduler's configured jobs.
	 * 
	 * @param filter
	 *        an optional filter to restrict the results to; if not provided all
	 *        jobs are returned
	 * @return the list of jobs
	 */
	@ResponseBody
	@RequestMapping(value = "/jobs", method = RequestMethod.GET)
	public Response<Collection<JobInfo>> listJobs(final JobFilter filter) {
		Collection<JobInfo> infos = schedulerManager.allJobInfos();
		if ( filter != null ) {
			for ( Iterator<JobInfo> itr = infos.iterator(); itr.hasNext(); ) {
				JobInfo info = itr.next();
				if ( !filter.includesJobInfo(info) ) {
					itr.remove();
				}
			}
		}
		return response(infos);
	}

}
