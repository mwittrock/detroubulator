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

import static java.util.Arrays.asList;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.detroubulator.core.TransformationParams;
import org.detroubulator.server.ServerException;
import org.detroubulator.server.XIServer;
import org.detroubulator.util.ConfigurationException;

public class TestMessageMapping extends TestCase {
	
	private Map<String, List<String>> emptyConfigMap;
	private Map<String, List<String>> correctConfigMap;
	private Map<String, List<String>> badConfigMap;
	private XIServer mockServer;
	private TransformationParams params;
	private MappingInput input;
	
	public void setUp() {
		// Create an empty configuration Map
		Map<String, List<String>> empty = new HashMap<String, List<String>>(); 
		emptyConfigMap = Collections.unmodifiableMap(empty);
		// Create a configuration Map containing the mandatory parameters
		Map<String, List<String>> correct = new HashMap<String, List<String>>(); 
		correct.put("name", asList("test value for parameter name"));
		correct.put("ns", asList("test value for parameter ns"));
		correct.put("swcv.id", asList("test value for parameter swcv.id"));
		correctConfigMap = Collections.unmodifiableMap(correct);
		// Create a configuration Map containing unrecognized parameters
		Map<String, List<String>> bad = new HashMap<String, List<String>>(); 
		bad.put("bad_param", asList("test value for parameter bad_param"));
		badConfigMap = Collections.unmodifiableMap(bad);
		// Create a mock server.
		mockServer = createMock(XIServer.class);
		// Set up parameters to the execute method.
		params = new TransformationParams();
		byte[] inputData = new byte[10]; // All elements will be initialized to 0
		input = new MappingInput(new ByteArrayInputStream(inputData));		
	}

	public void testNotConfiguredAfterConstruction() {
		MessageMapping mm = new MessageMapping();
		assertFalse(mm.isConfigured());
	}

	public void testConfigureFailsOnNullConfigurationMap() {
		MessageMapping mm = new MessageMapping();
		try {
			mm.configure(null);
			fail();
		} catch (NullPointerException npe) {
			assertTrue(true);
		} catch (ConfigurationException ce) {
			fail();
		}
	}
	
	public void testConfigureFailsOnEmptyConfigurationMap() {
		MessageMapping mm = new MessageMapping();
		try {
			mm.configure(emptyConfigMap);
			fail();
		} catch (ConfigurationException ce) {
			assertTrue(true);
		}
	}

	public void testConfigureFailsOnBadConfigurationMap() {
		MessageMapping mm = new MessageMapping();
		try {
			mm.configure(badConfigMap);
			fail();
		} catch (ConfigurationException ce) {
			assertTrue(true);
		}
	}

	public void testIsConfiguredOnCorrectConfigurationMap() {
		MessageMapping mm = new MessageMapping();
		try {
			mm.configure(correctConfigMap);
		} catch (ConfigurationException ce) {
			fail();
		}
		assertTrue(mm.isConfigured());
	}

	public void testCannotExecuteAfterConstruction() {
		MessageMapping mm = new MessageMapping();
		assertFalse(mm.canExecute());
	}

	public void testCannotExecuteAfterConfigurationButNoServerSet() {
		MessageMapping mm = new MessageMapping();
		try {
			mm.configure(correctConfigMap);
		} catch (ConfigurationException ce) {
			fail();
		}
		assertFalse(mm.canExecute());
	}

	public void testCannotExecuteAfterServerSetButNoConfiguration() {
		MessageMapping mm = new MessageMapping();
		mm.setServer(mockServer);
		assertFalse(mm.canExecute());
	}

	public void testCanExecuteWhenServerIsSet() {
		MessageMapping mm = new MessageMapping();
		try {
			mm.configure(correctConfigMap);
		} catch (ConfigurationException ce) {
			fail();
		}
		/*
		 * The state of the mock server doesn't really
		 * matter in this test, but let's reset it anyway.
		 */
		reset(mockServer);
		mm.setServer(mockServer);
		assertTrue(mm.canExecute());
	}

	public void testFailedExecuteWhenNoServerSet() {
		MessageMapping mm = new MessageMapping();
		try {
			mm.configure(correctConfigMap);
		} catch (ConfigurationException ce) {
			fail();		
		}
		try {
			mm.execute(params, input);
			fail();
		} catch (ServerException se) {
			fail();
		} catch (IllegalStateException ise) {
			assertTrue(true);
		}
	}

	public void testServerInteractionWithNormalReturn() throws ServerException {
		/*
		 * Why "throws ServerException"? It allows us to call the 
		 * executeMessageMapping on the mock server object without
		 * a try-catch block.
		 */
		MessageMapping mm = new MessageMapping();
		try {
			mm.configure(correctConfigMap);
		} catch (ConfigurationException ce) {
			fail();
		}
		/*
		 * Record the expected behaviour on the mock server. The call
		 * to executeMessageMapping will return a MappingOutput object
		 * wrapping a MappingFailure. This is the easiest MappingOutput
		 * object to set up and we don't care about the return value in 
		 * this test anyway.
		 */
		MappingOutput output = new MappingOutput(new MappingFailure("Mapping program failed"));
		reset(mockServer);
		expect(mockServer.executeMessageMapping(mm, input, params)).andReturn(output);
		replay(mockServer);
		/*
		 * Pass the mock server to the MessageMapping instance and
		 * execute the mapping program.
		 */
		mm.setServer(mockServer);
		try {
			mm.execute(params, input);
		} catch (ServerException e) {
			fail();
		}
		verify(mockServer);
	}

	public void testServerInteractionWithException() throws ServerException {
		/*
		 * Why "throws ServerException"? It allows us to call the 
		 * executeMessageMapping on the mock server object without
		 * a try-catch block.
		 */
		MessageMapping mm = new MessageMapping();
		try {
			mm.configure(correctConfigMap);
		} catch (ConfigurationException ce) {
			fail();
		}
		/*
		 * Record the expected behaviour on the mock server. The call
		 * to executeMessageMapping will throw a ServerException.
		 */
		reset(mockServer);
		expect(mockServer.executeMessageMapping(mm, input, params)).andThrow(new ServerException("Connection lost"));
		replay(mockServer);
		/*
		 * Pass the mock server to the MessageMapping instance and
		 * execute the mapping program.
		 */
		mm.setServer(mockServer);
		try {
			mm.execute(params, input);
			fail();
		} catch (ServerException e) {
			// Expected behaviour.
		}
		verify(mockServer);
	}
	
}