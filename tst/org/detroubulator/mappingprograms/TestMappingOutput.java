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

package org.detroubulator.mappingprograms;

import org.detroubulator.mappingprograms.MappingFailure;
import org.detroubulator.mappingprograms.MappingOutput;

import junit.framework.TestCase;

public class TestMappingOutput extends TestCase {

	private byte[] payload;
	private MappingFailure failure;
	private MappingOutput outputWithFailure;
	private MappingOutput outputWithPayload;
	
	public void setUp() {
		payload = new byte[10]; // Every element will be initialized to zero.
		failure = new MappingFailure("Mapping program failed");
		outputWithPayload = new MappingOutput(payload);
		outputWithFailure = new MappingOutput(failure);
	}
	
	public void testHasPayloadWithNoFailure() {
		assertTrue(outputWithPayload.hasPayload());
		assertFalse(outputWithPayload.hasFailure());
	}
	
	public void testHasFailureWithNoPayload() {
		assertFalse(outputWithFailure.hasPayload());
		assertTrue(outputWithFailure.hasFailure());
	}

	public void testCantAccessMissingPayload() {
		try {
			outputWithFailure.getPayload();
			fail();
		} catch (IllegalStateException e) {
			assertTrue(true);
		}
	}

	public void testCantAccessMissingFailure() {
		try {
			outputWithPayload.getFailure();
			fail();
		} catch (IllegalStateException e) {
			assertTrue(true);
		}
	}

	public void testConstructorFailsOnNullMappingFailure() {
		try {
			MappingFailure mf = null;
			new MappingOutput(mf);
			fail();
		} catch (NullPointerException e) {
			assertTrue(true);
		}
	}

	public void testConstructorFailsOnNullPayload() {
		try {
			byte[] pl = null;
			new MappingOutput(pl);
			fail();
		} catch (NullPointerException e) {
			assertTrue(true);
		}
	}
	
	public void testEqualityWithWrappedFailures() {
		MappingOutput mo1 = new MappingOutput(new MappingFailure("Mapping failed"));
		MappingOutput mo2 = new MappingOutput(new MappingFailure("Mapping failed"));
		assertTrue(mo1.equals(mo2));
		assertTrue(mo2.equals(mo1));
	}

	public void testInequalityWithWrappedFailures() {
		MappingOutput mo1 = new MappingOutput(new MappingFailure("Mapping failed"));
		MappingOutput mo2 = new MappingOutput(new MappingFailure("Mapping failed again"));
		assertFalse(mo1.equals(mo2));
		assertFalse(mo2.equals(mo1));
	}
	
	public void testEqualityWithWrappedPayloads() {
		byte[] pl1 = {(byte)1, (byte)2, (byte)3};
		byte[] pl2 = {(byte)1, (byte)2, (byte)3};
		MappingOutput mo1 = new MappingOutput(pl1);
		MappingOutput mo2 = new MappingOutput(pl2);
		assertTrue(mo1.equals(mo2));
		assertTrue(mo2.equals(mo1));
	}

	public void testInequalityWithWrappedPayloads() {
		byte[] pl1 = {(byte)1, (byte)2, (byte)3};
		byte[] pl2 = {(byte)3, (byte)2, (byte)1};
		MappingOutput mo1 = new MappingOutput(pl1);
		MappingOutput mo2 = new MappingOutput(pl2);
		assertFalse(mo1.equals(mo2));
		assertFalse(mo2.equals(mo1));
	}
	
	public void testInequalityWithWrappedFailureAndPayload() {
		byte[] pl1 = {(byte)1, (byte)2, (byte)3};
		MappingOutput mo1 = new MappingOutput(pl1);	
		MappingOutput mo2 = new MappingOutput(new MappingFailure("Mapping failed"));
		assertFalse(mo1.equals(mo2));
		assertFalse(mo2.equals(mo1));
	}

}