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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

public final class MappingOutput {

	private MappingFailure failure;
	private byte[] payload;
	
    public MappingOutput(MappingFailure failure) {
        if (failure == null) {
            throw new NullPointerException("Null parameter: failure");
        }
        this.failure = failure;
    }
    
    public MappingOutput(byte[] payload) {
        if (payload == null) {
            throw new NullPointerException("Null parameter: payload");
        }
        this.payload = new byte[payload.length];
        System.arraycopy(payload, 0, this.payload, 0, payload.length);
    }
	
	public boolean hasFailure() {
		return failure != null;
	}
	
	public boolean hasPayload() {
		return (payload != null);
	}
	
	public MappingFailure getFailure() {
		if (!hasFailure()) {
			throw new IllegalStateException("No mapping failure available");
		}
		return failure;
	}
	
	public InputStream getPayload() {
		if (!hasPayload()) {
			throw new IllegalStateException("No payload available");
		}
		return new ByteArrayInputStream(payload);
	}

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("MappingOutput wrapping: ");
        if (hasPayload()) {
        	sb.append(payload.length).append(" bytes of payload");
        } else {
        	sb.append("MappingFailure (").append(failure.getMessage()).append(")");
        }
        return sb.toString();
    }
    
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if(!(o instanceof MappingOutput)) {
            return false;
        }
        MappingOutput other = (MappingOutput)o;
        /*
         * The MappingOutput instances must either both encapsulate
         * a mapping failure or both encapsulate a payload. Otherwise
         * they cannot be equal.
         */
        if ((this.hasPayload() && other.hasFailure()) ||
        		(this.hasFailure() && other.hasPayload())) {
        	return false;
        }
        if (this.hasPayload()) {
        	return Arrays.equals(this.payload, other.payload); 
        } else {
        	assert hasFailure();
        	return this.failure.equals(other.failure);
        }
    }
    
    public int hashCode() {
    	/*
    	 * Hash algorithm borrowed from Item 8 of Effective Java 
    	 * by Joshua Bloch.
    	 */
    	int hc = 17;
    	if (hasPayload()) {
    		hc = 37 * hc + Arrays.hashCode(payload);
    	} else {
    		assert hasFailure();
    		hc = 37 * hc + failure.hashCode();
    	}
    	return hc;
    }

}