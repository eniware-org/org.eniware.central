/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.instructor.domain;

import java.io.Serializable;

/**
 * Helper class for instruction parameters.
 * 
 * @version 1.1
 */
public class InstructionParameter implements Serializable {

	private static final long serialVersionUID = 2828143065346415324L;

	private String name;
	private String value;

	/**
	 * Default constructor.
	 */
	public InstructionParameter() {
		super();
	}

	/**
	 * Construct with values.
	 * 
	 * @param name
	 *        the name
	 * @param value
	 *        the value
	 */
	public InstructionParameter(String name, String value) {
		super();
		setName(name);
		setValue(value);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * Compare two {@link InstructionParameter} objects for equality. Only the
	 * {@code name} property is used for comparison.
	 */
	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( getClass() != obj.getClass() ) {
			return false;
		}
		InstructionParameter other = (InstructionParameter) obj;
		if ( name == null ) {
			if ( other.name != null ) {
				return false;
			}
		} else if ( !name.equals(other.name) ) {
			return false;
		}
		return true;
	}

}
