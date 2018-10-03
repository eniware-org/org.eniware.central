/* ==================================================================
 * Eniware Open Source:Nikolai Manchev
 * Apache License 2.0
 * ==================================================================
 */

package org.eniware.central;

import java.io.Serializable;

/**
 * An exception when some task that can be repeated safely has failed to signal
 * to the caller to retry the task.
 * @version 1.1
 */
public class RepeatableTaskException extends RuntimeException {

	private static final long serialVersionUID = 948738004538481858L;

	private final Serializable id;

	/**
	 * Default constructor.
	 */
	public RepeatableTaskException() {
		super();
		this.id = null;
	}

	/**
	 * Construct with an ID value.
	 * 
	 * @param id
	 *        An ID associated with this repeatable task.
	 * @since 1.1
	 */
	public RepeatableTaskException(Serializable id) {
		super();
		this.id = id;
	}

	public RepeatableTaskException(String msg, Throwable t) {
		super(msg, t);
		this.id = null;
	}

	/**
	 * Construct with values.
	 * 
	 * @param msg
	 *        A message.
	 * @param t
	 *        A nested exception.
	 * @param id
	 *        An ID associated with this repeatable task.
	 * @since 1.1
	 */
	public RepeatableTaskException(String msg, Throwable t, Serializable id) {
		super(msg, t);
		this.id = id;
	}

	public RepeatableTaskException(String msg) {
		super(msg);
		this.id = null;
	}

	public RepeatableTaskException(Throwable t) {
		super(t);
		this.id = null;
	}

	/**
	 * Get the ID associated with the repeatable task.
	 * 
	 * @return An ID, or <em>null</em> if none available.
	 */
	public Serializable getId() {
		return id;
	}

}
