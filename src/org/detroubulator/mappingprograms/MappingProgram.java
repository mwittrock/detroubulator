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

import org.detroubulator.core.TransformationParams;
import org.detroubulator.server.ServerException;
import org.detroubulator.server.XIServer;
import org.detroubulator.util.Configurable;

public interface MappingProgram extends Configurable {
	
	public boolean canExecute();
	
	public MappingOutput execute(TransformationParams params, MappingInput input) throws ServerException;

    public void setServer(XIServer server);
    
    public XIServer getServer();

}