/*
 * Copyright 2006, 2007 AppliCon A/S
 * 
 * This file is part of Detroubulator.
 * 
 * Detroubulator is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Detroubulator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Detroubulator; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.detroubulator.core;

import static org.detroubulator.util.XmlUtil.getChildElementByTagName;
import static org.detroubulator.util.XmlUtil.getChildElementsByTagName;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPathExpressionException;

import org.detroubulator.mappingprograms.MappingProgram;
import org.detroubulator.mappingprograms.MappingProgramFactory;
import org.detroubulator.mappingprograms.MappingProgramType;
import org.detroubulator.reports.TestReport;
import org.detroubulator.reports.TestReportFactory;
import org.detroubulator.reports.TestReportType;
import org.detroubulator.server.ServerException;
import org.detroubulator.server.XIServer;
import org.detroubulator.server.XIServerFactory;
import org.detroubulator.util.Configurable;
import org.detroubulator.util.ConfigurationException;
import org.detroubulator.util.NamespaceContextImpl;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

final class TestSuiteFactory {
	
	private static final String TESTFILE_SCHEMA_RESOURCE_PATH = "resources/testfile_schema.xsd";

	private TestSuiteFactory() {
		// Not supposed to be instantiated.
	}
	
	public static TestSuite createFromFile(File testFile) throws IOException, TestFileException, ServerException, ConfigurationException  {
		assert testFile.exists();
		assert testFile.isFile();
		// Validate the test file.
		validate(testFile);
		// Test file is valid, start parsing.
		DocumentBuilderFactory dbfact = DocumentBuilderFactory.newInstance();
		Element docElement;
		try {
			DocumentBuilder parser = dbfact.newDocumentBuilder();
			docElement = parser.parse(new FileInputStream(testFile)).getDocumentElement();
		} catch (Exception e) {
			throw new TestFileException("Error parsing test file", e);
		}
        // Get an XIServer instance
        XIServer server = getXIServerInstance(docElement);
		// Get a MappingProgram instance.
		MappingProgram mp = getMappingProgramInstance(docElement);
        mp.setServer(server);
		// Get the global TransformationParams instance
		TransformationParams params = getTransformationParamsInstance(null, docElement);
		// Get a List of TestReport instances.
		List<TestReport> reports = getTestReportList(docElement);
		// Get a NamespaceContext instance.
		NamespaceContext nsContext = getNamespaceContextInstance(docElement);
		// Get a List of TestCase instances.
		List<TestCase> testCases = getTestCaseList(docElement, nsContext, params);
		// Construct a TestSuite object.
		TestSuite suite = new TestSuite(mp, reports, testCases);
		// We're done.
		return suite;
	}

	private static Map<String, List<String>> getConfigurationMap(Element configurable) {
		Map<String, List<String>> params = new HashMap<String, List<String>>();
		Element configElement = getChildElementByTagName(configurable, "configuration");
		List<Element> paramElements = getChildElementsByTagName(configElement, "parameter");
		for (Element param : paramElements) {
			String name = param.getAttribute("name");
			String value = param.getTextContent(); // The empty string if the element has no child nodes.
			if (params.containsKey(name)) {
				params.get(name).add(value);
			} else {
				List<String> values = new ArrayList<String>();
				values.add(value);
				params.put(name, values);
			}
		}
		return params;
	}
	
	private static void validate(File testFile) throws IOException, TestFileException {
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new StreamSource(TestSuiteFactory.class.getClassLoader().getResourceAsStream(TESTFILE_SCHEMA_RESOURCE_PATH)));
			Validator validator = schema.newValidator();
			Source documentSource = new SAXSource(new InputSource(new FileInputStream(testFile)));
			validator.validate(documentSource);
		} catch (SAXException se) {
			throw new TestFileException("Test file is not valid: " + se.getMessage(), se);
		}
	}
    
    private static XIServer getXIServerInstance(Element docElement) throws ServerException, ConfigurationException  {
        Element mpElement = getChildElementByTagName(docElement, "server");
        Map<String, List<String>> srvConfig = getConfigurationMap(mpElement);
        XIServer srv = XIServerFactory.newInstance(srvConfig);
        return srv;
    }

	private static MappingProgram getMappingProgramInstance(Element docElement) throws TestFileException {
		Element mpElement = getChildElementByTagName(docElement, "program");
		MappingProgramType mpType = MappingProgramType.valueOf(mpElement.getAttribute("type"));
		Map<String, List<String>> mpConfig = getConfigurationMap(mpElement);
		MappingProgram mp;
		try {
			mp = MappingProgramFactory.newInstance(mpType, mpConfig);
		} catch (ConfigurationException ce) {
			throw new TestFileException("Mapping program not correctly configured: " + ce.getMessage(), ce);
		}
		return mp;
	}

	private static List<TestReport> getTestReportList(Element docElement) throws TestFileException, ConfigurationException  {
		Element repElement = getChildElementByTagName(docElement, "reporting");
		List<TestReport> reports = new ArrayList<TestReport>();
		List<Element> trElements = getChildElementsByTagName(repElement, "testreport");
		for (Element trElement : trElements) {
			Map<String, List<String>> trConfig = getConfigurationMap(trElement);
			/*
			 * The <testreport> element must have either a type attribute
			 * or a class attribute. This cannot be expressed in XML
			 * Schema, however. Both attributes are optional in the
			 * schema, which means that we need to check that exactly one
			 * of them is present. If none of them or both are present,
			 * the test file is invalid.
			 */
			String attrType = trElement.getAttribute("type");
			String attrClass = trElement.getAttribute("class");
			if (!("".equals(attrType) ^ "".equals(attrClass))) {
				throw new TestFileException("All testreport elements should have either a type attribute or a class attribute");
			}
			TestReport tr;
			if (!"".equals(attrType)) {
				// Test report identified by type.
				TestReportType trType = TestReportType.valueOf(attrType);
				tr = TestReportFactory.newInstance(trType, trConfig);
			} else {
				// Test report identified by class name.
				assert !"".equals(attrClass);
				Class reportClass;
				try {
					reportClass = Class.forName(attrClass);
					tr = TestReportFactory.newInstance(reportClass, trConfig);
				} catch (ClassNotFoundException cnfe) {
					throw new TestFileException("Could not load test report class: " + attrClass, cnfe);
				} catch (IllegalArgumentException iae) {
					throw new TestFileException(iae.getMessage(), iae);
				} catch (InstantiationException ie) {
					throw new TestFileException("Could not instantiate test report class: " + attrClass, ie);
				} catch (IllegalAccessException iace) {
					throw new TestFileException("Access error creating test report class: " + attrClass, iace);
				}
			}
			reports.add(tr);
		}
		return reports;
	}

	private static NamespaceContext getNamespaceContextInstance(Element docElement) {
		NamespaceContextImpl nsContext = new NamespaceContextImpl();
		Element nscElement = getChildElementByTagName(docElement, "namespacecontext");
		/*
		 * If no namespacecontext element exists, an empty NamespaceContext
		 * implementation will be returned. 
		 */
		if (nscElement != null) {
			List<Element> nsElements = getChildElementsByTagName(nscElement, "namespace");
			for (Element nsElement : nsElements) {
				Element prefixElement = getChildElementByTagName(nsElement, "prefix");
				Element uriElement = getChildElementByTagName(nsElement, "uri");
				nsContext.add(prefixElement.getTextContent(), uriElement.getTextContent());
			}
		}
		return nsContext;
	}

	private static Map<String, List<Assertion>> getAssertionGroups(Element sharedElement, NamespaceContext nsContext) throws TestFileException, ConfigurationException {
		Map<String, List<Assertion>> sharedAsserts = new HashMap<String, List<Assertion>>();
		List<Element> assertGroups = getChildElementsByTagName(sharedElement, "assertiongroup");
		for (Element agElement : assertGroups) {
			String groupName = agElement.getAttribute("name");
			if (sharedAsserts.containsKey(groupName)) {
				/*
				 * The group name is already in use.
				 */
				throw new TestFileException(String.format("Assertion group names must be unique, but the name '%s' occurs more than once", groupName));
			}
			List<Assertion> groupAsserts = getAssertionList(agElement, nsContext);
			if (groupAsserts.isEmpty()) {
				/*
				 * An assertion group must contain assertions.
				 */
				throw new TestFileException(String.format("Assertion group '%s' does not contain any assertions", groupName));
			}
			sharedAsserts.put(groupName, groupAsserts);
		}
		return sharedAsserts;
	}
	
	private static List<TestCase> getTestCaseList(Element docElement, NamespaceContext nsContext, TransformationParams globalParams) throws TestFileException, ConfigurationException {
		Element testsElement = getChildElementByTagName(docElement, "tests");
		Map<String, List<Assertion>> sharedAsserts = new HashMap<String, List<Assertion>>();
		/*
		 * If the <shared> element is present, load the shared assertion groups into a Map
		 * that maps from the group's name to the List of the assertions it contains. 
		 */
		Element shared = getChildElementByTagName(testsElement, "shared");
		if (null != shared) {
			sharedAsserts = getAssertionGroups(shared, nsContext);
		}
		List<TestCase> testCases = new ArrayList<TestCase>();
		List<Element> tcElements = getChildElementsByTagName(testsElement, "testcase");
		for (Element tcElement : tcElements) {
			Element inputDocElement = getChildElementByTagName(tcElement, "inputdocument");
			// Make sure that the input document exists
			File inputDocument = new File(inputDocElement.getTextContent());
			if (!inputDocument.exists()) {
				throw new TestFileException(String.format("Input document does not exist: %s", inputDocument.getPath()));
			}
			if (!inputDocument.isFile()) {
				throw new TestFileException(String.format("Input document is not a file: %s", inputDocument.getPath()));
			}
			// Get a local TransformationParams instance based on the global transformation parameters.
			TransformationParams params = getTransformationParamsInstance(globalParams, tcElement);
			/*
			 * Now, the <assertions> element can either:
			 *   1. refer to shared assertion groups via the include attribute,
			 *   2. contain its own assertion children or
			 *   3. do both
			 * It must, however, contain assertions, either by including or directly.
			 */
			Element assertionsElement = getChildElementByTagName(tcElement, "assertions");
			List<Assertion> assertions = new ArrayList<Assertion>();
			String includes = assertionsElement.getAttribute("include");
			if (!"".equals(includes)) {
				String[] groups = includes.split("\\s"); // Split by whitespace
				for (String group : groups) {
					if (!sharedAsserts.containsKey(group)) {
						throw new TestFileException(String.format("Reference to unknown assertion group '%s'", group));
					}
					assertions.addAll(sharedAsserts.get(group));
				}
			}
			/*
			 * Add the assertion children the <assertions> element might have.
			 */
			assertions.addAll(getAssertionList(assertionsElement, nsContext));
			/*
			 * Each test case must contain at least one assertion (either directly or
			 * by referencing a shared assertion group.
			 */
			if (assertions.isEmpty()) {
				throw new TestFileException("Each test case must contain at least one assertion");
			}
			/*
			 * The list of assertions must be sane, i.e. not contain both exception assertions
			 * and non-exception assertions.
			 */
			if (!assertionsAreSane(assertions)) {
				throw new TestFileException("List of assertions is not sane (it cannot contain both exception and non-exception assertions)");
			}
			TestCase tc = new TestCase(inputDocument, assertions, params);
			testCases.add(tc);
		}
		return testCases;
	}
	
	private static List<Assertion> getAssertionList(Element parent, NamespaceContext nsContext) throws TestFileException, ConfigurationException {
		List<Assertion> assertions = new ArrayList<Assertion>();
		List<Element> assertElements = getChildElementsByTagName(parent, "*"); // * matches all child elements.
		for (Element assertElement : assertElements) {
			String tagName = assertElement.getTagName();
			Assertion assertObject;
			// Wouldn't it be nice if you could switch on string values?
			if ("content".equals(tagName)) {
				String xpath = assertElement.getAttribute("xpath");
				ContentAssertion ca;
				try {
					ca = new ContentAssertion(xpath, nsContext);
				} catch (XPathExpressionException e) {
					throw new TestFileException("Error compiling XPath expression: " + xpath, e);
				}
				if (assertElement.hasAttribute("expect")) {
					/*
					 * Expected value provided.
					 */
					XPathScalar expected;
					String expval = assertElement.getAttribute("expect");
					try {
						expected = XPathScalar.valueOf(expval);
					} catch (Exception e) {
						throw new TestFileException(String.format("Bad expected value: %s", expval), e);
					}
					ca.setExpectedValue(expected);
				}
				String message = assertElement.getTextContent();
				if (!"".equals(message)) {
					/*
					 * Failure message provided.
					 */
					ca.setFailureMessage(message);
				}
				assertObject = ca;
			} else if ("exception".equals(tagName)) {
				ExceptionAssertion ea = new ExceptionAssertion();
				String message = assertElement.getTextContent();
				if (!"".equals(message)) {
					/*
					 * Failure message provided.
					 */
					ea.setFailureMessage(message);
				}
				assertObject = ea;
			} else if ("validate".equals(tagName)) {
				File schemaFile = new File(assertElement.getAttribute("schema"));
				SchemaType type = SchemaType.valueOf(assertElement.getAttribute("type"));
				ValidationAssertion va;
				try {
					va = new ValidationAssertion(schemaFile, type);
				} catch (AssertionException ae) {
					throw new TestFileException("Error processing schema", ae);
				} catch (Exception e) {
					throw new TestFileException(e);
				}
				String message = assertElement.getTextContent();
				if (!"".equals(message)) {
					/*
					 * Failure message provided.
					 */
					va.setFailureMessage(message);
				}
				assertObject = va;
			} else if ("external".equals(tagName)) {
				String attrClass = assertElement.getAttribute("class");
				Class assertClass;
				Assertion a;
				try {
					assertClass = Class.forName(attrClass);
					Object o = assertClass.newInstance();
					if (!(o instanceof Assertion)) {
						throw new TestFileException("Class " + assertClass.getName() + " does not implement the Assertion interface");
					}
					a = (Assertion)o;
				} catch (ClassNotFoundException cnfe) {
					throw new TestFileException("Could not load external assertion class: " + attrClass, cnfe);
				} catch (IllegalArgumentException iae) {
					throw new TestFileException(iae.getMessage(), iae);
				} catch (InstantiationException ie) {
					throw new TestFileException("Could not instantiate external assertion class: " + attrClass, ie);
				} catch (IllegalAccessException iace) {
					throw new TestFileException("Access error creating external assertion class: " + attrClass, iace);
				}
				/*
				 * Now, if the external assertion class implements the Configurable
				 * interface, configure it using the configuration parameters found
				 * in the <configuration> element, which must be present.
				 */
				if (a instanceof Configurable) {
					if (getChildElementByTagName(assertElement, "configuration") != null) {
						Map<String, List<String>> params = getConfigurationMap(assertElement);
						Configurable ca = (Configurable)a;
						ca.configure(params);
						if (!ca.isConfigured()) {
							throw new TestFileException(String.format("External assertion class %s not properly configured", attrClass));
						}
					} else {
						throw new TestFileException(String.format("External assertion class %s requires a <configuration> element", attrClass));
					}
				}
				assertObject = a;
			} else {
				// If the test file is valid, this should never happen.
				assert false : String.format("Unexpected assertion: %s", tagName);
				break;
			}
			assertions.add(assertObject);
		}
		return assertions;
	}

	private static TransformationParams getTransformationParamsInstance(TransformationParams base, Element parent) throws TestFileException {
		TransformationParams tp = (base == null ? new TransformationParams() : new TransformationParams(base));
		Element tpElement = getChildElementByTagName(parent, "transformationparams");
		if (tpElement != null) {
			// The parent element has a <transformationparams> child.
			List<Element> paramElements = getChildElementsByTagName(tpElement, "parameter");
			for (Element parameter : paramElements) {
				String name = parameter.getAttribute("name");
				String value = parameter.getTextContent(); // The empty string if the element has no child nodes.
				try {
					tp.addParameter(name, value);
				} catch (IllegalArgumentException e) {
					/*
					 * This exception occurs when the parameter name does not match
					 * a field in SAP's StreamTransformationConstants interface. 
					 */
					throw new TestFileException(e.getMessage(), e);
				}
			}
		}
		return tp;
	}

	private static boolean assertionsAreSane(List<Assertion> asserts) {
		/*
		 * If the list of assertions associated with a test case contains
		 * more than one assertion, either all of them or none of them
		 * must be the exception assertion.
		 */
		boolean sane = true;
		if (asserts.size() > 1) {
			boolean hasExceptions = false;
			boolean hasOthers = false;
			for (Assertion a : asserts) {
				if (a instanceof ExceptionAssertion) {
					hasExceptions = true;
				} else {
					hasOthers = true;
				}
				if (hasExceptions && hasOthers) {
					/*
					 * We've seen enough, thank you very much.
					 */
					break;
				}
			}
			sane = hasExceptions ^ hasOthers;
		}
		return sane;
	}
	
}