/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.mail;

/**
 * API for mail address information.
 * 
 * @author matt
 * @version $Id$
 */
public interface MailAddress {

	/**
	 * Get list of addresses to send the mail to.
	 * 
	 * @return array of email addresses
	 */
	String[] getTo();
	
	/**
	 * Get list of addresses to carbon-copy the mail to.
	 * 
	 * @return array of email addresses
	 */
	String[] getCc();
	
	/**
	 * Get list of addresses to blind-carbon-copy the mail to.
	 * 
	 * @return array of email addresses
	 */
	String[] getBcc();
	
	/**
	 * Get the address to send the mail from.
	 * 
	 * @return email address
	 */
	String getFrom();
	
}
