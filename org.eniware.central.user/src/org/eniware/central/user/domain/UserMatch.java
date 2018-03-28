/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

/**
 * A "match" to a {@link User} entity.
 * 
 * Although this class extends {@link User} that is merely an implementation
 * detail.
 * 
 * @version 1.0
 * @since 1.25
 */
public class UserMatch extends User implements UserFilterMatch {

	private static final long serialVersionUID = -3527259710501547791L;

	/**
	 * Default constructor.
	 */
	public UserMatch() {
		super();
	}

	/**
	 * Construct with values.
	 * 
	 * @param userId
	 *        the uesr ID
	 * @param email
	 *        the email
	 */
	public UserMatch(Long userId, String email) {
		super(userId, email);
	}

}
