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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.detroubulator.core.Logging;
import org.detroubulator.core.SWCV;
import org.detroubulator.core.TransformationParams;
import org.detroubulator.mappingprograms.ABAPMapping;
import org.detroubulator.mappingprograms.ABAPXSLTMapping;
import org.detroubulator.mappingprograms.InterfaceMapping;
import org.detroubulator.mappingprograms.JavaMapping;
import org.detroubulator.mappingprograms.MappingFailure;
import org.detroubulator.mappingprograms.MappingInput;
import org.detroubulator.mappingprograms.MappingOutput;
import org.detroubulator.mappingprograms.MessageMapping;
import org.detroubulator.mappingprograms.XSLTMapping;
import org.detroubulator.util.ConfigurationException;
import org.detroubulator.util.Console;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sap.mw.jco.JCO.Client;
import com.sap.mw.jco.JCO.Function;
import com.sap.mw.jco.JCO.ParameterList;
import com.sap.mw.jco.JCO.Structure;
import com.sap.mw.jco.JCO.Table;

public final class XIServerRFCImpl implements XIServer {

    private static final String RFC_ABAP_FUNCTION = "Z_XI_DTRB_MAPPING_EXECUTE";

    private static final String RFC_JAVA_FUNCTION = "SMPP_CALL_JAVA_RUNTIME3";
    
    private static final String RFC_INTF_FUNCTION = "Z_XI_DTRB_MAPPING_GET_STEPS";

    private static Logger log = Logging.getLogger(XIServerRFCImpl.class);

    private static final String DEFAULT_LOG_LEVEL = "3";

    private static final int BUFF_SIZE = 4096;

    private static final Set<String> MANDATORY_PARAMS;

    static {
        MANDATORY_PARAMS = new HashSet<String>();
        MANDATORY_PARAMS.add("host");
        MANDATORY_PARAMS.add("gateway");
        MANDATORY_PARAMS.add("client");
        MANDATORY_PARAMS.add("user");
        MANDATORY_PARAMS.add("sysnum");
        MANDATORY_PARAMS.add("progid");
    }

    private boolean configured;

    private String identification;

    private String logLevel;

    Client gwClient;

    Function function;
    
    Function abapFunction;
    
    Function intfFunction;

    private Client repoClient;
    
    private MappingOutput executeMappingABAP(String mappingType, 
            String mappingProgram, String mappingNamespace, SWCV mappingSWCV,
            MappingInput input, TransformationParams params) throws ServerException {
        log.entering(XIServerRFCImpl.class.getName(), "executeMappingABAP");
        
        // check if custom function is present
        if (abapFunction == null) {
            throw new ServerException(
                "Detroubulator custom ABAP function " + RFC_ABAP_FUNCTION + " not found on server.");
        }
        
        // marshal imports
        ParameterList imports = abapFunction.getImportParameterList();
        // clear imports before assigning any, otherwise table parameters
        // will be added to those from the previous run (if any)
        imports.clear();

        // SOURCE
        try {
            byte[] inputBytes = inputStreamToBytes(input.getInputStream());
            imports.setValue(inputBytes, "SOURCE");
        } catch (IOException e) {
            ServerException se = new ServerException(
                    "Error getting input document", e);
            log.throwing(getClass().getName(), "executeMappingABAP", se);
            throw se;
        }

        // mapping parameters
        imports.setValue(mappingType, "MAPTYPE");
        imports.setValue(mappingProgram, "PROG");
        if (mappingNamespace != null)
            imports.setValue(mappingNamespace, "NAMESPACE");
        if (mappingSWCV != null)
            imports.setValue(mappingSWCV.getID(), "SWCV");

        // PARAMS
        Table paramTab = imports.getTable("PARAMS");
        Map<String, String> map = params.getParameterMap();
        for (String key : map.keySet()) {
            paramTab.appendRow();
            paramTab.setValue(key, "NAME");
            paramTab.setValue(map.get(key), "VALUE");
        }

        // LOGLEVEL
        imports.setValue(logLevel, "LOGLEVEL");

        // execute function
        try {
            repoClient.connect();
            repoClient.execute(abapFunction);
            repoClient.disconnect();
        } catch (JCO.Exception e) {
            log.throwing(XIServerRFCImpl.class.getName(), "executeMappingABAP", e);
            throw new ServerException(e.getMessage(), e);
        }
        if (log.isLoggable(Level.FINE)) {
            try {
                File dumpFile = File
                        .createTempFile("XIServerRFCImpl_FunctionCall_",
                                ".html", new File("."));
                FileWriter writer = new FileWriter(dumpFile);
                log.fine("Dumping JCo function snapshot to file "
                        + dumpFile.getCanonicalPath());
                abapFunction.writeHTML(writer);
                writer.close();
            } catch (IOException e) {
                log.warning("Unable to write JCo HTML dump: " + e.getMessage());
            }
        }

        // marshal exports
        ParameterList exports = abapFunction.getExportParameterList();
        byte[] result = exports.getByteArray("RESULT");
        Structure mRet = exports.getStructure("MAPPING_RETURN");
        MappingOutput output = null;
        if ("OK".equalsIgnoreCase(mRet.getString("MONID"))) {
            output = new MappingOutput(result);
        } else {
            String msg = mRet.getString("MONID") + "," + mRet.getChar("MSGTY")
                    + "," + mRet.getString("MSGID") + ","
                    + mRet.getInt("MSGNO") + "," + mRet.getString("MSGV1")
                    + "," + mRet.getString("MSGV2") + ","
                    + mRet.getString("MSGV3") + "," + mRet.getString("MSGV4");
            if ("RESOURCE_NOT_FOUND".equalsIgnoreCase(mRet.getString("MONID")))
                // mapping program not found
                throw new ServerException(msg);
            else
                output = new MappingOutput(new MappingFailure(msg));
        }

        log.exiting(XIServerRFCImpl.class.getName(), "executeMappingABAP", output);
        return output;
    }

    private MappingOutput executeMapping(String mappingType, SWCV mappingSWCV,
            String mappingNamespace, String mappingProgram, MappingInput input,
            TransformationParams params) throws ServerException {
        log.entering(XIServerRFCImpl.class.getName(), "executeMapping");

        // marshal imports
        ParameterList imports = function.getImportParameterList();
        // clear imports before assigning any, otherwise table parameters
        // will be added to those from the previous run (if any)
        imports.clear();

        // SOURCE
        try {
            byte[] inputBytes = inputStreamToBytes(input.getInputStream());
            imports.setValue(inputBytes, "SOURCE");
        } catch (IOException e) {
            ServerException se = new ServerException(
                    "Error getting input document", e);
            log.throwing(getClass().getName(), "executeMapping", se);
            throw se;
        }

        // STEPS
        Table stepTab = imports.getTable("STEPS");
        stepTab.appendRow();
        stepTab.setValue(mappingType, "MAPTYPE");
        stepTab.setValue(mappingProgram, "PROG");
        stepTab.setValue(mappingNamespace, "NAMESPACE");
        stepTab.setValue(mappingSWCV.getID(), "VERSION_ID");
        stepTab.setValue(-1, "VERSION_SP");

        // PARAMS
        Table paramTab = imports.getTable("PARAMS");
        Map<String, String> map = params.getParameterMap();
        for (String key : map.keySet()) {
            paramTab.appendRow();
            paramTab.setValue(key, "NAME");
            paramTab.setValue(map.get(key), "VALUE");
        }

        // LOGLEVEL
        imports.setValue(logLevel, "LOGLEVEL");

        // CONTROL
        imports.setValue("0", "CONTROL");

        // execute function
        try {
            gwClient.connect();
            gwClient.execute(function);
            gwClient.disconnect();
        } catch (JCO.Exception e) {
            log.throwing(XIServerRFCImpl.class.getName(), "executeMapping", e);
            throw new ServerException(e.getMessage(), e);
        }
        if (log.isLoggable(Level.FINE)) {
            try {
                File dumpFile = File
                        .createTempFile("XIServerRFCImpl_FunctionCall_",
                                ".html", new File("."));
                FileWriter writer = new FileWriter(dumpFile);
                log.fine("Dumping JCo function snapshot to file "
                        + dumpFile.getCanonicalPath());
                function.writeHTML(writer);
                writer.close();
            } catch (IOException e) {
                log.warning("Unable to write JCo HTML dump: " + e.getMessage());
            }
        }

        // marshal exports
        ParameterList exports = function.getExportParameterList();
        byte[] result = exports.getByteArray("RESULT");
        Structure mRet = exports.getStructure("MAPPING_RETURN");
        MappingOutput output = null;
        if ("OK".equalsIgnoreCase(mRet.getString("MONID"))) {
            output = new MappingOutput(result);
        } else {
            String msg = mRet.getString("MONID") + "," + mRet.getChar("MSGTY")
                    + "," + mRet.getString("MSGID") + ","
                    + mRet.getInt("MSGNO") + "," + mRet.getString("MSGV1")
                    + "," + mRet.getString("MSGV2") + ","
                    + mRet.getString("MSGV3") + "," + mRet.getString("MSGV4");
            if ("RESOURCE_NOT_FOUND".equalsIgnoreCase(mRet.getString("MONID")))
                // mapping program not found
                throw new ServerException(msg);
            else
                output = new MappingOutput(new MappingFailure(msg));
        }

        log.exiting(XIServerRFCImpl.class.getName(), "executeMapping", output);
        return output;
    }

    public MappingOutput executeMessageMapping(MessageMapping mm,
            MappingInput input, TransformationParams params)
            throws ServerException {
        log.entering(XIServerRFCImpl.class.getName(), "executeMessageMapping");
        MappingOutput output = executeMapping("JAVA", mm.getMappingSWCV(), mm
                .getMappingNamespace(), "com/sap/xi/tf/_" + mm.getMappingName()
                + "_", input, params);
        log.exiting(XIServerRFCImpl.class.getName(), "executeMessageMapping",
                output);
        return output;
    }

    public MappingOutput executeJavaMapping(JavaMapping jm, MappingInput input,
            TransformationParams params) throws ServerException {
        log.entering(XIServerRFCImpl.class.getName(), "executeJavaMapping");
        MappingOutput output = executeMapping("JAVA", jm.getMappingSWCV(), jm
                .getMappingNamespace(), jm.getMappingName(), input, params);
        log.exiting(XIServerRFCImpl.class.getName(), "executeJavaMapping",
                output);
        return output;
    }

    public MappingOutput executeXSLTMapping(XSLTMapping xm, MappingInput input,
            TransformationParams params) throws ServerException {
        log.entering(XIServerRFCImpl.class.getName(), "executeXSLMapping");
        MappingOutput output = executeMapping("XSLT", xm.getMappingSWCV(), xm
                .getMappingNamespace(), xm.getMappingName(), input, params);
        log.exiting(XIServerRFCImpl.class.getName(), "executeXSLMapping",
                output);
        return output;
    }

    public MappingOutput executeABAPMapping(ABAPMapping am, MappingInput input, TransformationParams params) throws ServerException {
        log.entering(XIServerRFCImpl.class.getName(), "executeABAPMapping");
        MappingOutput output = executeMappingABAP(
                "R3_ABAP", am.getMappingName(), null, null, input, params);
        log.exiting(XIServerRFCImpl.class.getName(), "executeABAPMapping",
                output);
        return output;
    }

    public MappingOutput executeABAPXSLTMapping(ABAPXSLTMapping ax, MappingInput input, TransformationParams params) throws ServerException {
        log.entering(XIServerRFCImpl.class.getName(), "executeABAPMapping");
        MappingOutput output = executeMappingABAP(
                "R3_XSLT", ax.getMappingName(), null, null, input, params);
        log.exiting(XIServerRFCImpl.class.getName(), "executeABAPMapping",
                output);
        return output;
    }

    public MappingOutput executeInterfaceMapping(InterfaceMapping im, MappingInput input, TransformationParams params) throws ServerException {
        log.entering(XIServerRFCImpl.class.getName(), "executeInterfaceMapping");

        // check if custom function is present
        if (intfFunction == null) {
            throw new ServerException(
                "Detroubulator custom ABAP function " + RFC_INTF_FUNCTION + " not found on server.");
        }
        
        // marshal imports
        ParameterList imports = intfFunction.getImportParameterList();
        // clear imports before assigning any, otherwise table parameters
        // will be added to those from the previous run (if any)
        imports.clear();

        // mapping parameters
        imports.setValue(im.getMappingName(), "PROG");
        imports.setValue(im.getMappingNamespace(), "NAMESPACE");
        imports.setValue(im.getMappingSWCV().getID(), "SWCV");

        // execute function
        try {
            repoClient.connect();
            repoClient.execute(intfFunction);
            repoClient.disconnect();
        } catch (JCO.Exception e) {
            log.throwing(XIServerRFCImpl.class.getName(), "executeInterfaceMapping", e);
            throw new ServerException(e.getMessage(), e);
        }
        if (log.isLoggable(Level.FINE)) {
            try {
                File dumpFile = File
                        .createTempFile("XIServerRFCImpl_IntfFunctionCall_",
                                ".html", new File("."));
                FileWriter writer = new FileWriter(dumpFile);
                log.fine("Dumping JCo function snapshot to file "
                        + dumpFile.getCanonicalPath());
                intfFunction.writeHTML(writer);
                writer.close();
            } catch (IOException e) {
                log.warning("Unable to write JCo HTML dump: " + e.getMessage());
            }
        }

        // marshal exports into list of mapping steps
        SortedSet<InterfaceMappingStep> steps = new TreeSet<InterfaceMappingStep>();
        ParameterList exports = intfFunction.getExportParameterList();
        JCO.Table stepsTab = exports.getTable("STEPS");
        if (stepsTab.getNumRows() > 0) {
            stepsTab.firstRow();
            do {
                InterfaceMappingStep step = new InterfaceMappingStep(
                        stepsTab.getInt("STEP"),
                        stepsTab.getString("MAPTYPE"),
                        stepsTab.getString("PROG"),
                        stepsTab.getString("MAPNS"),
                        stepsTab.getString("STEP_VERSION_ID"));
                steps.add(step);
            } while(stepsTab.nextRow());
        }
        
        // execute mapping steps in turn
        MappingOutput output = null;
        for (Iterator<InterfaceMappingStep> it = steps.iterator(); it.hasNext(); ) {
            InterfaceMappingStep step = it.next();
            if ("JAVA".equals(step.type) || "XSLT".equals(step.type)) {
                output = executeMapping(step.type, new SWCV(step.swcv), step.ns, step.prog, input, params);
            } else if ("R3_ABAP".equals(step.type) || "R3_XSLT".equals(step.type)) {
                output = executeMappingABAP(step.type, step.prog, step.ns, new SWCV(step.swcv), input, params);
            } else {
                throw new ServerException("Unknown mapping type '" + step.type + "'");
            }
            if (output.hasFailure()) {
            	/*
            	 * Cannot proceed to next mapping step, returning the output
            	 * of the failed step.
            	 */ 
            	break;
            }
            if (it.hasNext()) {
                input = new MappingInput(output.getPayload());
            }
        }
                
        log.exiting(XIServerRFCImpl.class.getName(), "executeInterfaceMapping",
                output);
        return output;
    }

    public boolean isConfigured() {
        log.entering(XIServerRFCImpl.class.getName(), "isConfigured");

        log
                .exiting(XIServerRFCImpl.class.getName(), "isConfigured",
                        configured);
        return configured;
    }

    public void configure(Map<String, List<String>> params)
            throws ConfigurationException {
        // get password from parameters
        String password = null;
        List<String> pw = params.get("password");
        if (pw != null) {
            password = pw.get(0);
            pw.set(0, "********"); // hack to avoid logging plaintext password
        }

        log.entering(XIServerRFCImpl.class.getName(), "configure", params);

        log.info("JCo version: " + JCO.getVersion());

        /*
         * Make sure that we've got at least all the mandatory parameters.
         */
        for (String mandatory : MANDATORY_PARAMS) {
            if (!params.containsKey(mandatory)) {
                throw new ConfigurationException(
                        "Mandatory parameter not present: " + mandatory);
            }
        }

        // We're expecting each List<String> to contain at least one String.
        for (List<String> l : params.values()) {
            assert l.size() > 0;
        }
        // Store this server's identification.
        identification = String.format("Host %s, sys.no. %s, client %s", params
                .get("host").get(0), params.get("sysnum").get(0), params.get(
                "client").get(0));

        // Set up JCo stuff
        String host = params.get("host").get(0);
        String gwserv = params.get("gateway").get(0);
        String progid = params.get("progid").get(0);
        String client = params.get("client").get(0);
        String user = params.get("user").get(0);
        String sysnum = params.get("sysnum").get(0);

        // store loglevel
        List<String> paramsLogLevel = params.get("loglevel");
        if (paramsLogLevel != null && paramsLogLevel.size() > 0) {
            logLevel = paramsLogLevel.get(0);
        } else {
            logLevel = DEFAULT_LOG_LEVEL;
        }

        // prompt for password if not provided
        if (password == null) {
        	Console.p("Please log on to client %s of %s (sys.no. %s)", client, host, sysnum);
        	char[] pwChars = Console.readPassword(String.format("Enter password for user %s: ", user));
        	Console.p();
            if (pwChars == null) {
                throw new ConfigurationException("No password provided");
            } else {
                password = new String(pwChars);
            }
        }

        try {
            gwClient = JCO.createClient(host, gwserv, progid);
            repoClient = JCO.createClient(client, user, password, "EN",
                                host, sysnum);
            IRepository repository = JCO
                    .createRepository("default", repoClient);
            IFunctionTemplate template = repository
                    .getFunctionTemplate(RFC_JAVA_FUNCTION);
            function = template.getFunction();

            IFunctionTemplate abapTemplate = repository
                    .getFunctionTemplate(RFC_ABAP_FUNCTION);
            if (abapTemplate != null)
                abapFunction = abapTemplate.getFunction();

            IFunctionTemplate intfTemplate = repository
                    .getFunctionTemplate(RFC_INTF_FUNCTION);
            if (abapTemplate != null)
                intfFunction = intfTemplate.getFunction();

            configured = true;
        } catch (JCO.Exception e) {
            ServerException se = new ServerException(e);
            throw new ConfigurationException(se);
        }

        log.exiting(XIServerRFCImpl.class.getName(), "configure");
    }

    private static byte[] inputStreamToBytes(InputStream in) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        byte[] bytesIn = new byte[BUFF_SIZE];
        for (int bytesRead = in.read(bytesIn); bytesRead != -1; bytesRead = in
                .read(bytesIn)) {
            bytesOut.write(bytesIn, 0, bytesRead);
        }
        return bytesOut.toByteArray();
    }

    public String toString() {
        return identification;
    }

}