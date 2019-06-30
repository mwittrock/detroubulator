package org.detroubulator.core;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.detroubulator.mappingprograms.MappingOutput;
import org.detroubulator.server.XIServerRFCImpl;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class ValidationAssertion implements Assertion {

	private static Logger log = Logging.getLogger(XIServerRFCImpl.class);
	
	private Validator validator;
	private String failureMessage;
	private String humanMessage;
	private File schemaFile;
	
	public ValidationAssertion(File schema, SchemaType type) throws AssertionException {
		if (schema == null) {
			throw new NullPointerException("Null parameter: schema");
		}
		if (type == null) {
			throw new NullPointerException("Null parameter: type");
		}
		if (type != SchemaType.xsd) {
			/*
			 * XML Schema is the only supported schema type at the moment.
			 */
			throw new IllegalArgumentException(String.format("Unsupported schema type: %s", type));
		}
		if (!schema.exists()) {
			throw new IllegalArgumentException(String.format("Schema file does not exist: %s", schema.getPath()));
		}
		if (!schema.isFile()) {
			throw new IllegalArgumentException(String.format("Schema file is not a file: %s", schema.getPath()));
		}
		schemaFile = schema;
		/*
		 * The following assumes that the schema type is XML Schema!
		 */
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema xsd;
		try {
			xsd = factory.newSchema(schema);
		} catch (SAXException se) {
			throw new AssertionException("Error processing schema", se);
		}
		validator = xsd.newValidator();
	}

	public boolean evaluate(MappingOutput mo) throws AssertionException {
		if (mo == null) {
			throw new NullPointerException("Null parameter: mo");
		}
		boolean result;
		if (mo.hasFailure()) {
			result = false;
			failureMessage = String.format("The mapping failed on the server side (%s)", mo.getFailure().getMessage());
		} else {
			try {
				/*
				 * The validate method requires a Source argument, but the
				 * implementing class cannot be StreamSource (that would
				 * make using it too easy ;-). Hence the following workaround.
				 */
				Source payload = new SAXSource(new InputSource(mo.getPayload()));
				/*
				 * The call to validate will throw a SAXException if the
				 * mapping output does not validate.
				 */
				validator.validate(payload);
				result = true;
				failureMessage = null;
			} catch (IOException ioe) {
				throw new AssertionException("I/O error while validating mapping output", ioe);
			} catch (SAXException se) {
				result = false;
				log.fine(String.format("Validation error: %s" , se.getMessage()));
				if (humanMessage != null) {
					failureMessage = humanMessage;
				} else {
					failureMessage = String.format("Validation of mapping output against schema '%s' failed", schemaFile.getPath());
				}
			}
		}
		return result;
	}

	public String getFailureMessage() {
		if (failureMessage == null) {
			throw new IllegalStateException("No failure message available");
		}
		return failureMessage;
	}

	public void setFailureMessage(String message) {
		if (message == null) {
			throw new NullPointerException("Null parameter: message");
		}
		humanMessage = message;
	}
	
}
