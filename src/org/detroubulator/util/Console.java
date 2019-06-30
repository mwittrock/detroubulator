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

package org.detroubulator.util;

public final class Console {
	
	private static final String SECTION_INDENT = "   ";
	
	private static boolean inSection = false;

	private Console() {
		// Not meant to be instantiated.
	}

	public static void p() {
		System.out.println();
	}

	public static void p(String s) {
		if (inSection) {
			System.out.println(SECTION_INDENT + s);
		} else {
			System.out.println(s);
		}
	}

	public static void p(String format, Object... args) {
		if (inSection) {
			System.out.println(SECTION_INDENT + String.format(format, args));
		} else {
			System.out.println(String.format(format, args));
		}
	}

	public static void startSection(String label) {
		if (inSection) {
			throw new IllegalStateException("A labeled section has already been started");
		}
		if (label == null) {
			throw new NullPointerException("Null parameter: label");
		}
		p("%s:", label);
		inSection = true;
	}

	public static void endSection() {
		if (!inSection) {
			throw new IllegalStateException("A labeled section has not been started");
		}
		inSection = false;
		p();
	}
	
    /**
     * Shortcut to java.io.console.readPassword(fmt, args...)
     * @param fmt A format string as described in Format string syntax  for the prompt text.
     * @param args Arguments referenced by the format specifiers in the format string.
     * @return A character array containing the password or passphrase read from the console, 
     * not including any line-termination characters, or null if an end of stream has been reached.
     */
    public static char[] readPassword(String fmt, Object... args) {
        java.io.Console cons = System.console();
        return cons.readPassword(fmt, args);
    }
}