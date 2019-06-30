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

package org.detroubulator.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public final class NamespaceContextImpl implements NamespaceContext {

	private Map<String, List<String>> nsMap;
	private Map<String, String> prefixMap;
	
	public NamespaceContextImpl() {
		nsMap = new HashMap<String, List<String>>();
		prefixMap = new HashMap<String, String>();
		// Add two special cases
		add(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
		add(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
	}
	
	public void add(String prefix, String nsURI) {
		if (prefix == null) {
			throw new NullPointerException("Null parameter: prefix");
		}
		if (nsURI== null) {
			throw new NullPointerException("Null parameter: nsURI");
		}
		List<String> prefixes;
		if (nsMap.containsKey(nsURI)) {
			prefixes = nsMap.get(nsURI);
		} else {
			prefixes = new ArrayList<String>();
			nsMap.put(nsURI, prefixes);
		}
		if (!prefixes.contains(prefix)) {
			prefixes.add(prefix);
		}
		prefixMap.put(prefix, nsURI);
	}
	
	public String getNamespaceURI(String prefix) {
		/*
		 * The reason we're not throwing a NullPointerException when prefix
		 * is null is that the NamespaceContext interface specifies that
		 * an IllegalArgumentException must be thrown instead.
		 */
		if (prefix == null) {
			throw new IllegalArgumentException("Null parameter: prefix");
		}
		String nsURI;
		if (prefixMap.containsKey(prefix)) {
			nsURI = prefixMap.get(prefix);
		} else {
			nsURI = XMLConstants.NULL_NS_URI;
		}
		return nsURI;
	}

	public String getPrefix(String nsURI) {
		/*
		 * The reason we're not throwing a NullPointerException when nsURI
		 * is null is that the NamespaceContext interface specifies that
		 * an IllegalArgumentException must be thrown instead.
		 */
		if (nsURI == null) {
			throw new IllegalArgumentException("Null parameter: nsURI");
		}
		String prefix;
		if (nsMap.containsKey(nsURI)) {
			prefix = (String)getPrefixes(nsURI).next();
		} else {
			prefix = null;
		}
		return prefix;
	}

	public Iterator getPrefixes(String nsURI) {
		/*
		 * The reason we're not throwing a NullPointerException when nsURI
		 * is null is that the NamespaceContext interface specifies that
		 * an IllegalArgumentException must be thrown instead.
		 */
		if (nsURI == null) {
			throw new IllegalArgumentException("Null parameter: nsURI");
		}
		Iterator prefixes;
		if (nsMap.containsKey(nsURI)) {
			prefixes = nsMap.get(nsURI).iterator();
		} else {
			// Return an empty Iterator object
			prefixes = new Iterator() {
				public boolean hasNext() {
					return false;
				}
				public Object next() {
					throw new NoSuchElementException("Iterator is empty");
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
		return prefixes;
	}

}