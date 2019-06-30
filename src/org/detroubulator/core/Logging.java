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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class Logging {
	
	private static final String LOG_FILE_NAME = "detroubulator.log";
	private static final String DETROUBULATOR_ROOT_LOGGER_NAME = "org.detroubulator";
	private static final Level DEFAULT_LOG_LEVEL = Level.WARNING;
	private static final Level DEBUG_LOG_LEVEL = Level.FINER;
	
	public Logging() {
		/*
		 * If a class name is stored in the system property
		 * java.util.logging.config.class, the LogManager will
		 * create an instance of the class. Logging configuration
		 * is then performed in this class' no-arg constructor.
		 */
		initialize();
	}

	private static void initialize() {
		/*
		 * We might want to store the following configuration in a
		 * properties file and call LogManager.readConfiguration to
		 * load it. For now, however, we'll configure logging in
		 * code.
		 */
		try {
			Logger globalLog = Logger.getLogger(DETROUBULATOR_ROOT_LOGGER_NAME);
			globalLog.setLevel(DEFAULT_LOG_LEVEL); 
			Handler fh = new FileHandler(LOG_FILE_NAME, false); // Do not append to an existing file
			fh.setFormatter(new SimpleFormatter());
			globalLog.addHandler(fh);
			globalLog.setUseParentHandlers(false); // Don't pass LogRecords to the parent logger
		} catch (IOException e) {
			/*
			 * The log file could not be created. In order to log this
			 * fact, a log message is passed to the root logger of the
			 * logging framework. 
			 */
			Logger root = Logger.getLogger("");
			root.warning("Could not create Detroubulator log file");
			root.warning(stackTraceToString(e));
		}
	}
	
	public static String stackTraceToString(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	public static Logger getLogger(Class cl) {
		return Logger.getLogger(cl.getName());
	}
	
	public static void setDebugMode() {
		Logger.getLogger(DETROUBULATOR_ROOT_LOGGER_NAME).setLevel(DEBUG_LOG_LEVEL);
	}
	
}