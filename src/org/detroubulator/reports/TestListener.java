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

import org.detroubulator.core.Assertion;
import org.detroubulator.core.TestCase;
import org.detroubulator.mappingprograms.MappingOutput;
import org.detroubulator.mappingprograms.MappingProgram;

interface TestListener {

	public void startTesting(MappingProgram program) throws ReportException;

	public void startTestCase(TestCase tc, MappingOutput mo) throws ReportException;

	public void assertionFailed(Assertion failed) throws ReportException;

	public void assertionPassed(Assertion passed) throws ReportException;

	public void endTestCase() throws ReportException;

	public void endTesting() throws ReportException;

}