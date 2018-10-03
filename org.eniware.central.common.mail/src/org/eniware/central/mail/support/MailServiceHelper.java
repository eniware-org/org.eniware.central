/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.mail.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eniware.central.mail.MailAddress;
import org.eniware.central.mail.MessageTemplateDataSource;

/**
 * Factory helper class for creating mail objects.
 * @version $Id$
 */
public class MailServiceHelper implements Serializable {
	
	private static final long serialVersionUID = 534833315331249860L;

	/**
	 * Create a new {@link MailAddress} from a display name and an
	 * email address.
	 * 
	 * @param toName the display name
	 * @param toAddress the email address
	 * @return new MailAddress
	 */
	public MailAddress createAddress(String toName, String toAddress) {
		return new BasicMailAddress(toName, toAddress);
	}
	
	/**
	 * Create a new {@link MessageTemplateDataSource} from necessary components.
	 * 
	 * @param subject the mail subject
	 * @param resourcePath the resource path
	 * @param locale the message locale
	 * @param params the message template parameters
	 * @return new MessageTemplateDataSource
	 */
	public MessageTemplateDataSource createResourceDataSource(
			String subject, String resourcePath, Locale locale, Object... params) {
		Map<String, Object> model = new HashMap<String, Object>();
		for ( Object o : params ) {
			Class<?> clazz = o.getClass();
			// prefer interfaces to class names if possible
			if ( !clazz.isInterface() && clazz.getInterfaces().length > 0 ) {
				clazz = clazz.getInterfaces()[0];
			}
			model.put(clazz.getSimpleName(), o);
		}
		return new ClasspathResourceMessageTemplateDataSource(locale, subject, 
				resourcePath, model);
	}
	
}
