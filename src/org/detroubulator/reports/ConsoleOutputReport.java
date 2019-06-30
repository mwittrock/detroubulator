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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.detroubulator.core.Assertion;
import org.detroubulator.core.Logging;
import org.detroubulator.core.TestCase;
import org.detroubulator.mappingprograms.MappingOutput;
import org.detroubulator.mappingprograms.MappingProgram;
import org.detroubulator.util.ConfigurationException;
import org.detroubulator.util.Console;

final class ConsoleOutputReport extends StatsCollector implements TestReport {

    private static final String CONSOLE_LABEL = "ConsoleOutputReport";

	private static Logger log = Logging.getLogger(ConsoleOutputReport.class);

	private boolean configured;
	private TestCase currentTestCase;
	private MappingOutput currentOutput;
	private MappingProgram program;
	private DumpHandler dump;
	
	ConsoleOutputReport() {
		configured = false;
	}

	public boolean isConfigured() {
		return configured;
	}

	public void configure(Map<String, List<String>> params) throws ConfigurationException {
		if (params == null) {
			throw new NullPointerException("Null parameter: params");
		}
		if (!params.containsKey("dumpdir")) {
			throw new ConfigurationException("Mandatory parameter not present: dumpdir");
		}
		assert params.get("dumpdir").size() > 0;
		DumpHandlerFactory dhfact = DumpHandlerFactory.newInstance(); 
		try {
			dump = dhfact.newDumpHandler(new File(params.get("dumpdir").get(0)));
		} catch (IllegalArgumentException iae) {
			throw new ConfigurationException(iae.getMessage(), iae);
		}
		configured = true;
	}

	public void startTesting(MappingProgram program) throws ReportException {
		super.startTesting(program);
		this.program = program;
	}
	
	public void startTestCase(TestCase tc, MappingOutput output) throws ReportException {
		super.startTestCase(tc, output);
		currentTestCase = tc;
		currentOutput = output;
	}
	
	public void assertionFailed(Assertion failed) throws ReportException {
		super.assertionFailed(failed);
		Console.startSection(CONSOLE_LABEL);
		Console.p("AN ASSERTION HAS FAILED!");
		Console.p("Input document: %s", currentTestCase.getInputDoc().getPath());
		/*
		 * Dumping only makes sense if the mapping actually
		 * produced a document (as opposed to an exception).
		 */
		if (currentOutput.hasPayload()) {
			log.fine("Dumping MappingOutput: " + currentOutput.toString());
			File dumpFile;
			try {
				dumpFile = dump.dumpOutput(currentOutput);
			} catch (IOException e) {
				dumpFile = null;
			}
			Console.p("Output dumped to: %s", dumpFile == null ? "Dump failed" : dumpFile.getPath());
		}
		Console.p("Message: %s", failed.getFailureMessage());
		Console.endSection();
	}

	public void endTesting() throws ReportException {
		super.endTesting();
		Console.startSection(CONSOLE_LABEL);
		String execTime = formatExecutionTime(getExecutionTimeMillis());
		Console.p("Finished testing: %s", program.toString());
		Console.p("Test cases executed: %d", testCases);
		Console.p("Passed assertions: %d", passedAssertions);
		Console.p("Failed assertions: %d", failedAssertions);
		Console.p("Execution time: %s seconds", execTime);
		Console.endSection();
	}

}