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

package org.detroubulator.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.sap.aii.mapping.api.StreamTransformationConstants;

public final class TransformationParams {

	private static final String EMPTY_PARAM = "";
	
	private Map<String, String> parameters;
	
	/*
	 * In the no-arg constructor we knowingly access deprecated fields 
	 * in the StreamTransformationConstants interface, so we don't need 
	 * to be warned about deprecation, thank you very much. 
	 */
	@SuppressWarnings("deprecation")

	public TransformationParams() {
		/*
		 * The default parameters and their values are identical to the
		 * default parameters in the Integration Builder.
		 */
		parameters = new HashMap<String, String>();
		parameters.put(StreamTransformationConstants.CONVERSATION_ID, EMPTY_PARAM);
		parameters.put(StreamTransformationConstants.INTERFACE, EMPTY_PARAM);
		parameters.put(StreamTransformationConstants.INTERFACE_NAMESPACE, EMPTY_PARAM);
		parameters.put(StreamTransformationConstants.MESSAGE_ID, EMPTY_PARAM);
		parameters.put(StreamTransformationConstants.PROCESSING_MODE, EMPTY_PARAM);
		parameters.put(StreamTransformationConstants.RECEIVER_NAME, EMPTY_PARAM);
		parameters.put(StreamTransformationConstants.RECEIVER_NAMESPACE, EMPTY_PARAM);
		parameters.put(StreamTransformationConstants.RECEIVER_PARTY, "Test_ReceiverParty");
		parameters.put(StreamTransformationConstants.RECEIVER_PARTY_AGENCY, EMPTY_PARAM);
		parameters.put(StreamTransformationConstants.RECEIVER_PARTY_SCHEME, EMPTY_PARAM);
		parameters.put(StreamTransformationConstants.RECEIVER_SERVICE, "Test_ReceiverService");
		parameters.put(StreamTransformationConstants.REF_TO_MESSAGE_ID, EMPTY_PARAM);
		parameters.put(StreamTransformationConstants.SENDER_PARTY, "Test_SenderParty");
		parameters.put(StreamTransformationConstants.SENDER_PARTY_AGENCY, EMPTY_PARAM);
		parameters.put(StreamTransformationConstants.SENDER_PARTY_SCHEME, EMPTY_PARAM);
		parameters.put(StreamTransformationConstants.SENDER_SERVICE, "Test_SenderService");
		parameters.put(StreamTransformationConstants.TIME_SENT, EMPTY_PARAM);
		parameters.put(StreamTransformationConstants.VERSION_MAJOR, EMPTY_PARAM);
		parameters.put(StreamTransformationConstants.VERSION_MINOR, EMPTY_PARAM);
		/*
		 * The following fields are deprecated but nevertheless available in 
		 * the Integration Builder, which is why we use them in spite of their
		 * deprecated status.
		 */
		parameters.put(StreamTransformationConstants.SENDER_SYSTEM, "Test_SenderSystem");
		parameters.put(StreamTransformationConstants.SENDER_NAMESPACE, EMPTY_PARAM);
		parameters.put(StreamTransformationConstants.SENDER_NAME, EMPTY_PARAM);
		parameters.put(StreamTransformationConstants.RECEIVER_SYSTEM, "Test_ReceiverSystem");
	}

	public TransformationParams(TransformationParams other) {
		/*
		 * Create a new TransformationParams object with the same contents
		 * as an existing TransformationParams object.
		 */
		parameters = new HashMap<String, String>(other.parameters);
	}
	
	public void addParameter(String name, String value) throws IllegalArgumentException {
		if (name == null) {
			throw new NullPointerException("Null value: name");
		}
		/*
		 * The parameter must be the name of a public, static, final field
		 * in the StreamTransformationConstants interface. If it isn't, the 
		 * call to getFieldValue throws an IllegalArgumentException. 
		 */
		String fldVal = getFieldValue(name);
		/*
		 * Null values are interpreted as empty parameters.
		 */
		parameters.put(fldVal, value == null ? EMPTY_PARAM : value);
	}
	
	public Map<String, String> getParameterMap() {
		return Collections.unmodifiableMap(parameters);
	}
	
	private String getFieldValue(String fieldName) throws IllegalArgumentException {
		assert fieldName != null;
		Field fld;
		String val;
		try {
			fld = StreamTransformationConstants.class.getField(fieldName);
		} catch (NoSuchFieldException e) {
			throw new IllegalArgumentException(String.format("Field %s not present in StreamTransformationConstants", fieldName));
		}
		int fieldMod = fld.getModifiers();
		if (!Modifier.isStatic(fieldMod)) {
			throw new IllegalArgumentException(String.format("Field %s is not static", fieldName));
		}
		if (!Modifier.isFinal(fieldMod)) {
			throw new IllegalArgumentException(String.format("Field %s is not final", fieldName));
		}
		try {
			val = (String)fld.get(null);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(String.format("Field %s is not accessible", fieldName));
		}
		return val;
	}
	
	public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if(!(o instanceof TransformationParams)) {
            return false;
        }
        TransformationParams other = (TransformationParams)o;
        return this.parameters.equals(other.parameters);
	}
	
	public int hashCode() {
		return parameters.hashCode();
	}

}