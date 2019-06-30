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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public final class MappingInput {
	
	private InputStream stream;
	
	public MappingInput(InputStream stream) {
		if (stream == null) {
			throw new NullPointerException("Null parameter: stream");
		}
		this.stream = stream;
	}
	
	public MappingInput(File file) {
		if (file == null) {
			throw new NullPointerException("Null parameter: file");
		}
		try {
			stream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("File does not exist", e);
		}
	}
	
	public InputStream getInputStream() {
		return stream;
	}

}