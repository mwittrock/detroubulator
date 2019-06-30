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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.detroubulator.mappingprograms.MappingInput;
import org.detroubulator.mappingprograms.MappingOutput;
import org.detroubulator.mappingprograms.MappingProgram;
import org.detroubulator.reports.ReportException;
import org.detroubulator.reports.TestReport;
import org.detroubulator.server.ServerException;

final class TestSuite {

	private MappingProgram mp;
	private List<TestReport> reports;
	private List<TestCase> testCases;

	public TestSuite(MappingProgram mp, List<TestReport> reports, List<TestCase> testCases) {
		assert mp != null;
		assert reports != null;
		assert testCases != null;
		assert reports.size() > 0;
		assert testCases.size() > 0;
		this.mp = mp;
		// Defensively copy TestReports to a new List.
		this.reports = new ArrayList<TestReport>();
		for (TestReport tr : reports) {
			this.reports.add(tr);
		}
		// Defensively  copy TestCases to a new List.
		this.testCases = new ArrayList<TestCase>();
		for (TestCase tc : testCases) {
			this.testCases.add(tc);
		}
	}

	public void execute() throws ServerException, ReportException, AssertionException {
		fireStartTesting(mp);
		// Execute each testcase
		for (TestCase tc : testCases) {
			// Transform the input document
			File inputDoc = tc.getInputDoc();
			TransformationParams params = tc.getParameters();
			MappingInput input = new MappingInput(inputDoc);
			MappingOutput output = mp.execute(params, input);
			fireStartTestCase(tc, output);
			// Evaluate each assertion against the result of the transformation
			for (Assertion a : tc.getAssertions()) {
				boolean passed = a.evaluate(output);
				if (passed) {
					fireAssertionPassed(a);
				} else {
					fireAssertionFailed(a);
				}
			}
			fireEndTestCase();
		}
		fireEndTesting(); 
	}

	private void fireStartTesting(MappingProgram program) throws ReportException {
		for (TestReport tr : reports) {
			tr.startTesting(program);
		}
	}

	private void fireStartTestCase(TestCase tc, MappingOutput mo) throws ReportException {
		for (TestReport tr : reports) {
			tr.startTestCase(tc, mo);
		}
	}

	private void fireAssertionFailed(Assertion failed) throws ReportException {
		for (TestReport tr : reports) {
			tr.assertionFailed(failed);
		}
	}

	private void fireAssertionPassed(Assertion passed) throws ReportException {
		for (TestReport tr : reports) {
			tr.assertionPassed(passed);
		}
	}

	private void fireEndTestCase() throws ReportException {
		for (TestReport tr : reports) {
			tr.endTestCase();
		}
	}

	private void fireEndTesting() throws ReportException {
		for (TestReport tr : reports) {
			tr.endTesting();
		}
	}

}