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

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.detroubulator.core.Logging;
import org.detroubulator.util.ConfigurationException;

import com.sap.mw.jco.JCO;

public final class XIServerFactory {

    private static Logger log = Logging.getLogger(XIServerFactory.class);
	
	private XIServerFactory() {
		// Not supposed to be instantiated.
	}
	
    public static XIServer newInstance(Map<String, List<String>> params) throws ServerException, ConfigurationException  {
        log.entering(XIServerFactory.class.getName(), "newInstance", params);
        XIServer server;
        try {
            server = new XIServerRFCImpl();
        } catch (JCO.Exception e) {
            throw new ServerException(e.getMessage(), e);
        }
        server.configure(params);
        assert server.isConfigured();
        log.exiting(XIServerFactory.class.getName(), "newInstance", server);
        return server;
    }

}