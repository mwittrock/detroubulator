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

public final class MappingFailure {
	
	private String message;
	
	public MappingFailure(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if(!(o instanceof MappingFailure)) {
            return false;
        }
        MappingFailure other = (MappingFailure)o;
        return this.message.equals(other.message);
    }
    
    public int hashCode() {
    	/*
    	 * Hash algorithm borrowed from Item 8 of Effective Java 
    	 * by Joshua Bloch.
    	 */
    	int hc = 17;
    	hc = 37 * hc + message.hashCode();
    	return hc;
    }

}