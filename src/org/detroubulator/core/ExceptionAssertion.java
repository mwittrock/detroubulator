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

import org.detroubulator.mappingprograms.MappingOutput;

final class ExceptionAssertion implements Assertion {

	private String failureMessage;
	private String humanMessage;

	ExceptionAssertion() {
		humanMessage = null;
	}
	
	public void setFailureMessage(String message) {
		if (message == null) {
			throw new NullPointerException("Null parameter: message");
		}
		humanMessage = message;
	}

	public boolean evaluate(MappingOutput mo) {
		if (mo == null) {
			throw new NullPointerException("Null parameter: mo");
		}
		boolean result;
		if (mo.hasFailure()) {
			result = true;
			failureMessage = null;
		} else {
			result = false;
			if (humanMessage != null) {
				failureMessage = humanMessage;
			} else {
				failureMessage = "An exception was expected but did not occur";
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
