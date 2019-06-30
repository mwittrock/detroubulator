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

public class TestDumpHandlerFactory extends TestCase {
	
	public void testSingletonFactory() {
		DumpHandlerFactory fact1 = DumpHandlerFactory.newInstance();
		DumpHandlerFactory fact2 = DumpHandlerFactory.newInstance();
		assertSame(fact1, fact2);
	}
	
	public void testSameDirSameDumpHandler() {
		DumpHandlerFactory fact = DumpHandlerFactory.newInstance();
		File dir = new File("/my/dump/directory");
		DumpHandler dh1 = fact.newDumpHandler(dir);
		DumpHandler dh2 = fact.newDumpHandler(dir);
		assertSame(dh1, dh2);
	}
	
	public void testDifferentDirDifferentDumpHandler() {
		DumpHandlerFactory fact = DumpHandlerFactory.newInstance();
		File dir1 = new File("/my/dump/directory");
		File dir2 = new File("/my/other/dump/directory");
		DumpHandler dh1 = fact.newDumpHandler(dir1);
		DumpHandler dh2 = fact.newDumpHandler(dir2);
		assertNotSame(dh1, dh2);
		assertFalse(dh1.equals(dh2));
	}

}
