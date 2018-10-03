/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.mail;

/**
 * FIXME
 * 
 * <p>TODO</p>
 * 
 * <p>The configurable properties of this class are:</p>
 * 
 * <dl class="class-properties">
 *   <dt></dt>
 *   <dd></dd>
 * </dl>
 * @version $Id$
 */
public interface MailService {

	/**
	 * Send a template-based mail message.
	 * 
	 * @param address where to send the mail to
	 * @param messageDataSource the message data source
	 */
	void sendMail(MailAddress address, MessageTemplateDataSource messageDataSource);
	
}
