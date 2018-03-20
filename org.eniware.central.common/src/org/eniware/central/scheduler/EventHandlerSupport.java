/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.scheduler;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation of {@link EventHandler}.
 * 
 * <p>This class traps all Exceptions and logs an error message. This
 * helps prevent the event handler from becoming black-listed by the
 * EventAdmin service, as the Apache Felix implementation does.</p>
 * 
 * @author matt
 * @version $Revision$
 */
public abstract class EventHandlerSupport implements EventHandler {

	/** A class-level logger. */
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	public final void handleEvent(Event event) {
		try {
			handleEventInternal(event);
		} catch ( Exception e ) {
			log.error("Exception in OSGi Event handler", e);
		}
	}
	
	/**
	 * Execute the event handler.
	 * 
	 * @param event the event
	 * @throws Exception if any error occurs
	 */
	protected abstract void handleEventInternal(Event event)
	throws Exception;
	
}
