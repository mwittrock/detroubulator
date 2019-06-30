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

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.detroubulator.util.ConfigurationException;

final class HtmlOutputReport extends XmlOutputReport {
	
	private static final String STYLESHEET_RESOURCE_PATH = "resources/testreport_v110.xslt";
	
	public void configure(Map<String, List<String>> params) throws ConfigurationException {
		super.configure(params);
		InputStream xslt = getClass().getClassLoader().getResourceAsStream(STYLESHEET_RESOURCE_PATH);
		TransformerFactory tf = TransformerFactory.newInstance();
		try {
			stylesheet = tf.newTransformer(new StreamSource(xslt));
		} catch (TransformerConfigurationException e) {
			throw new ConfigurationException("Error creating stylesheet transformer: " + e.getMessage(), e);
		}
	}

	protected String getConsoleLabel() {
		return "HtmlOutputReport";
	}
	
}