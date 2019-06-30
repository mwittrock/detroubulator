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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Date;

import org.detroubulator.core.Assertion;
import org.detroubulator.core.TestCase;
import org.detroubulator.mappingprograms.MappingOutput;
import org.detroubulator.mappingprograms.MappingProgram;

class StatsCollector extends TestAdapter {

	protected int failedAssertions;
	protected int passedAssertions;
	protected int testCases;
	protected Date startedAt;
	protected Date finishedAt;

	StatsCollector() {
		failedAssertions = 0;
		passedAssertions = 0;
		testCases = 0;
	}

	public void startTesting(MappingProgram program) throws ReportException {
		startedAt = new Date();
	}
	
	public void startTestCase(TestCase tc, MappingOutput result) throws ReportException {
		testCases++;
	}
	
	public void assertionPassed(Assertion passed) throws ReportException {
		passedAssertions++;
	}
	
	public void assertionFailed(Assertion failed) throws ReportException {
		failedAssertions++;
	}

	public void endTesting() throws ReportException {
		finishedAt = new Date();
	}
	
	protected long getExecutionTimeMillis() {
		assert startedAt != null;
		assert finishedAt != null;
		return finishedAt.getTime() - startedAt.getTime();
	}

	protected String formatExecutionTime(long millis) {
		BigDecimal ms = new BigDecimal(BigInteger.valueOf(millis));
		BigDecimal s = ms.movePointLeft(3);
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		return nf.format(s);
	}

}