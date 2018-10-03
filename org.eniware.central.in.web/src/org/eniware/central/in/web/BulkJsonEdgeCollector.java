/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.in.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import javax.servlet.http.HttpServletResponse;

import org.eniware.central.RepeatableTaskException;
import org.eniware.central.dao.EniwareEdgeDao;
import org.eniware.central.instructor.domain.Instruction;
import org.eniware.central.instructor.domain.InstructionState;
import org.eniware.central.support.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eniware.central.datum.domain.Datum;
import org.eniware.central.datum.domain.GeneralLocationDatum;
import org.eniware.central.datum.domain.GeneralEdgeDatum;
import org.eniware.central.datum.domain.HardwareControlDatum;
import org.eniware.central.in.biz.DataCollectorBiz;
import org.eniware.central.instructor.biz.InstructorBiz;
import org.eniware.central.security.AuthenticatedEdge;
import org.eniware.domain.EdgeControlPropertyType;
import org.eniware.web.domain.Response;

/**
 * JSON implementation of bulk upload service.
 * 
 * @version 1.4
 */
@Controller
@RequestMapping(value = { "/bulkCollector.do", "/u/bulkCollector.do" }, consumes = "application/json")
public class BulkJsonEdgeCollector extends AbstractDataCollector {

	/** The JSON field name for an "object type". */
	public static final String OBJECT_TYPE_FIELD = "__type__";

	/** The InstructionStatus type. */
	public static final String INSTRUCTION_STATUS_TYPE = "InstructionStatus";

	/** The EdgeControlInfo type. */
	public static final String Edge_CONTROL_INFO_TYPE = "EdgeControlInfo";

	/** The {@link GeneralEdgeDatum} or {@link GeneralLocationDatum} type. */
	public static final String GENERAL_Edge_DATUM_TYPE = "datum";

	/**
	 * The JSON field name for a location ID on a {@link GeneralLocationDatum}
	 * value.
	 */
	public static final String LOCATION_ID_FIELD = "locationId";

	private final ObjectMapper objectMapper;

	/**
	 * Constructor.
	 * 
	 * @param dataCollectorBiz
	 *        the {@link DataCollectorBiz} to use
	 * @param eniwareEdgeDao
	 *        the {@link EniwareEdgeDao} to use
	 * @param objectMapper
	 *        the {@link ObjectMapper} to use
	 */
	@Autowired
	public BulkJsonEdgeCollector(DataCollectorBiz dataCollectorBiz, EniwareEdgeDao eniwareEdgeDao,
			ObjectMapper objectMapper) {
		setDataCollectorBiz(dataCollectorBiz);
		setEniwareEdgeDao(eniwareEdgeDao);
		this.objectMapper = objectMapper;
	}

	/**
	 * Handle a {@link RuntimeException}.
	 * 
	 * @param e
	 *        the exception
	 * @param response
	 *        the response
	 * @return an error response object
	 */
	@ExceptionHandler(RuntimeException.class)
	@ResponseBody
	public Response<?> handleRuntimeException(RuntimeException e, HttpServletResponse response) {
		log.error("RuntimeException in {} controller", getClass().getSimpleName(), e);
		return new Response<Object>(Boolean.FALSE, null, "Internal error", null);
	}

	/**
	 * Post new data.
	 * 
	 * <p>
	 * If {@code encoding} contains {@code gzip} the InputStream itself is
	 * assumed to be compressed with GZip and encoded as Base64. Otherwise the
	 * InputStream is assumed to be regular text (not compressed).
	 * </p>
	 * 
	 * @param encoding
	 *        an optional encoding value
	 * @param in
	 *        the request input stream
	 * @return the result model
	 * @throws IOException
	 *         if any IO error occurs
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST)
	public Response<BulkUploadResult> postData(
			@RequestHeader(value = "Content-Encoding", required = false) String encoding, InputStream in,
			Model model) throws IOException {
		AuthenticatedEdge authEdge = getAuthenticatedEdge(true);

		InputStream input = in;
		if ( encoding != null && encoding.toLowerCase().contains("gzip") ) {
			input = new GZIPInputStream(in);
		}

		List<Datum> parsedDatum = new ArrayList<Datum>();
		List<GeneralEdgeDatum> parsedGeneralEdgeDatum = new ArrayList<GeneralEdgeDatum>();
		List<GeneralLocationDatum> parsedGeneralLocationDatum = new ArrayList<GeneralLocationDatum>();
		List<Object> resultDatum = new ArrayList<Object>();

		try {
			JsonNode tree = objectMapper.readTree(input);
			if ( tree.isArray() ) {
				for ( JsonNode child : tree ) {
					Object o = handleEdge(child);
					if ( o instanceof GeneralEdgeDatum ) {
						parsedGeneralEdgeDatum.add((GeneralEdgeDatum) o);
					} else if ( o instanceof GeneralLocationDatum ) {
						parsedGeneralLocationDatum.add((GeneralLocationDatum) o);
					} else if ( o instanceof Datum ) {
						parsedDatum.add((Datum) o);
					} else if ( o instanceof Instruction ) {
						resultDatum.add(o);
					}
				}
			}
		} finally {
			if ( input != null ) {
				input.close();
			}
		}

		try {
			if ( parsedDatum.size() > 0 ) {
				@SuppressWarnings("deprecation")
				List<Datum> postedDatum = getDataCollectorBiz().postDatum(parsedDatum);
				resultDatum.addAll(postedDatum);
			}
			if ( parsedGeneralEdgeDatum.size() > 0 ) {
				getDataCollectorBiz().postGeneralEdgeDatum(parsedGeneralEdgeDatum);
				for ( GeneralEdgeDatum d : parsedGeneralEdgeDatum ) {
					resultDatum.add(d.getId());
				}
			}
			if ( parsedGeneralLocationDatum.size() > 0 ) {
				getDataCollectorBiz().postGeneralLocationDatum(parsedGeneralLocationDatum);
				for ( GeneralLocationDatum d : parsedGeneralLocationDatum ) {
					resultDatum.add(d.getId());
				}
			}
		} catch ( RepeatableTaskException e ) {
			if ( log.isDebugEnabled() ) {
				Throwable root = e;
				while ( root.getCause() != null ) {
					root = root.getCause();
				}
				log.debug("RepeatableTaskException caused by: " + root.getMessage());
			}
		}

		BulkUploadResult result = new BulkUploadResult();
		if ( resultDatum.size() > 0 ) {
			result.setDatum(resultDatum);
		}

		// add instructions for the Edge
		InstructorBiz instructorBiz = (getInstructorBiz() == null ? null : getInstructorBiz().service());
		if ( instructorBiz != null ) {
			List<Instruction> instructions = instructorBiz
					.getActiveInstructionsForEdge(authEdge.getEdgeId());
			if ( instructions != null && instructions.size() > 0 ) {
				result.setInstructions(instructions);
			}
		}

		return new Response<BulkUploadResult>(result);
	}

	private Object handleEdge(JsonNode Edge) {
		String EdgeType = getStringFieldValue(Edge, OBJECT_TYPE_FIELD, GENERAL_Edge_DATUM_TYPE);
		if ( GENERAL_Edge_DATUM_TYPE.equalsIgnoreCase(EdgeType) ) {
			// if we have a location ID, this is actually a GeneralLocationDatum
			final JsonNode locId = Edge.get(LOCATION_ID_FIELD);
			if ( locId != null && locId.isNumber() ) {
				return handleGeneralLocationDatum(Edge);
			}
			return handleGeneralNodeDatum(Edge);
		} else if ( INSTRUCTION_STATUS_TYPE.equalsIgnoreCase(EdgeType) ) {
			return handleInstructionStatus(Edge);
		} else if ( Edge_CONTROL_INFO_TYPE.equalsIgnoreCase(EdgeType) ) {
			return handleEdgeControlInfo(Edge);
		} else {
			return handleLegacyDatum(Edge);
		}
	}

	private String getStringFieldValue(JsonNode Edge, String fieldName, String placeholder) {
		JsonNode child = Edge.get(fieldName);
		return (child == null ? placeholder : child.asText());
	}

	private HardwareControlDatum handleEdgeControlInfo(JsonNode Edge) {
		HardwareControlDatum datum = null;
		String controlId = getStringFieldValue(Edge, "controlId", null);
		String propertyName = getStringFieldValue(Edge, "propertyName", null);
		String value = getStringFieldValue(Edge, "value", null);
		String type = getStringFieldValue(Edge, "type", null);
		if ( type != null && value != null ) {
			datum = new HardwareControlDatum();

			EdgeControlPropertyType t = EdgeControlPropertyType.valueOf(type);
			switch (t) {
				case Boolean:
					if ( value.length() > 0 && (value.equals("1") || value.equalsIgnoreCase("yes")
							|| value.equalsIgnoreCase("true")) ) {
						datum.setIntegerValue(Integer.valueOf(1));
					} else {
						datum.setIntegerValue(Integer.valueOf(0));
					}
					break;

				case Integer:
					datum.setIntegerValue(Integer.valueOf(value));
					break;

				case Float:
				case Percent:
					datum.setFloatValue(Float.valueOf(value));
					break;

			}
			String sourceId = controlId;
			if ( propertyName != null ) {
				sourceId += ";" + propertyName;
			}
			datum.setSourceId(sourceId);
		}

		return datum;
	}

	private Instruction handleInstructionStatus(JsonNode Edge) {
		String instructionId = getStringFieldValue(Edge, "instructionId", null);
		String status = getStringFieldValue(Edge, "status", null);
		Map<String, Object> resultParams = JsonUtils.getStringMapFromTree(Edge.get("resultParameters"));
		Instruction result = null;
		InstructorBiz biz = getInstructorBiz().service();
		if ( instructionId != null && status != null && biz != null ) {
			Long id = Long.valueOf(instructionId);
			InstructionState state = InstructionState.valueOf(status);
			biz.updateInstructionState(id, state, resultParams);
			result = new Instruction();
			result.setId(id);
			result.setState(state);
			result.setResultParameters(resultParams);
			return result;
		}
		return result;
	}

	private Object handleLegacyDatum(JsonNode Edge) {
		String className = getStringFieldValue(Edge, OBJECT_TYPE_FIELD, null);
		if ( className == null ) {
			return null;
		}
		className = "org.eniware.central.datum.domain." + className;

		Class<?> datumClass = null;
		Object datum = null;
		try {
			datumClass = Class.forName(className, true, Datum.class.getClassLoader());
			datum = objectMapper.treeToValue(Edge, datumClass);
		} catch ( ClassNotFoundException e ) {
			if ( log.isWarnEnabled() ) {
				log.warn("Unable to load Datum class " + className + " specified in JSON");
			}
			return null;
		} catch ( IOException e ) {
			log.debug("Unable to parse JSON into {} class: {}", className, e.getMessage());
			return null;
		}

		if ( log.isTraceEnabled() ) {
			log.trace("Parsed datum " + datum + " from JSON");
		}

		return datum;
	}

	private GeneralEdgeDatum handleGeneralNodeDatum(JsonNode Edge) {
		try {
			return objectMapper.treeToValue(Edge, GeneralEdgeDatum.class);
		} catch ( IOException e ) {
			log.debug("Unable to parse JSON into GeneralEdgeDatum: {}", e.getMessage());
			return null;
		}
	}

	private GeneralLocationDatum handleGeneralLocationDatum(JsonNode Edge) {
		try {
			return objectMapper.treeToValue(Edge, GeneralLocationDatum.class);
		} catch ( IOException e ) {
			log.debug("Unable to parse JSON into GeneralLocationDatum: {}", e.getMessage());
			return null;
		}
	}

}
