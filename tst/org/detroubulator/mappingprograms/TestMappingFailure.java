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

import junit.framework.TestCase;

public class TestMappingFailure extends TestCase {

	private MappingFailure fail1;
	private MappingFailure fail2;
	private MappingFailure fail3;
	
	public void setUp() {
		fail1 = new MappingFailure("Message");
		fail2 = new MappingFailure("Another message");
		fail3 = new MappingFailure("Message");
	}
	
	public void testEquality() {
		assertTrue(fail1.equals(fail3));
		assertTrue(fail3.equals(fail1));
	}
	
	public void testInequality() {
		assertFalse(fail1.equals(fail2));
		assertFalse(fail2.equals(fail1));
	}

	public void testHashCodeEquality() {
		assertTrue(fail1.hashCode() == fail3.hashCode());
	}

}