/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 * $Id$
 * ==================================================================
 */

package org.eniware.central.domain;

import java.io.Serializable;

import org.joda.time.DateTime;

/**
 * Domain object for a group of Edges.
 * 
 * <p>Groups are assigned a {@link Location} which is meant to give the group
 * a broadly-defined location There is no actual restriction that Edges within
 * the group are physically within the group location's boundaries, but in 
 * practice this will often be the case.</p>
 * @version $Revision$
 */
public class EniwareEdgeGroup extends BaseEntity implements Cloneable, Serializable, EdgeGroupIdentity {

	private static final long serialVersionUID = 1843734913796373879L;

	private Long locationId = null;
	private String name;

	/**
	 * Default constructor.
	 */
	public EniwareEdgeGroup() {
		super();
	}
	
	/**
	 * Construct with values.
	 * 
	 * @param id the ID
	 * @param locationId the location ID
	 * @param name the name
	 */
	public EniwareEdgeGroup(Long id, Long locationId, String name) {
		super();
		setId(id);
		setCreated(new DateTime());
		setLocationId(locationId);
		setName(name);
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the locationId
	 */
	public Long getLocationId() {
		return locationId;
	}

	/**
	 * @param locationId the locationId to set
	 */
	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
	
}
