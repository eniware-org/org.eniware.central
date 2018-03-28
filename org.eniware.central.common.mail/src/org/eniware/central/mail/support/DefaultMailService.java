/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.mail.support;

import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.lang3.text.WordUtils;
import org.eniware.central.mail.MailAddress;
import org.eniware.central.mail.MailService;
import org.eniware.central.mail.MessageTemplateDataSource;
import org.eniware.util.StringMerger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * Default implementation of {@link MailService} that uses Spring's mail classes
 * for sending mail.
 * 
 * <p>
 * The configurable properties of this class are:
 * </p>
 * 
 * <dl class="class-properties">
 * <dt>mailSender</dt>
 * <dd>The Spring {@link MailSender} to use for sending mail.</dd>
 * 
 * <dt>templateMessage</dt>
 * <dd>If configured, a template to use as a starting point for all messages.
 * This can be used to configure a default "from" address, for example.</dd>
 * 
 * </dl>
 * @version 1.2
 */
public class DefaultMailService implements MailService {

	private final MailSender mailSender;
	private SimpleMailMessage templateMessage;
	private int hardWrapColumnIndex = 0;

	private final Logger log = LoggerFactory.getLogger(DefaultMailService.class);

	/**
	 * Constructor.
	 * 
	 * @param mailSender
	 *        the {@link MailSender} to use
	 */
	public DefaultMailService(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	public void sendMail(MailAddress address, MessageTemplateDataSource messageDataSource) {
		SimpleMailMessage msg = new SimpleMailMessage();
		if ( templateMessage != null ) {
			templateMessage.copyTo(msg);
		}
		msg.setTo(address.getTo());
		if ( address.getFrom() != null ) {
			msg.setFrom(address.getFrom());
		}
		msg.setCc(address.getCc());
		msg.setBcc(address.getBcc());

		msg.setSubject(messageDataSource.getSubject());
		try {
			String msgText = StringMerger.mergeResource(messageDataSource.getMessageTemplate(),
					messageDataSource.getModel());
			int wrapColumn = 0;
			if ( messageDataSource.getWordWrapCharacterIndex() != null ) {
				wrapColumn = messageDataSource.getWordWrapCharacterIndex().intValue();
			} else {
				wrapColumn = hardWrapColumnIndex;
			}
			if ( wrapColumn > 0 ) {
				// WordUtils doesn't preserve paragraphs, so first split text into paragraph strings and wrap each of those
				StringBuilder buf = new StringBuilder();
				String[] paragraphs = msgText.split("\n{2,}");
				for ( String para : paragraphs ) {
					if ( buf.length() > 0 ) {
						buf.append("\n\n");
					}
					// we also replace all single \n within the paragraph with spaces, in case the message was already hard-wrapped
					buf.append(WordUtils.wrap(para.replace("\n", " "), wrapColumn));
				}
				msgText = buf.toString();
			}
			msg.setText(msgText);
		} catch ( IOException e ) {
			throw new RuntimeException("Unable to merge resource ["
					+ messageDataSource.getMessageTemplate().getFilename() + ']', e);
		}

		if ( log.isDebugEnabled() ) {
			log.debug("Sending mail [" + msg.getSubject() + "] to " + Arrays.toString(msg.getTo()));
		}

		// work around class loading issues re:
		// javax.mail.NoSuchProviderException: Unable to load class for provider: protocol=smtp; 
		//   class=org.apache.geronimo.javamail.transport.smtp.SMTPTransport
		ClassLoader oldCCL = Thread.currentThread().getContextClassLoader();
		if ( oldCCL != null ) {
			Thread.currentThread().setContextClassLoader(null);
		}
		try {
			mailSender.send(msg);
		} finally {
			if ( oldCCL != null ) {
				Thread.currentThread().setContextClassLoader(oldCCL);
			}
		}
	}

	public SimpleMailMessage getTemplateMessage() {
		return templateMessage;
	}

	public void setTemplateMessage(SimpleMailMessage templateMessage) {
		this.templateMessage = templateMessage;
	}

	/**
	 * Get the hard-wrap character column size setting.
	 * 
	 * @return The hard-wrap column.
	 * @since 1.1
	 */
	public int getHardWrapColumnIndex() {
		return hardWrapColumnIndex;
	}

	/**
	 * Set a character index to hard-wrap message text at. Hard-wrapping is
	 * disabled by setting this to zero.
	 * 
	 * @param hardWrapColumnIndex
	 *        The column index to hard-wrap message text at, or <code>0</code>
	 *        to disable hard wrapping.
	 * @since 1.1
	 */
	public void setHardWrapColumnIndex(int hardWrapColumnIndex) {
		this.hardWrapColumnIndex = hardWrapColumnIndex;
	}

}
