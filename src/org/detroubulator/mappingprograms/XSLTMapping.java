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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.detroubulator.core.Logging;
import org.detroubulator.core.SWCV;
import org.detroubulator.core.TransformationParams;
import org.detroubulator.server.ServerException;
import org.detroubulator.util.ConfigurationException;

public final class XSLTMapping extends JavaMapping {

    private static Logger log = Logging.getLogger(XSLTMapping.class);

    private static final Set<String> MANDATORY_PARAMS;

    static {
        MANDATORY_PARAMS = new HashSet<String>();
        MANDATORY_PARAMS.add("name");
        MANDATORY_PARAMS.add("ns");
        MANDATORY_PARAMS.add("swcv.id");
    }

    private boolean configured;
    private String mappingName;
    private String mappingNamespace;
    private SWCV mappingSWCV;
    
    public String getMappingName() {
        return mappingName;
    }

    public String getMappingNamespace() {
        return mappingNamespace;
    }

    public SWCV getMappingSWCV() {
        return mappingSWCV;
    }

    public boolean isConfigured() {
        log.entering(XSLTMapping.class.getName(), "isConfigured");
        log.exiting(XSLTMapping.class.getName(), "isConfigured", configured);
        return configured;
    }

    public void configure(Map<String, List<String>> params) throws ConfigurationException {
        log.entering(XSLTMapping.class.getName(), "configure", params); 
        if (params == null) {
            throw new NullPointerException("Null parameter: params");
        }
        /*
         * Make sure that we've got at least all the mandatory parameters.
         */
        for (String mandatory : MANDATORY_PARAMS) {
            if (!params.containsKey(mandatory)) {
                throw new ConfigurationException("Mandatory parameter not present: " + mandatory);
            }
        }
        // We're expecting each List<String> to contain at least one String.
        for (List<String> l : params.values()) {
            assert l.size() > 0;
        }
        mappingName = params.get("name").get(0);
        mappingNamespace = params.get("ns").get(0);
        mappingSWCV = new SWCV(params.get("swcv.id").get(0));
        configured = true;
        log.exiting(XSLTMapping.class.getName(), "configure");
    }

    public MappingOutput execute(TransformationParams params, MappingInput input) throws ServerException {
    	log.entering(XSLTMapping.class.getName(), "execute", new Object[] {params, input});
    	if (!canExecute()) {
    		IllegalStateException e = new IllegalStateException("No associated server");
    		log.throwing(this.getClass().getName(), "execute", e);
    		throw e;
    	}
        MappingOutput result = getServer().executeXSLTMapping(this, input, params);
        log.exiting(XSLTMapping.class.getName(), "execute", result);
        return result;
    }

    public String toString() {
        log.entering(XSLTMapping.class.getName(), "identify");
        String ident = String.format("XSLT mapping %s", getMappingName());
        log.exiting(XSLTMapping.class.getName(), "identify", ident);
        return ident;
    }

}