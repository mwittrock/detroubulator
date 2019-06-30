/*
 * Copyright 2006 AppliCon A/S
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

import junit.framework.TestCase;

import org.detroubulator.core.ExceptionAssertion;
import org.detroubulator.mappingprograms.MappingFailure;
import org.detroubulator.mappingprograms.MappingOutput;

public final class TestExceptionAssertion extends TestCase {

	private ExceptionAssertion assertion;
	private MappingOutput resultWithException;
	private MappingOutput resultWithoutException;
	
	public void setUp() {
		assertion = new ExceptionAssertion();
		resultWithException = new MappingOutput(new MappingFailure("Mapping program failed"));
		byte[] document = new byte[10]; // All elements will be initialized to 0.
		resultWithoutException = new MappingOutput(document);
	}
	
	public void testEvaluteWithException() {
		assertTrue(assertion.evaluate(resultWithException));
	}
	
	public void testEvaluteWithoutException() {
		assertFalse(assertion.evaluate(resultWithoutException));
	}
	
	public void testNoDetailMessageAfterConstruction() {
		ExceptionAssertion ea = new ExceptionAssertion();
		try {
			ea.getFailureMessage();
			fail();
		} catch (IllegalStateException e) {
			assertTrue(true);
		}
	}

	public void testNoDetailMessageAfterEvaluateTrue() {
		ExceptionAssertion ea = new ExceptionAssertion();
		ea.evaluate(resultWithException);
		try {
			ea.getFailureMessage();
			fail();
		} catch (IllegalStateException e) {
			assertTrue(true);
		}
	}

	public void testDetailMessageAfterEvaluateFalse() {
		ExceptionAssertion ea = new ExceptionAssertion();
		ea.evaluate(resultWithoutException);
		assertNotNull(ea.getFailureMessage());
	}

	public void testDetailMessageAfterEvaluateTrueThenFalse() {
		ExceptionAssertion ea = new ExceptionAssertion();
		ea.evaluate(resultWithException);
		ea.evaluate(resultWithoutException);
		assertNotNull(ea.getFailureMessage());
	}
	
	public void testNoDetailMessageAfterEvaluateFalseThenTrue() {
		ExceptionAssertion ea = new ExceptionAssertion();
		ea.evaluate(resultWithoutException);
		ea.evaluate(resultWithException);
		try {
			ea.getFailureMessage();
			fail();
		} catch (IllegalStateException e) {
			assertTrue(true);
		}
	}
	
}