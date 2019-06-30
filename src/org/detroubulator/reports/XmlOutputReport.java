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

package org.detroubulator.reports;

import static org.detroubulator.util.XmlUtil.appendChild;
import static org.detroubulator.util.XmlUtil.appendSimpleChild;
import static org.detroubulator.util.XmlUtil.getChildElementByTagName;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.detroubulator.core.Assertion;
import org.detroubulator.core.TestCase;
import org.detroubulator.mappingprograms.MappingOutput;
import org.detroubulator.mappingprograms.MappingProgram;
import org.detroubulator.util.ConfigurationException;
import org.detroubulator.util.Console;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

class XmlOutputReport extends StatsCollector implements TestReport {

    private static final Set<String> MANDATORY_PARAMS;

    static {
        MANDATORY_PARAMS = new HashSet<String>();
        MANDATORY_PARAMS.add("file");
        MANDATORY_PARAMS.add("dumpdir");
    }
	
	private File output;
	private boolean configured;
	private DumpHandler dump;
    protected Transformer stylesheet;
    private MappingProgram program;
    private TestCase currentTestCase;
    private MappingOutput currentOutput;
    private Document report;
    private Node header;
    private Node details;
    private Node currFailure;
	
	XmlOutputReport() {
		configured = false;
	}
	
	public boolean isConfigured() {
		return configured;
	}

	public void configure(Map<String, List<String>> params) throws ConfigurationException {
        /*
         * Make sure that we've got at least all the mandatory parameters.
         */
        for (String mandatory : MANDATORY_PARAMS) {
            if (!params.containsKey(mandatory)) {
                throw new ConfigurationException("Mandatory parameter not present: " + mandatory);
            }
        }
        // We're expecting each List<String> to contain at least one String.
        for (List<String> l : params.values()) {
            assert l.size() > 0;
        }
        File out = new File(params.get("file").get(0));
        /*
         * Files that do not yet exist are allowed but an existing
         * directory is an error.
         */
        if (out.exists() && out.isDirectory()) {
        	throw new ConfigurationException("Output file is a directory: " + out.getPath());
        }
        output = out;
        DumpHandlerFactory dhfact = DumpHandlerFactory.newInstance();
        dump = dhfact.newDumpHandler(new File(params.get("dumpdir").get(0)));
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
	        if (params.containsKey("stylesheet")) {
	        	File xslt = new File(params.get("stylesheet").get(0));
	        	if (!xslt.exists()) {
	        		throw new ConfigurationException("Stylesheet does not exist: " + xslt.getPath());
	        	}
	        	if (!xslt.isFile()) {
	        		throw new ConfigurationException("Stylesheet is not a file: " + xslt.getPath());
	        		
	        	}
	    			stylesheet = tf.newTransformer(new StreamSource(xslt));
	        } else {
	        	stylesheet = tf.newTransformer();
	        }
		} catch (TransformerConfigurationException e) {
			throw new ConfigurationException("Error creating stylesheet transformer: " + e.getMessage(), e);
		}
        configured = true;
	}
	
	public void startTesting(MappingProgram program) throws ReportException {
		super.startTesting(program);
		this.program = program;
		/*
		 * Create the XML document and the two top-level elements: 
		 * "header" and "details".
		 */
		try {
			report = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw new ReportException("Error creating XML document: " + e.getMessage(), e);
		}
		Node root = report.appendChild(report.createElement("testreport"));
		header = root.appendChild(report.createElement("header"));
		details = root.appendChild(report.createElement("details"));
	}
	
	public void startTestCase(TestCase tc, MappingOutput output) throws ReportException {
		super.startTestCase(tc, output);
		currentTestCase = tc;
		currentOutput = output;
	}
	
	public void assertionFailed(Assertion failed) throws ReportException {
		super.assertionFailed(failed);
		Node messagesNode;
		if (currFailure == null) {
			/*
			 * This is the first assertion to fail in the current
			 * test case, i.e. a "failure" element has not yet
			 * been created.
			 */
			currFailure = details.appendChild(report.createElement("failure"));
			try {
				addInputElement();
				addOutputElement();
			} catch (IOException e) {
				throw new RuntimeException("XML error");
			}
			messagesNode = currFailure.appendChild(report.createElement("messages"));
		} else {
			/*
			 * Other assertions in the current test case have failed,
			 * so we only need to retrieve the "messages" element.
			 */
			messagesNode = getChildElementByTagName(currFailure, "messages");
		}
		messagesNode.appendChild(report.createElement("message")).appendChild(report.createTextNode(failed.getFailureMessage()));
	}

	public void endTestCase() throws ReportException {
		super.endTestCase();
		currentTestCase = null;
		currentOutput = null;
		currFailure = null;
	}

	public void endTesting() throws ReportException {
		super.endTesting();
		/*
		 * The "details" element has already been populated with
		 * data. What remains is to add the header information
		 */
		addHeaderElements();
		try {
			writeReport();
			Console.startSection(getConsoleLabel());
			Console.p("Report written to file: %s", output.getPath());
			Console.endSection();
		} catch (TransformerException e) {
			throw new ReportException("Error transforming report: " + e.getMessage(), e);
		}
	}
	
	private void writeReport() throws TransformerException {
		stylesheet.setOutputProperty(OutputKeys.INDENT, "yes");
		stylesheet.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		stylesheet.transform(new DOMSource(report.getDocumentElement()), 
				new StreamResult(output));
	}

	private void addHeaderElements() {
		appendSimpleChild(header, "program", program.toString());
		appendSimpleChild(header, "server", program.getServer().toString());
		appendSimpleChild(header, "started", startedAt.toString());
		appendSimpleChild(header, "finished", finishedAt.toString());
		Node exectimeNode = appendChild(header, "exectime");
		appendSimpleChild(exectimeNode, "millis", Long.toString(getExecutionTimeMillis()));
		appendSimpleChild(exectimeNode, "seconds", formatExecutionTime(getExecutionTimeMillis()));
		appendSimpleChild(header, "testcases", Integer.toString(testCases));
		Node assertionsNode = appendChild(header, "assertions"); 
		appendSimpleChild(assertionsNode, "passed", Integer.toString(passedAssertions));
		appendSimpleChild(assertionsNode, "failed", Integer.toString(failedAssertions));
	}

	private void addInputElement() throws MalformedURLException {
		assert currFailure != null;
		assert report != null;
		File inputDoc = currentTestCase.getInputDoc();
		Node inputNode = currFailure.appendChild(report.createElement("input"));
		addFileInformation(inputNode, inputDoc);
	}
	
	private void addOutputElement() throws MalformedURLException, IOException {
		assert currFailure != null;
		assert report != null;
		if (!currentOutput.hasPayload()) {
			/*
			 * No output returned from the mapping program. An
			 * "output" element will not be added.
			 */
			return;
		}
		File outputFile = dump.dumpOutput(currentOutput);
		Node outputNode = currFailure.appendChild(report.createElement("output"));
		addFileInformation(outputNode, outputFile);
	}
	
	private void addFileInformation(Node parent, File file) throws MalformedURLException {
		assert report != null;
		parent.appendChild(report.createElement("filename")).appendChild(report.createTextNode(file.getName()));
		URL inputURL = file.toURI().toURL();
		parent.appendChild(report.createElement("url")).appendChild(report.createTextNode(inputURL.toString()));
	}
	
	protected String getConsoleLabel() {
		return "XmlOutputReport";
	}

}