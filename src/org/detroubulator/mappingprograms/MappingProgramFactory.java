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

package org.detroubulator.mappingprograms;

import java.util.List;
import java.util.Map;

import org.detroubulator.util.ConfigurationException;

public final class MappingProgramFactory {
	
	private MappingProgramFactory() {
		// Not supposed to be instantiated.
	}
	
	public static MappingProgram newInstance(MappingProgramType type, Map<String, List<String>> config) throws ConfigurationException {
		MappingProgram mp;
		switch (type) {
        case java:
            mp = new JavaMapping();
            break;
		case message:
			mp = new MessageMapping();
			break;
        case xslt:
            mp = new XSLTMapping();
            break;
        case abap:
            mp = new ABAPMapping();
            break;
        case abapxslt:
            mp = new ABAPXSLTMapping();
            break;
        case intf:
            mp = new InterfaceMapping();
            break;
		default:
			throw new AssertionError("Unexpected mapping program type: " + type);
		}
		mp.configure(config);
		assert mp.isConfigured();
		return mp;
	}

}