package org.detroubulator.core;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;

public final class XPathScalar {
	
	private static final String NUMBER_PREFIX = "num:";
	private static final String STRING_PREFIX = "str:";
	private static final String BOOLEAN_PREFIX = "bool:";

	private static final Map<QName, Class> TYPE_MAP;

	static {
		TYPE_MAP = new HashMap<QName, Class>();
		TYPE_MAP.put(XPathConstants.BOOLEAN, Boolean.class);
		TYPE_MAP.put(XPathConstants.NUMBER, Double.class);
		TYPE_MAP.put(XPathConstants.STRING, String.class);
	}
	
	private QName type;
	private Object value;
	
	public XPathScalar(QName type, Object value) {
		if (type == null) {
			throw new NullPointerException("Null parameter: type");
		}
		if (value == null) {
			throw new NullPointerException("Null parameter: value");
		}
		if (!TYPE_MAP.keySet().contains(type)) {
			throw new IllegalArgumentException(String.format("Type not allowed: %s", type));
		}
		if (!TYPE_MAP.get(type).equals(value.getClass())) {
			throw new IllegalArgumentException(String.format("Unexpected class of value. Expected class %s, actual class is %s.", TYPE_MAP.get(type), value.getClass()));
		}
		this.type = type;
		this.value = value;
	}

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if(!(o instanceof XPathScalar)) {
            return false;
        }
        XPathScalar other = (XPathScalar)o;
        return this.type.equals(other.type) && this.value.equals(other.value);
    }

    public int hashCode() {
        /*
         *  Hash algorithm borrowed from Item 8 of Effective Java by Joshua
         *  Bloch (if you don't own this book, buy it immediately!).
         */
        int result = 41;
        result = 43 * result + type.hashCode();
        result = 43 * result + value.hashCode();
        return result;
    }

    public String toString() {
        return value.toString();
    }

	public QName getType() {
		/*
		 * QName is immutable.
		 */
		return type;
	}
	
	public Object getValue() {
		/*
		 * All three possible value classes (String, Double and Boolean)
		 * are immutable.
		 */
		return value;
	}
	
	public static XPathScalar valueOf(String s) {
		XPathScalar xps;
		if (s.startsWith(STRING_PREFIX)) {
			/*
			 * String scalar.
			 */
			String sval = s.substring(STRING_PREFIX.length());
			xps = new XPathScalar(XPathConstants.STRING, sval);
		} else if (s.startsWith(NUMBER_PREFIX)) {
			/*
			 * Numeric scalar.
			 */
			if (s.length() == NUMBER_PREFIX.length()) {
				/*
				 * No number is present.
				 */
				throw new IllegalArgumentException("No number present, correct format is num:123");
			}
			String sval = s.substring(NUMBER_PREFIX.length());
			try {
				xps = new XPathScalar(XPathConstants.NUMBER, Double.valueOf(sval));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(String.format("Cannot convert %s to a number.", sval));
			}
		} else if (s.startsWith(BOOLEAN_PREFIX)) {
			/*
			 * Boolean scalar.
			 */
			if (s.length() == BOOLEAN_PREFIX.length()) {
				/*
				 * No boolean value present.
				 */
				throw new IllegalArgumentException("No boolean value present, correct format is bool:true or bool:false");
			}
			boolean bval;
			String sval = s.substring(BOOLEAN_PREFIX.length());
			if ("true".equals(sval)) {
				bval = true;
			} else if ("false".equals(sval)) {
				bval = false;
			} else {
				throw new IllegalArgumentException(String.format("Bad boolean value (must be true or false): %s", sval));
			}
			xps = new XPathScalar(XPathConstants.BOOLEAN, Boolean.valueOf(bval));
		} else {
			/*
			 * Assume that the type is string.
			 */
			xps = new XPathScalar(XPathConstants.STRING, s);
		}
		return xps;
	}
	
}
