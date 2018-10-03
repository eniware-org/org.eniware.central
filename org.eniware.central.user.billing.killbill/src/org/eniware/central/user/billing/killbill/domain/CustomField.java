/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill.domain;

import org.eniware.central.user.billing.domain.MetadataItem;

/**
 * A custom field.
 * 
 * @version 1.0
 */
public class CustomField implements MetadataItem {

	private String customFieldId;
	private String objectId;
	private String objectType;
	private String name;
	private String value;

	/**
	 * Default constructor.
	 */
	public CustomField() {
		super();
	}

	/**
	 * Construct with an ID.
	 * 
	 * @param customFieldId
	 *        the custom field ID
	 */
	public CustomField(String customFieldId) {
		super();
		this.customFieldId = customFieldId;
	}

	/**
	 * Construct with a name and value
	 * 
	 * @param name
	 *        the account ID
	 * @param timeZone
	 *        the account time zone
	 */
	public CustomField(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((customFieldId == null) ? 0 : customFieldId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( !(obj instanceof CustomField) ) {
			return false;
		}
		CustomField other = (CustomField) obj;
		if ( customFieldId == null ) {
			if ( other.customFieldId != null ) {
				return false;
			}
		} else if ( !customFieldId.equals(other.customFieldId) ) {
			return false;
		}
		if ( name == null ) {
			if ( other.name != null ) {
				return false;
			}
		} else if ( !name.equals(other.name) ) {
			return false;
		}
		if ( value == null ) {
			if ( other.value != null ) {
				return false;
			}
		} else if ( !value.equals(other.value) ) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "CustomField{name=" + name + ", value=" + value + "}";
	}

	/**
	 * Get the custom field ID.
	 * 
	 * @return the ID
	 */
	public String getCustomFieldId() {
		return customFieldId;
	}

	/**
	 * Set the custom field ID.
	 * 
	 * @param customFieldId
	 *        the ID to set
	 */
	public void setCustomFieldId(String customFieldId) {
		this.customFieldId = customFieldId;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Set the name.
	 * 
	 * @param name
	 *        the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getValue() {
		return value;
	}

	/**
	 * Set the value.
	 * 
	 * @param value
	 *        the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Get the object ID.
	 * 
	 * @return the object ID
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * Set the object ID.
	 * 
	 * @param objectId
	 *        the object ID to set
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	/**
	 * Get the object type.
	 * 
	 * @return the object type
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * Set the object type.
	 * 
	 * @param objectType
	 *        the object type to set
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

}
