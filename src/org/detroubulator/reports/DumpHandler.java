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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.detroubulator.mappingprograms.MappingOutput;

final class DumpHandler {
	
	private static final int BUFF_SIZE = 4096;
	
	private int dumpCount;
	private Map<MappingOutput, File> dumps;
	private File dumpDir;
	
	DumpHandler(File dumpDir) throws IllegalArgumentException {
		if (dumpDir == null) {
			throw new NullPointerException("Null parameter: dumpDir");
		}
		/*
		 * Directories that do not yet exist are allowed
		 * but files that do exist make no sense.
		 */
		if (dumpDir.exists()) {
			if (!dumpDir.isDirectory()) {
				throw new IllegalArgumentException("Dump directory is not a directory: " + dumpDir.getPath());
			}
		}
		this.dumpDir = dumpDir;
		dumpCount = 0;
		dumps = new HashMap<MappingOutput, File>();
	}
	
	public File dumpOutput(MappingOutput output) throws IOException, IllegalArgumentException {
		if (dumpDir == null) {
			throw new IllegalStateException("Dump directory not set");
		}
		if (!output.hasPayload()) {
			throw new IllegalArgumentException("MappingOutput contains no payload");
		}
		/*
		 * If the output has been dumped already, return the
		 * previously created File object.
		 */
		if (dumps.containsKey(output)) {
			return dumps.get(output);
		}
		dumpCount++;
		// Make sure that the dump directory exists.
		if (!dumpDir.exists()) {
			dumpDir.mkdirs();
		}
		File dumpFile = new File(dumpDir, String.format("Dump%d.xml", dumpCount));
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dumpFile));
		BufferedInputStream in = new BufferedInputStream(output.getPayload());
		byte[] buffer = new byte[BUFF_SIZE];
		int len;
		while ((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}
		out.flush();
		out.close();
		/*
		 * Store the output file for later reuse, if the same output
		 * needs to be dumped again.
		 */
		dumps.put(output, dumpFile);
		return dumpFile;
	}

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if(!(o instanceof DumpHandler)) {
            return false;
        }
        DumpHandler other = (DumpHandler)o;
        return this.dumps.equals(other.dumps) && 
        	this.dumpDir.equals(other.dumpDir);
    }
    
    public int hashCode() {
    	/*
    	 * Hash algorithm borrowed from Item 8 of Effective Java 
    	 * by Joshua Bloch.
    	 */
    	int hc = 17;
   		hc = 37 * hc + dumps.hashCode();
   		hc = 37 * hc + dumpDir.hashCode();
    	return hc;
    }

}