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

package org.detroubulator.reports;

import java.io.File;

import junit.framework.TestCase;

public class TestDumpHandler extends TestCase {

	private DumpHandler dh1;
	private DumpHandler dh2;
	private DumpHandler dh3;
	
	public void setUp() {
		dh1 = new DumpHandler(new File("/my/dump/dir"));
		dh2 = new DumpHandler(new File("/my/other/dump/dir"));
		dh3 = new DumpHandler(new File("/my/dump/dir"));
	}
	
	public void testEquality() {
		assertTrue(dh1.equals(dh3));
		assertTrue(dh3.equals(dh1));
		assertTrue(dh1.equals(dh1));
	}
	
	public void testInequality() {
		assertFalse(dh1.equals(dh2));
		assertFalse(dh2.equals(dh1));
	}
	
	public void testHashCodeEquality() {
		assertEquals(dh1.hashCode(), dh3.hashCode());
	}

}