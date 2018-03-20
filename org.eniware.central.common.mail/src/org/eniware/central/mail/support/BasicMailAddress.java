/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.mail.support;

import org.eniware.central.mail.MailAddress;

/**
 * Basic implementation of {@link MailAddress}.
 * 
 * @author matt
 * @version $Id$
 */
public class BasicMailAddress implements MailAddress {
	
	private String[] to;
	private String[] cc;
	private String[] bcc;
	private String from;

	/**
	 * Construct with a single "to" address.
	 * 
	 * @param toName the address display name 
	 * @param toAddress the email address
	 */
	public BasicMailAddress(String toName, String toAddress) {
		this.to = new String[] { formatMailAddress(toName, toAddress) };
	}
	
	@Override
	public String[] getBcc() {
		return bcc == null ? null : bcc.clone();
	}

	@Override
	public String[] getCc() {
		return cc == null ? null : cc.clone();
	}

	@Override
	public String getFrom() {
		return from;
	}

	@Override
	public String[] getTo() {
		return to == null ? null : to.clone();
	}

	private String formatMailAddress(String name, String email) {
		if ( name == null || name.length() < 1 ) {
			return email;
		}
		return "\"" + name + "\" <" + email + ">";
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(String[] to) {
		this.to = to;
	}

	/**
	 * @param cc the cc to set
	 */
	public void setCc(String[] cc) {
		this.cc = cc;
	}

	/**
	 * @param bcc the bcc to set
	 */
	public void setBcc(String[] bcc) {
		this.bcc = bcc;
	}

	/**
	 * @param from the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}
	
}
