/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.instructor.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eniware.central.domain.BaseEntity;
import org.eniware.central.support.JsonUtils;
import org.joda.time.DateTime;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Domain object for an individual instruction.
 *
 * @version 1.1
 */
public class Instruction extends BaseEntity {

	private static final long serialVersionUID = 3505998875736791309L;

	private String topic;
	private DateTime instructionDate;
	private InstructionState state = InstructionState.Unknown;
	private List<InstructionParameter> parameters;
	private Map<String, Object> resultParameters;

	private String resultParametersJson;

	/**
	 * Default constructor.
	 */
	public Instruction() {
		super();
	}

	/**
	 * Construct with data.
	 * 
	 * @param topic
	 *        the topic
	 * @param instructionDate
	 *        the instruction date
	 */
	public Instruction(String topic, DateTime instructionDate) {
		super();
		this.topic = topic;
		this.instructionDate = instructionDate;
	}

	/**
	 * Remove all parameters.
	 */
	public void clearParameters() {
		parameters.clear();
	}

	/**
	 * Add a parameter value.
	 * 
	 * @param key
	 *        the key
	 * @param value
	 *        the value
	 */
	public void addParameter(String key, String value) {
		if ( parameters == null ) {
			parameters = new ArrayList<InstructionParameter>(5);
		}
		parameters.add(new InstructionParameter(key, value));
	}

	/**
	 * Set a result parameter value.
	 * 
	 * @param key
	 *        the key
	 * @param value
	 *        the value
	 */
	public void putResultParameter(String key, Object value) {
		Map<String, Object> map = resultParameters;
		if ( map == null ) {
			map = new LinkedHashMap<String, Object>();
			resultParameters = map;
		}
		map.put(key, value);
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public DateTime getInstructionDate() {
		return instructionDate;
	}

	public void setInstructionDate(DateTime instructionDate) {
		this.instructionDate = instructionDate;
	}

	public InstructionState getState() {
		return state;
	}

	public void setState(InstructionState state) {
		this.state = state;
	}

	public List<InstructionParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<InstructionParameter> parameters) {
		this.parameters = parameters;
	}

	@JsonIgnore
	@SuppressWarnings("unchecked")
	public Map<String, Object> getResultParameters() {
		Map<String, Object> map = this.resultParameters;
		if ( map != null ) {
			return map;
		}
		String json = resultParametersJson;
		if ( json != null ) {
			map = JsonUtils.getObjectFromJSON(json, Map.class);
			this.resultParameters = map;
		}
		return map;
	}

	@JsonIgnore
	public void setResultParameters(Map<String, Object> resultParameters) {
		this.resultParameters = resultParameters;
		resultParametersJson = null;
	}

	/**
	 * Get the result parameters object as a JSON string.
	 * 
	 * @return a JSON encoded string, never <em>null</em>
	 */
	@JsonGetter("resultParameters")
	@JsonRawValue
	public String getResultParametersJson() {
		if ( resultParametersJson != null ) {
			return resultParametersJson;
		}
		Map<String, Object> map = getResultParameters();
		if ( map == null ) {
			return null;
		}
		String json = JsonUtils.getJSONString(map, null);
		resultParametersJson = json;
		return json;
	}

	/**
	 * Set the result parameters object via a JSON string.
	 * 
	 * <p>
	 * This method will remove any previously created result parameters and
	 * replace it with the values parsed from the JSON.
	 * </p>
	 * 
	 * @param json
	 */
	@JsonSetter("resultParameters")
	@JsonRawValue
	public void setResultParametersJson(String json) {
		resultParametersJson = json;
		resultParameters = null;
	}

}
