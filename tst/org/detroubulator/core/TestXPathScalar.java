package org.detroubulator.core;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;

import junit.framework.TestCase;

public class TestXPathScalar extends TestCase {

	public void testConstructorWithTwoNullParams() {
		try {
			new XPathScalar(null, null);
			fail();
		} catch (NullPointerException e) {
			assertTrue(true);
		}
	}
	
	public void testConstructorWithOneNullParam() {
		try {
			new XPathScalar(XPathConstants.STRING, null);
			fail();
		} catch (NullPointerException e) {
			assertTrue(true);
		}		
	}
	
	public void testConstructorWithAnotherNullParam() {
		try {
			new XPathScalar(null, "testing 1-2-3");
			fail();
		} catch (NullPointerException e) {
			assertTrue(true);
		}		
	}
	
	public void testConstructorWithIllegalType() {
		QName type = XPathConstants.NODE;
		Object value = new Object();
		try {
			new XPathScalar(type, value);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}
	
	public void testConstructorWithIllegalValueClass() {
		QName type = XPathConstants.STRING;
		Object value = new Object();
		try {
			new XPathScalar(type, value);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}
	
	public void testConstructorWithCorrectParams() {
		QName type = XPathConstants.STRING;
		Object value = "testing 1-2-3";
		try {
			new XPathScalar(type, value);
			assertTrue(true);
		} catch (Throwable t) {
			fail();
		}
	}
	
	public void testNumericEquality() {
		XPathScalar xps1 = new XPathScalar(XPathConstants.NUMBER, new Double(5));
		XPathScalar xps2 = new XPathScalar(XPathConstants.NUMBER, new Double(5));
		assertTrue(xps1.equals(xps2));
	}
	
	public void testNumericInequality() {
		XPathScalar xps1 = new XPathScalar(XPathConstants.NUMBER, new Double(5));
		XPathScalar xps2 = new XPathScalar(XPathConstants.NUMBER, new Double(10));
		assertFalse(xps1.equals(xps2));
	}
	
	public void testStringEquality() {
		XPathScalar xps1 = new XPathScalar(XPathConstants.STRING, new String("abc"));
		XPathScalar xps2 = new XPathScalar(XPathConstants.STRING, new String("abc"));
		assertTrue(xps1.equals(xps2));
	}

	public void testStringInequality() {
		XPathScalar xps1 = new XPathScalar(XPathConstants.STRING, new String("abc"));
		XPathScalar xps2 = new XPathScalar(XPathConstants.STRING, new String("def"));
		assertFalse(xps1.equals(xps2));
	}
	
	public void testBooleanEquality() {
		XPathScalar xps1 = new XPathScalar(XPathConstants.BOOLEAN, new Boolean(true));
		XPathScalar xps2 = new XPathScalar(XPathConstants.BOOLEAN, new Boolean(true));
		assertTrue(xps1.equals(xps2));
	}

	public void testBooleanInequality() {
		XPathScalar xps1 = new XPathScalar(XPathConstants.BOOLEAN, new Boolean(true));
		XPathScalar xps2 = new XPathScalar(XPathConstants.BOOLEAN, new Boolean(false));
		assertFalse(xps1.equals(xps2));
	}
	
	public void testBooleanNumericInequality() {
		XPathScalar xps1 = new XPathScalar(XPathConstants.BOOLEAN, new Boolean(true));
		XPathScalar xps2 = new XPathScalar(XPathConstants.NUMBER, new Double(1));
		assertFalse(xps1.equals(xps2));		
	}

	public void testStringNumericInequality() {
		XPathScalar xps1 = new XPathScalar(XPathConstants.STRING, new String("5"));
		XPathScalar xps2 = new XPathScalar(XPathConstants.NUMBER, new Double(5));
		assertFalse(xps1.equals(xps2));
	}
	
	public void testStringBooleanInequality() {
		XPathScalar xps1 = new XPathScalar(XPathConstants.STRING, new String("true"));
		XPathScalar xps2 = new XPathScalar(XPathConstants.BOOLEAN, new Boolean(true));
		assertFalse(xps1.equals(xps2));
	}
	
	public void testValueOfNumericWithSeparator() {
		XPathScalar xps1 = new XPathScalar(XPathConstants.NUMBER, Double.valueOf(5));
		XPathScalar xps2 = XPathScalar.valueOf("num:5.0");
		assertTrue(xps1.equals(xps2));
	}

	public void testValueOfNumericWithoutSeparator() {
		XPathScalar xps1 = new XPathScalar(XPathConstants.NUMBER, Double.valueOf(5));
		XPathScalar xps2 = XPathScalar.valueOf("num:5");
		assertTrue(xps1.equals(xps2));
	}
	
	public void testValueOfNumericInvalid() {
		try {
			XPathScalar.valueOf("num:abc");
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	public void testValueOfNumericNoNumber() {
		try {
			XPathScalar.valueOf("num:");
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}
	
	public void testValueOfBooleanTrue() {
		XPathScalar xps1 = new XPathScalar(XPathConstants.BOOLEAN, Boolean.TRUE);
		XPathScalar xps2 = XPathScalar.valueOf("bool:true");
		assertTrue(xps1.equals(xps2));
	}
	
	public void testValueOfBooleanFalse() {
		XPathScalar xps1 = new XPathScalar(XPathConstants.BOOLEAN, Boolean.FALSE);
		XPathScalar xps2 = XPathScalar.valueOf("bool:false");
		assertTrue(xps1.equals(xps2));
	}
	
	public void testValueOfBooleanInvalid() {
		try {
			XPathScalar.valueOf("bool:xyz");
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}	

	public void testValueOfBooleanNoBool() {
		try {
			XPathScalar.valueOf("bool:");
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}
	
	public void testValueOfStringNonempty() {
		XPathScalar xps1 = new XPathScalar(XPathConstants.STRING, "testing 1-2-3");
		XPathScalar xps2 = XPathScalar.valueOf("str:testing 1-2-3");
		assertTrue(xps1.equals(xps2));	
	}

	public void testValueOfStringEmpty() {
		XPathScalar xps1 = new XPathScalar(XPathConstants.STRING, "");
		XPathScalar xps2 = XPathScalar.valueOf("str:");
		assertTrue(xps1.equals(xps2));	
	}
	
	public void testValueOfStringContainingPrefix() {
		XPathScalar xps1 = new XPathScalar(XPathConstants.STRING, "num:5");
		XPathScalar xps2 = XPathScalar.valueOf("str:num:5");
		assertTrue(xps1.equals(xps2));	
	}
	
	public void testValueOfDefaultNonempty() {
		XPathScalar xps1 = new XPathScalar(XPathConstants.STRING, "testing 1-2-3");
		XPathScalar xps2 = XPathScalar.valueOf("testing 1-2-3");
		assertTrue(xps1.equals(xps2));	
	}
	
	public void testValueOfDefaultEmpty() {
		XPathScalar xps1 = new XPathScalar(XPathConstants.STRING, "");
		XPathScalar xps2 = XPathScalar.valueOf("");
		assertTrue(xps1.equals(xps2));	
	}

}
