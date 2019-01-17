/* ==================================================================
 * Eniware Open Source Nikolai Manchev
 * ==================================================================
 */

package org.eniware.central.mail.mock;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * Mock implementation of Spring's {@link MailSender}.
 * 
 * <p>
 * This implementation will log sending of messages only.
 * </p>
 * 
 * @author 
 * @version 1.2
 */
public class MockMailSender implements MailSender {

	private final Logger log = LoggerFactory.getLogger(MockMailSender.class);

	private final Queue<SimpleMailMessage> sent = new ConcurrentLinkedQueue<SimpleMailMessage>();

	@Override
	public void send(SimpleMailMessage msg) throws MailException {
		if ( msg == null ) {
			return;
		}

		log.info("MOCK: sending mail from {} to {} with text:\n{}\n", msg.getFrom(), msg.getTo(),
				msg.getText());
		sent.add(msg);
	}

	@Override
	public void send(SimpleMailMessage... msgs) throws MailException {
		if ( msgs == null ) {
			return;
		}
		for ( SimpleMailMessage msg : msgs ) {
			send(msg);
		}
	}

	public Logger getLog() {
		return log;
	}

	/**
	 * Get a list of all sent messages. This list can be cleared during unit
	 * tests to keep track of the messages sent during the test.
	 * 
	 * @return List of messages, never <em>null</em>.
	 * @since 1.1
	 */
	public Queue<SimpleMailMessage> getSent() {
		return sent;
	}

}
