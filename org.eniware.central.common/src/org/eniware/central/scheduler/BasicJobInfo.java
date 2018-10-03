/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.scheduler;

import org.joda.time.DateTime;

/**
 * Basic implementation of {@link JobInfo}.
 * @version 1.0
 * @since 1.37
 */
public class BasicJobInfo implements JobInfo {

	private final String groupId;
	private final String id;
	private final String executionScheduleDescription;

	public BasicJobInfo(String groupId, String id, String executionScheduleDescription) {
		super();
		this.groupId = groupId;
		this.id = id;
		this.executionScheduleDescription = executionScheduleDescription;
	}

	@Override
	public final String getGroupId() {
		return groupId;
	}

	@Override
	public final String getId() {
		return id;
	}

	@Override
	public final String getExecutionScheduleDescription() {
		return executionScheduleDescription;
	}

	@Override
	public JobStatus getJobStatus() {
		return null;
	}

	@Override
	public boolean isExecuting() {
		return false;
	}

	@Override
	public DateTime getPreviousExecutionTime() {
		return null;
	}

	@Override
	public DateTime getNextExecutionTime() {
		return null;
	}

}
