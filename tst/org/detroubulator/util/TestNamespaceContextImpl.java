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

package org.detroubulator.util;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import junit.framework.TestCase;

public class TestNamespaceContextImpl extends TestCase {
	
	private NamespaceContext empty;
	private NamespaceContext single;
	private NamespaceContext multi;
	private NamespaceContext multiDup;
	
	protected void setUp() {
		// Set up NamespaceContext empty.
		empty = new NamespaceContextImpl();
		// Set up NamespaceContext single.
		NamespaceContextImpl s = new NamespaceContextImpl();
		s.add("ns1", "http://www.detroubulator.org/namespace");
		single = s;
		// Set up NamespaceContext multi.
		NamespaceContextImpl m = new NamespaceContextImpl();
		m.add("ns1", "http://www.detroubulator.org/namespace");
		m.add("ns2", "http://www.detroubulator.org/othernamespace");
		multi = m;
		// Set up NamespaceContext multiDup.
		NamespaceContextImpl md = new NamespaceContextImpl();
		md.add("ns1", "http://www.detroubulator.org/namespace");
		md.add("ns2", "http://www.detroubulator.org/namespace");
		md.add("ns3", "http://www.detroubulator.org/othernamespace");
		multiDup = md;
	}
	
	/*
	 * Tests of the getNamespaceURI method.
	 */
	
	public void testGetNamespaceURIUnboundPrefixEmpty() {
		assertEquals(empty.getNamespaceURI("unbound"), XMLConstants.NULL_NS_URI);
	}

	public void testGetNamespaceURIUnboundPrefixSingle() {
		assertEquals(single.getNamespaceURI("unbound"), XMLConstants.NULL_NS_URI);
	}

	public void testGetNamespaceURIUnboundPrefixMulti() {
		assertEquals(multi.getNamespaceURI("unbound"), XMLConstants.NULL_NS_URI);
	}
	
	public void testGetNamespaceURIUnboundPrefixMultiDup() {
		assertEquals(multiDup.getNamespaceURI("unbound"), XMLConstants.NULL_NS_URI);
	}
	
	public void testGetNamespaceURIBoundPrefixSingle() {
		assertEquals(single.getNamespaceURI("ns1"), "http://www.detroubulator.org/namespace");
	}

	public void testGetNamespaceURIBoundPrefixMulti() {
		assertEquals(multi.getNamespaceURI("ns1"), "http://www.detroubulator.org/namespace");
		assertEquals(multi.getNamespaceURI("ns2"), "http://www.detroubulator.org/othernamespace");
	}

	public void testGetNamespaceURIBoundPrefixMultiDup() {
		assertEquals(multiDup.getNamespaceURI("ns1"), "http://www.detroubulator.org/namespace");
		assertEquals(multiDup.getNamespaceURI("ns2"), "http://www.detroubulator.org/namespace");
		assertEquals(multiDup.getNamespaceURI("ns3"), "http://www.detroubulator.org/othernamespace");
	}

	public void testGetNamespaceURIXMLNsPrefixEmpty() {
		assertEquals(empty.getNamespaceURI(XMLConstants.XML_NS_PREFIX), XMLConstants.XML_NS_URI);
	}

	public void testGetNamespaceURIXMLNsPrefixSingle() {
		assertEquals(single.getNamespaceURI(XMLConstants.XML_NS_PREFIX), XMLConstants.XML_NS_URI);
	}

	public void testGetNamespaceURIXMLNsPrefixMulti() {
		assertEquals(multi.getNamespaceURI(XMLConstants.XML_NS_PREFIX), XMLConstants.XML_NS_URI);
	}

	public void testGetNamespaceURIXMLNsPrefixMultiDup() {
		assertEquals(multiDup.getNamespaceURI(XMLConstants.XML_NS_PREFIX), XMLConstants.XML_NS_URI);
	}
	
	public void testGetNamespaceURIXMLNsAttributePrefixEmpty() {
		assertEquals(empty.getNamespaceURI(XMLConstants.XMLNS_ATTRIBUTE), XMLConstants.XMLNS_ATTRIBUTE_NS_URI);		
	}

	public void testGetNamespaceURIXMLNsAttributePrefixSingle() {
		assertEquals(single.getNamespaceURI(XMLConstants.XMLNS_ATTRIBUTE), XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
	}

	public void testGetNamespaceURIXMLNsAttributePrefixMulti() {
		assertEquals(multi.getNamespaceURI(XMLConstants.XMLNS_ATTRIBUTE), XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
	}
	
	public void testGetNamespaceURIXMLNsAttributePrefixMultiDup() {
		assertEquals(multiDup.getNamespaceURI(XMLConstants.XMLNS_ATTRIBUTE), XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
	}

	public void testGetNamespaceURINullPrefixEmpty() {
		try {
			empty.getNamespaceURI(null);
			fail("An exception should have been thrown");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	public void testGetNamespaceURINullPrefixNonEmpty() {
		/*
		 * We're not testing this for all three kinds of non-empty
		 * NamespaceContext objects.
		 */
		try {
			multiDup.getNamespaceURI(null);
			fail("An exception should have been thrown");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}
	
	/*
	 * Tests of the getPrefixes method.
	 */
	
	public void testGetPrefixesUnboundURIEmpty() {
		Iterator i = empty.getPrefixes("http://www.detroubulator.org/namespace");
		assertFalse(i.hasNext());
	}

	public void testGetPrefixesUnboundURINonEmpty() {
		/*
		 * We're not testing this for all three kinds of non-empty
		 * NamespaceContext objects.
		 */
		Iterator i = multiDup.getPrefixes("http://some.unbound/URI");
		assertFalse(i.hasNext());
	}

	public void testGetPrefixesXMLNsURIEmpty() {
		Iterator i = empty.getPrefixes(XMLConstants.XML_NS_URI);
		assertTrue(i.hasNext());
		String prefix = (String)i.next();
		assertEquals(prefix, XMLConstants.XML_NS_PREFIX);
		assertFalse(i.hasNext());
	}

	public void testGetPrefixesXMLNsURISingle() {
		Iterator i = single.getPrefixes(XMLConstants.XML_NS_URI);
		assertTrue(i.hasNext());
		String prefix = (String)i.next();
		assertEquals(prefix, XMLConstants.XML_NS_PREFIX);
		assertFalse(i.hasNext());
	}

	public void testGetPrefixesXMLNsURIMulti() {
		Iterator i = multi.getPrefixes(XMLConstants.XML_NS_URI);
		assertTrue(i.hasNext());
		String prefix = (String)i.next();
		assertEquals(prefix, XMLConstants.XML_NS_PREFIX);
		assertFalse(i.hasNext());
	}

	public void testGetPrefixesXMLNsURIMultiDup() {
		Iterator i = multiDup.getPrefixes(XMLConstants.XML_NS_URI);
		assertTrue(i.hasNext());
		String prefix = (String)i.next();
		assertEquals(prefix, XMLConstants.XML_NS_PREFIX);
		assertFalse(i.hasNext());
	}

	public void testGetPrefixesXMLNsAttributeNsURIEmpty() {
		Iterator i = empty.getPrefixes(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
		assertTrue(i.hasNext());
		String prefix = (String)i.next();
		assertEquals(prefix, XMLConstants.XMLNS_ATTRIBUTE);
		assertFalse(i.hasNext());
	}

	public void testGetPrefixesXMLNsAttributeNsURISingle() {
		Iterator i = single.getPrefixes(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
		assertTrue(i.hasNext());
		String prefix = (String)i.next();
		assertEquals(prefix, XMLConstants.XMLNS_ATTRIBUTE);
		assertFalse(i.hasNext());
	}

	public void testGetPrefixesXMLNsAttributeNsURIMulti() {
		Iterator i = multi.getPrefixes(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
		assertTrue(i.hasNext());
		String prefix = (String)i.next();
		assertEquals(prefix, XMLConstants.XMLNS_ATTRIBUTE);
		assertFalse(i.hasNext());
	}	
	
	public void testGetPrefixesXMLNsAttributeNsURIMultiDup() {
		Iterator i = multiDup.getPrefixes(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
		assertTrue(i.hasNext());
		String prefix = (String)i.next();
		assertEquals(prefix, XMLConstants.XMLNS_ATTRIBUTE);
		assertFalse(i.hasNext());
	}

	public void testGetPrefixesNullURIEmpty() {
		try {
			empty.getPrefixes(null);
			fail("An exception should have been thrown");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	public void testGetPrefixesNullURINonEmpty() {
		/*
		 * We're not testing this for all three kinds of non-empty
		 * NamespaceContext objects.
		 */
		try {
			multiDup.getPrefixes(null);
			fail("An exception should have been thrown");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	public void testGetPrefixesBoundURISingle() {
		Iterator i = single.getPrefixes("http://www.detroubulator.org/namespace");
		assertTrue(i.hasNext());
		String prefix = (String)i.next();
		assertEquals(prefix, "ns1");
		assertFalse(i.hasNext());
	}

	public void testGetPrefixesBoundURIMultiFirstURI() {
		Iterator i = multi.getPrefixes("http://www.detroubulator.org/namespace");
		assertTrue(i.hasNext());
		String prefix = (String)i.next();
		assertFalse(i.hasNext());
		assertEquals(prefix, "ns1"); 
	}

	public void testGetPrefixesBoundURIMultiSecondURI() {
		Iterator i = multi.getPrefixes("http://www.detroubulator.org/othernamespace");
		assertTrue(i.hasNext());
		String prefix = (String)i.next();
		assertFalse(i.hasNext());
		assertEquals(prefix, "ns2"); 
	}

	public void testGetPrefixesBoundURIMultiDupDuplicateURI() {
		Iterator i = multiDup.getPrefixes("http://www.detroubulator.org/namespace");
		assertTrue(i.hasNext());
		String prefix1 = (String)i.next();
		assertTrue(i.hasNext());
		String prefix2 = (String)i.next();
		assertFalse(i.hasNext());
		assertTrue((prefix1.equals("ns1") && prefix2.equals("ns2")) || 
				(prefix1.equals("ns2") && prefix2.equals("ns1")));
	}

	public void testGetPrefixesBoundURIMultiDupSecondURI() {
		Iterator i = multiDup.getPrefixes("http://www.detroubulator.org/othernamespace");
		assertTrue(i.hasNext());
		String prefix = (String)i.next();
		assertFalse(i.hasNext());
		assertEquals(prefix, "ns3"); 
	}

	/*
	 * Tests of the getPrefix method.
	 */
	
	public void testGetPrefixUnboundURIEmpty() {
		assertNull(empty.getPrefix("http://some.unbound/URI"));
	}
	
	public void testGetPrefixUnboundURINonEmpty() {
		/*
		 * We're not testing this for all three kinds of non-empty
		 * NamespaceContext objects.
		 */
		assertNull(multiDup.getPrefix("http://some.unbound/URI"));
	}
	
	public void testGetPrefixBoundURISingle() {
		String prefix = single.getPrefix("http://www.detroubulator.org/namespace");
		assertEquals(prefix, "ns1");
	}

	public void testGetPrefixBoundURIMultiFirstURI() {
		String prefix = multi.getPrefix("http://www.detroubulator.org/namespace");
		assertEquals(prefix, "ns1");
	}

	public void testGetPrefixBoundURIMultiSecondURI() {
		String prefix = multi.getPrefix("http://www.detroubulator.org/othernamespace");
		assertEquals(prefix, "ns2");
	}
	
	public void testGetPrefixBoundURIMultiDupDuplicateURI() {
		String prefix = multiDup.getPrefix("http://www.detroubulator.org/namespace");
		assertTrue(prefix.equals("ns1") || prefix.equals("ns2"));
	}

	public void testGetPrefixBoundURIMultiDupSecondURI() {
		String prefix = multiDup.getPrefix("http://www.detroubulator.org/othernamespace");
		assertEquals(prefix, "ns3");
	}
	
	public void testGetPrefixXMLNsURIEmpty() {
		String prefix = empty.getPrefix(XMLConstants.XML_NS_URI);
		assertEquals(prefix, XMLConstants.XML_NS_PREFIX);
	}
	
	public void testGetPrefixXMLNsURISingle() {
		String prefix = single.getPrefix(XMLConstants.XML_NS_URI);
		assertEquals(prefix, XMLConstants.XML_NS_PREFIX);
	}
	
	public void testGetPrefixXMLNsURIMulti() {
		String prefix = multi.getPrefix(XMLConstants.XML_NS_URI);
		assertEquals(prefix, XMLConstants.XML_NS_PREFIX);
	}
	
	public void testGetPrefixXMLNsURIMultiDup() {
		String prefix = multiDup.getPrefix(XMLConstants.XML_NS_URI);
		assertEquals(prefix, XMLConstants.XML_NS_PREFIX);
	}

	public void testGetPrefixXMLNsAttributeNsURIEmpty() {
		String prefix = empty.getPrefix(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
		assertEquals(prefix, XMLConstants.XMLNS_ATTRIBUTE);
	}

	public void testGetPrefixXMLNsAttributeNsURISingle() {
		String prefix = single.getPrefix(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
		assertEquals(prefix, XMLConstants.XMLNS_ATTRIBUTE);
	}

	public void testGetPrefixXMLNsAttributeNsURIMulti() {
		String prefix = multi.getPrefix(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
		assertEquals(prefix, XMLConstants.XMLNS_ATTRIBUTE);
	}	
	
	public void testGetPrefixXMLNsAttributeNsURIMultiDup() {
		String prefix = multiDup.getPrefix(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
		assertEquals(prefix, XMLConstants.XMLNS_ATTRIBUTE);
	}

	public void testGetPrefixNullURIEmpty() {
		try {
			empty.getPrefix(null);
			fail("An exception should have been thrown");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}
	
	public void testGetPrefixNullURINonEmpty() {
		/*
		 * We're not testing this for all three kinds of non-empty
		 * NamespaceContext objects.
		 */
		try {
			multiDup.getPrefix(null);
			fail("An exception should have been thrown");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}	

}