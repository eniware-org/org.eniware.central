/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.mail;

import java.util.Locale;
import java.util.Map;
import org.springframework.core.io.Resource;

/**
 * API for data required to generate a template based mail message.
 * @version 1.1
 */
public interface MessageTemplateDataSource {

	/**
	 * A sensible standard column width to use for word-wrapping.
	 * 
	 * @since 1.1
	 */
	int STANDARD_WORD_WRAP_COLUMN = 80;

	/**
	 * Get a message template model to merge into the message.
	 * 
	 * @return Map of model objects
	 */
	Map<String, ?> getModel();

	/**
	 * Get a Locale for the message.
	 * 
	 * @return a Locale
	 */
	Locale getLocale();

	/**
	 * Get the message template.
	 * 
	 * @return message template
	 */
	Resource getMessageTemplate();

	/**
	 * Get the message subject.
	 * 
	 * @return message subject
	 */
	String getSubject();

	/**
	 * Get a character column index at which to hard-wrap message text at.
	 * Return <code>0</code> to indicate no wrapping should occur.
	 * 
	 * @return The word wrap character column index, or <em>null</em> if
	 *         unspecified.
	 * @since 1.1
	 */
	Integer getWordWrapCharacterIndex();

}
