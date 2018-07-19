/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.in.web;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.eniware.central.datum.domain.Datum;
import org.eniware.central.datum.domain.HardwareControlDatum;
import org.eniware.central.in.biz.DataCollectorBiz;
import org.eniware.central.security.AuthenticatedNode;
import org.eniware.central.security.SecurityException;
import org.eniware.domain.EdgeControlPropertyType;

import org.eniware.central.RepeatableTaskException;
import org.eniware.central.dao.EniwareEdgeDao;
import org.eniware.central.instructor.domain.Instruction;
import org.eniware.central.instructor.domain.InstructionState;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for accepting bulk upload XML documents.
 * 
 * <p>
 * This controller expects an {@link AuthenticatedNode} to be available on each
 * request for securely processing node data.
 * </p>
 * 
 * @version 1.1
 * @deprecated see {@link BulkJsonDataCollector}
 */
@Deprecated
@Controller
@RequestMapping(value = { "/bulkCollector.do", "/u/bulkCollector.do" })
public class BulkDataCollector extends AbstractDataCollector {

	/** The InstructionStatus element name. */
	public static final String INSTRUCTION_STATUS_ELEMENT_NAME = "InstructionStatus";

	/** The NodeControlInfo element name. */
	public static final String NODE_CONTROL_INFO_ELEMENT_NAME = "NodeControlInfo";

	private static final String NODE_ID_GLOBAL_PARAM = "nodeId";

	/**
	 * Default constructor.
	 */
	public BulkDataCollector() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param dataCollectorBiz
	 *        the {@link DataCollectorBiz} to use
	 * @param eniwareEdgeDao
	 *        the {@link EniwareEdgeDao} to use
	 */
	@Autowired
	public BulkDataCollector(DataCollectorBiz dataCollectorBiz, EniwareEdgeDao eniwareEdgeDao) {
		setDataCollectorBiz(dataCollectorBiz);
		setEniwareEdgeDao(eniwareEdgeDao);
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
	@RequestMapping(method = RequestMethod.POST)
	public String postData(@RequestHeader(value = "Content-Encoding", required = false) String encoding,
			InputStream in, Model model) throws IOException {
		AuthenticatedNode authNode = getAuthenticatedNode(false);

		InputStream input = in;
		if ( encoding != null && encoding.toLowerCase().contains("gzip") ) {
			input = new GZIPInputStream(in);
		}

		List<Datum> parsedDatum = new ArrayList<Datum>();
		List<Object> results = new ArrayList<Object>();
		Map<String, Object> globalAttributes = new HashMap<String, Object>();

		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		xmlInputFactory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
		xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
		xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
		try {
			XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(input);

			// once we pass the root element, assume all elements are datum records
			boolean rootFound = false;
			while ( reader.hasNext() ) {
				switch (reader.next()) {
					case XMLStreamReader.START_ELEMENT:
						if ( rootFound ) {
							Object o = handleElement(reader, globalAttributes);
							if ( o instanceof Datum ) {
								parsedDatum.add((Datum) o);
							} else if ( o instanceof Instruction ) {
								results.add(o);
							}
						} else {
							int count = reader.getAttributeCount();
							for ( int i = 0; i < count; i++ ) {
								globalAttributes.put(reader.getAttributeLocalName(i),
										reader.getAttributeValue(i));
							}
							if ( authNode != null ) {
								// set nodeId to authenticated node ID
								Long nodeId = authNode.getNodeId();
								if ( nodeId != null ) {
									globalAttributes.put(NODE_ID_GLOBAL_PARAM, authNode.getNodeId());
								}
							}
							rootFound = true;
						}
				}
			}
		} catch ( XMLStreamException e ) {
			throw new RuntimeException(e);
		}

		try {
			List<Datum> postedDatum = getDataCollectorBiz().postDatum(parsedDatum);
			results.addAll(postedDatum);
			model.addAttribute(MODEL_KEY_RESULT, results);
		} catch ( RepeatableTaskException e ) {
			if ( log.isDebugEnabled() ) {
				Throwable root = e;
				while ( root.getCause() != null ) {
					root = root.getCause();
				}
				log.debug("RepeatableTaskException caused by: " + root.getMessage());
			}
		}

		if ( authNode != null ) {
			defaultHandleNodeInstructions(authNode.getNodeId(), model);
		}

		return getViewName();
	}

	@ExceptionHandler(SecurityException.class)
	public void handleSecurityException(SecurityException e, HttpServletResponse res) {
		if ( log.isWarnEnabled() ) {
			log.warn("Security exception: " + e.getMessage());
		}
		res.setStatus(HttpServletResponse.SC_FORBIDDEN);
	}

	/**
	 * Parse InstructionStatus XML.
	 * 
	 * <p>
	 * The XML looks like this:
	 * </p>
	 * 
	 * <pre>
	 * &lt;InstructionStatus instructionId="123" status="Received"/>
	 * </pre>
	 * 
	 * @param reader
	 *        the stream reader
	 * @param attributes
	 *        global attributes
	 * @return
	 */
	private Object handleInstructionStatusElement(XMLStreamReader reader, Map<String, Object> attributes) {
		String instructionId = reader.getAttributeValue(null, "instructionId");
		String status = reader.getAttributeValue(null, "status");
		Instruction result = null;
		if ( instructionId != null && status != null && getInstructorBiz().isAvailable() ) {
			Long id = Long.valueOf(instructionId);
			InstructionState state = InstructionState.valueOf(status);
			getInstructorBiz().getService().updateInstructionState(id, state);
			result = new Instruction();
			result.setId(id);
			result.setState(state);
			return result;
		}
		return result;
	}

	private Object handleElement(XMLStreamReader reader, Map<String, Object> attributes)
			throws XMLStreamException {
		// we are on a START_ELEMENT event here, so create bean from datum
		// class and all attributes into JavaBean style properties
		String className = reader.getLocalName();
		if ( className.equals(INSTRUCTION_STATUS_ELEMENT_NAME) ) {
			// handle instruction status specially
			return handleInstructionStatusElement(reader, attributes);
		} else if ( className.equals(NODE_CONTROL_INFO_ELEMENT_NAME) ) {
			return handleNodeControlInfoElement(reader, attributes);
		}
		className = "org.eniware.central.datum.domain." + className;
		Datum datum = null;
		try {
			@SuppressWarnings("unchecked")
			Class<? extends Datum> datumClass = (Class<? extends Datum>) Class.forName(className, true,
					Datum.class.getClassLoader());
			datum = datumClass.newInstance();
		} catch ( ClassNotFoundException e ) {
			if ( log.isWarnEnabled() ) {
				log.warn("Unable to load Datum class " + className + " specified in XML");
			}
			return null;
		} catch ( InstantiationException e ) {
			if ( log.isWarnEnabled() ) {
				log.warn("Unable to instantiate Datum class " + className + " specified in XML: "
						+ e.getMessage());
			}
			return null;
		} catch ( IllegalAccessException e ) {
			if ( log.isWarnEnabled() ) {
				log.warn("Unable to access Datum class " + className + " specified in XML: "
						+ e.getMessage());
			}
			return null;
		}
		BeanWrapper bean = PropertyAccessorFactory.forBeanPropertyAccess(datum);
		bean.registerCustomEditor(DateTime.class, (PropertyEditor) DATE_TIME_EDITOR.clone());
		bean.registerCustomEditor(LocalDate.class, (PropertyEditor) LOCAL_DATE_EDITOR.clone());
		bean.registerCustomEditor(LocalTime.class, (PropertyEditor) LOCAL_TIME_EDITOR.clone());

		// apply global attributes
		MutablePropertyValues pvs = new MutablePropertyValues(attributes);
		bean.setPropertyValues(pvs, true, true);

		int propertyCount = reader.getAttributeCount();
		for ( int i = 0; i < propertyCount; i++ ) {
			String attrName = reader.getAttributeLocalName(i);
			if ( attrName.equalsIgnoreCase("id") ) {
				// skip ID attributes on incoming data!
				continue;
			}
			String attrValue = reader.getAttributeValue(i);
			try {
				bean.setPropertyValue(attrName, attrValue);
			} catch ( InvalidPropertyException e ) {
				if ( log.isDebugEnabled() ) {
					log.debug("Unable to set property [" + attrName + "] to [" + attrValue
							+ "] on instance of " + reader.getLocalName());
				}
			} catch ( PropertyAccessException e ) {
				if ( log.isDebugEnabled() ) {
					log.debug("Unable to set property [" + attrName + "] to [" + attrValue
							+ "] on instance of " + reader.getLocalName());
				}
			}
		}

		if ( log.isTraceEnabled() ) {
			log.trace("Parsed datum " + datum + " from XML");
		}

		return datum;
	}

	private Object handleNodeControlInfoElement(XMLStreamReader reader, Map<String, Object> attributes)
			throws XMLStreamException {
		HardwareControlDatum datum = null;
		String controlId = reader.getAttributeValue(null, "controlId");
		String propertyName = reader.getAttributeValue(null, "propertyName");
		String value = reader.getAttributeValue(null, "value");
		String type = reader.getAttributeValue(null, "type");
		if ( type != null && value != null ) {
			datum = new HardwareControlDatum();

			EdgeControlPropertyType t = EdgeControlPropertyType.valueOf(type);
			switch (t) {
				case Boolean:
					if ( value.length() > 0
							&& (value.equals("1") || value.equalsIgnoreCase("yes") || value
									.equalsIgnoreCase("true")) ) {
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

}
