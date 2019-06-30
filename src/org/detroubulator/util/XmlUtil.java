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
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class XmlUtil {

	private XmlUtil() {
		// Not supposed to be instantiated.
	}
	
	public static Element getChildElementByTagName(Node parent, String name) {
		Element child;
		List<Element> children = getChildElementsByTagName(parent, name);
		if (children.size() == 0) {
			child = null;
		} else {
			child = children.get(0);
		}
		return child;
	}
	
	public static List<Element> getChildElementsByTagName(Node parent, String name) {
		List<Element> children = new ArrayList<Element>();
		NodeList childNodes = parent.getChildNodes();
		for (int i = 0, len = childNodes.getLength(); i < len; i++) {
			Node childNode = childNodes.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element)childNode;
				if ("*".equals(name) || childElement.getTagName().equals(name)) {
					children.add(childElement);
				}
			}
		}
		return children;
	}
	
	public static Node appendSimpleChild(Node parent, String name, String content) {
		Document doc = parent.getOwnerDocument();
		Node child = parent.appendChild(doc.createElement(name));
		child.appendChild(doc.createTextNode(content));
		return child;
	}
	
	public static Node appendChild(Node parent, String name) {
		Document doc = parent.getOwnerDocument();
		Node child = parent.appendChild(doc.createElement(name));
		return child;
	}
	
}