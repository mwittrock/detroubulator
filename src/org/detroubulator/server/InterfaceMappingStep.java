/*
 * Copyright 2007 AppliCon A/S
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

package org.detroubulator.server;

class InterfaceMappingStep implements Comparable<InterfaceMappingStep> {
    int step;
    String type;
    String prog;
    String ns;
    String swcv;
    
    InterfaceMappingStep(int step, String type, String prog, String ns, String swcv) {
        this.step = step;
        this.type = type;
        this.prog = prog;
        this.ns = ns;
        this.swcv = swcv;
    }
    
    public int compareTo(InterfaceMappingStep other) {
        return this.step - other.step;
    }

}
