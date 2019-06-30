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

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.detroubulator.mappingprograms.MappingOutput;
import org.xml.sax.InputSource;

final class ContentAssertion implements Assertion {
	
	private static final XPathScalar DEFAULT_EXPECTATION = new XPathScalar(XPathConstants.BOOLEAN, Boolean.TRUE);
	
	private XPathExpression xpath;
	private XPathScalar expectedVal;
	private String expression;
	private String failureMessage;
	private String humanMessage;
	
	ContentAssertion(String expression, NamespaceContext nsContext) throws XPathExpressionException {
		if (expression == null) {
			throw new NullPointerException("Null parameter: expression");
		}
		if (nsContext == null) {
			throw new NullPointerException("Null parameter: nsContext");
		}
		XPath xp = XPathFactory.newInstance().newXPath();
		xp.setNamespaceContext(nsContext);
		this.xpath = xp.compile(expression);
		this.expression = expression;
		this.expectedVal = DEFAULT_EXPECTATION;
	}

	public void setExpectedValue(XPathScalar expectedVal) {
		if (expectedVal == null) {
			throw new NullPointerException("Null parameter: expectedVal");
		}
		this.expectedVal = expectedVal;
	}
	
	public void setFailureMessage(String message) {
		if (message == null) {
			throw new NullPointerException("Null parameter: message");
		}
		humanMessage = message;
	}

	public boolean evaluate(MappingOutput mo) throws AssertionException {
		if (mo == null) {
			throw new NullPointerException("Null parameter: mo");
		}
		boolean result;
		if (mo.hasFailure()) {
			result = false;
			failureMessage = String.format("An unexpected mapping exception occured (%s)", mo.getFailure().getMessage());
		} else {
			XPathScalar actualVal;
			try {
				Object value = xpath.evaluate(new InputSource(mo.getPayload()), expectedVal.getType());
				actualVal = new XPathScalar(expectedVal.getType(), value);
			} catch (Exception e) {
				/*
				 * All exceptions are rethrown as AssertionException.
				 */
				throw new AssertionException("Error evaluating value of XPath expression", e);
			}
			if (expectedVal.equals(actualVal)) {
				result = true;
				failureMessage = null;
			} else {
				result = false;
				if (humanMessage != null) {
					failureMessage = humanMessage;
				} else {
					failureMessage = String.format("XPath expression %s evaluated to '%s', expected '%s'", expression, actualVal, expectedVal);
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
	
}