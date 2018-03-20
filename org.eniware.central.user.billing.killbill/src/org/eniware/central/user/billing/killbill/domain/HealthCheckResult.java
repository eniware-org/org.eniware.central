/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill.domain;

/**
 * An individual health check result.
 * 
 * @author matt
 * @version 1.0
 */
public class HealthCheckResult {

	private final String name;
	private final boolean healthy;
	private final String message;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *        the check name
	 * @param healthy
	 *        {@literal true} if the check passed
	 */
	public HealthCheckResult(String name, boolean healthy) {
		this(name, healthy, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 *        the check name
	 * @param healthy
	 *        {@literal true} if the check passed
	 * @param message
	 *        an optional message
	 */
	public HealthCheckResult(String name, boolean healthy, String message) {
		super();
		this.name = name;
		this.healthy = healthy;
		this.message = message;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (healthy ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Compares the {@code name} and {@code healthy} flags of another
	 * {@link HealthCheckResult} for equality with this instance.
	 * </p>
	 */
	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( !(obj instanceof HealthCheckResult) ) {
			return false;
		}
		HealthCheckResult other = (HealthCheckResult) obj;
		if ( healthy != other.healthy ) {
			return false;
		}
		if ( name == null ) {
			if ( other.name != null ) {
				return false;
			}
		} else if ( !name.equals(other.name) ) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "HealthCheckResult{name=" + name + ", healthy=" + healthy + ", message=" + message + "}";
	}

	/**
	 * Get the name of the health check.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the health check status.
	 * 
	 * @return {@literal true} if the check passed and is considered "healthy"
	 */
	public boolean isHealthy() {
		return healthy;
	}

	/**
	 * Get an optional message.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

}
