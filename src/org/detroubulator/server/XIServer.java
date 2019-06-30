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

package org.detroubulator.server;

import org.detroubulator.core.TransformationParams;
import org.detroubulator.mappingprograms.ABAPMapping;
import org.detroubulator.mappingprograms.ABAPXSLTMapping;
import org.detroubulator.mappingprograms.InterfaceMapping;
import org.detroubulator.mappingprograms.JavaMapping;
import org.detroubulator.mappingprograms.MappingInput;
import org.detroubulator.mappingprograms.MappingOutput;
import org.detroubulator.mappingprograms.MessageMapping;
import org.detroubulator.mappingprograms.XSLTMapping;
import org.detroubulator.util.Configurable;

public interface XIServer extends Configurable {

    public MappingOutput executeJavaMapping(JavaMapping jm,
            MappingInput input, TransformationParams params)
            throws ServerException;
    
    public MappingOutput executeMessageMapping(MessageMapping mm,
            MappingInput input, TransformationParams params)
            throws ServerException;
    
    public MappingOutput executeXSLTMapping(XSLTMapping xm,
            MappingInput input, TransformationParams params)
            throws ServerException;

    public MappingOutput executeABAPMapping(ABAPMapping am,
            MappingInput input, TransformationParams params)
            throws ServerException;

    public MappingOutput executeABAPXSLTMapping(ABAPXSLTMapping ax,
            MappingInput input, TransformationParams params)
            throws ServerException;

    public MappingOutput executeInterfaceMapping(InterfaceMapping im,
            MappingInput input, TransformationParams params)
            throws ServerException;

}