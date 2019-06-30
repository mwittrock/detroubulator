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

import org.detroubulator.core.TransformationParams;

public final class TestTransformationParams extends TestCase {
	
	public void testCopyConstructorMapsNotIdentical() {
		/*
		 * The Map contained in the new TransformationParams instance must be
		 * different from the map in the TransformationParams instance that it 
		 * is based on.
		 */
		TransformationParams base = new TransformationParams();
		TransformationParams params = new TransformationParams(base);
		assertNotSame(params.getParameterMap(), base.getParameterMap());
	}

	public void testCopyConstructorMapEquality() {
		/*
		 * The Map contained in the new TransformationParams instance must be
		 * equal to the map in the TransformationParams instance that it is 
		 * based on.
		 */
		TransformationParams base = new TransformationParams();
		TransformationParams params = new TransformationParams(base);
		assertEquals(params.getParameterMap(), base.getParameterMap());
	}
	
	public void testBadParameterName() {
		/*
		 * Adding a parameter which is not the name of a static final field 
		 * in the StreamTransformationConstans interface should throw an
		 * IllegalArgumentException.
		 */
		TransformationParams params = new TransformationParams();
		try {
			params.addParameter("this_will_never_be_the_name_of_a_static_StreamTransformationConstants_field", "the value is not important");
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	public void testMapIndependence() {
		TransformationParams base = new TransformationParams();
		TransformationParams params = new TransformationParams(base);
		params.addParameter("MESSAGE_ID", "1234567890");
		assertFalse(params.getParameterMap().equals(base.getParameterMap()));
	}

	public void testParameterMapNotEmptyAfterConstruction() {
		TransformationParams params = new TransformationParams();
		assertFalse(params.getParameterMap().isEmpty());
	}
	
	public void testObjectEquality() {
		TransformationParams base = new TransformationParams();
		TransformationParams params = new TransformationParams(base);
		assertTrue(base.equals(params));
		assertTrue(params.equals(base));
	}
	
	public void testObjectInequality() {
		TransformationParams base = new TransformationParams();
		TransformationParams params = new TransformationParams(base);
		params.addParameter("MESSAGE_ID", "1234567890");
		assertFalse(base.equals(params));
		assertFalse(params.equals(base));		
	}

}