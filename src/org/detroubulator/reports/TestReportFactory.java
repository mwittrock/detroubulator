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

import java.util.List;
import java.util.Map;

import org.detroubulator.util.ConfigurationException;

public final class TestReportFactory {
	
	private TestReportFactory() {
		// Not supposed to be instantiated.
	}
	
	public static TestReport newInstance(TestReportType type, Map<String, List<String>> config) throws ConfigurationException {
		TestReport tr;
		switch (type) {
		case audio:
			tr = new AudioPlaybackReport();
			break;
		case console:
			tr = new ConsoleOutputReport();
			break;
		case xml:
			tr = new XmlOutputReport();
			break;
		case html:
			tr = new HtmlOutputReport();
			break;
		default:
			throw new AssertionError("Unexpected test report type: " + type);
		}
		tr.configure(config);
		assert tr.isConfigured();
		return tr;
	}

	public static TestReport newInstance(Class cl, Map<String, List<String>> config) throws InstantiationException, IllegalAccessException, ConfigurationException, IllegalArgumentException  {
		Object o = cl.newInstance();
		if (!(o instanceof TestReport)) {
			throw new IllegalArgumentException("Class " + cl.getName() + " does not implement the TestReport interface");
		}
		TestReport tr = (TestReport)o;
		tr.configure(config);
		assert tr.isConfigured();
		return tr;
	}

}