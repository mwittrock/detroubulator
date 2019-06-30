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

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.detroubulator.reports.ReportException;
import org.detroubulator.server.ServerException;
import org.detroubulator.util.ConfigurationException;
import org.detroubulator.util.Console;

public final class Launcher {
	
	private static Logger log = Logging.getLogger(Launcher.class);
	
	private File testFile;

	public static void main(String[] args) {
		Launcher l = new Launcher();
		l.start(args);
	}
	
	public void start(String[] args) {
		/*
		 * The processArguments method must be called before any logging
		 * is performed, because the debug switch might change the log level.
		 */
		processArguments(args);
		try {
			log.fine("Detroubulator started with arguments " + asList(args));
            printBanner();
            Console.p("Test file: %s", testFile.getPath());
            Console.p();
			TestSuite ts = TestSuiteFactory.createFromFile(testFile);
			ts.execute();
			log.fine("Detroubulator exiting");
		} catch (IOException ie) {
			terminate("I/O error while processing test file: " + ie.getMessage(), ie);
		} catch (TestFileException tfe) {
			terminate("Error in test file: " + tfe.getMessage(), tfe);
		} catch (ServerException se) {
			terminate("Server error: " + se.getMessage(), se);
		} catch (ReportException re) {
			terminate("Report error: " + re.getMessage(), re);
		} catch (ConfigurationException ce) {
			terminate("Configuration error: " + ce.getMessage(), ce);
		} catch (AssertionException ae) {
			terminate("Assertion evaluation error: " + ae.getMessage(), ae);
		}
	}

	private void processArguments(String[] args) {
		List<File> testFiles = new ArrayList<File>();
		for (String arg : args) {
			if (arg.equals("-h") || arg.equals("-help")) {
				printUsage();
				terminate();
			} else if (arg.equals("-d") || arg.equals("-debug")) {
				Logging.setDebugMode();
			} else if (arg.equals("-v") || arg.equals("-version")) {
				printBanner();
				terminate();
			} else {
				/*
				 * Not a recognized option. Assume it's the name of
				 * a test file.
				 */
				testFiles.add(new File(arg));
			}
		}
		// If no test file was provided, use the default test file name.
		if (testFiles.size() == 0) {
			testFiles.add(new File(Detroubulator.DEFAULT_TEST_FILE));
		}
		/*
		 * If more than one test file was provided, all but the first
		 * are ignored.
		 */
		assert testFiles.size() > 0;
		File tf = testFiles.get(0);
		// Make sure that the test file exists and is indeed a file.
		if (!tf.exists()) {
			terminate("Test file does not exist: " + tf.getPath());
		}
		if (!tf.isFile()) {
			terminate("Test file is not a file: " + tf.getPath());
		}
		testFile = tf;
	}
	
    private void printUsage() {
        String newLine = System.getProperty("line.separator");
        String indent = "   ";
        StringBuffer buff = new StringBuffer();
        buff.append("Usage: dtrb [options] [test_file]").append(newLine);
        buff.append("Options:").append(newLine);
        buff.append(indent).append("-h or -help         Show this usage message").append(newLine);
        buff.append(indent).append("-d or -debug        Set debug mode").append(newLine);
        buff.append(indent).append("-v or -version      Show version information").append(newLine);
        Console.p(buff.toString());
    }
    
    private void printBanner() {
    	Console.p("Detroubulator version %s, copyright %s", Detroubulator.VERSION, Detroubulator.COPYRIGHT);
    }

	private void terminate() {
		System.exit(1);
	}
	
	private void terminate(String message) {
		log.severe("Termination requested with message: " + message);
		Console.p(message);
		System.exit(1);
	}

	private void terminate(String message, Throwable cause) {
		log.severe("Termination requested with message: " + message);
		log.severe(Logging.stackTraceToString(cause));
		Console.p(message);
		System.exit(1);
	}

}