/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.core.launching;

public class OperatingSystem {
	public static final boolean WINDOWS = java.io.File.separatorChar == '\\'; //$NON-NLS-1$
	public static final boolean LINUX = java.io.File.separatorChar == '/'; //$NON-NLS-1$
	public static final boolean MAC = System.getProperty("os.name").startsWith("Mac"); //$NON-NLS-1$

	public static String escapeShellArg(String arg) {
		if (WINDOWS) {
			return "\"" + arg.replace("\"", "\\\"") + "\"";
		} else {
			return arg.replace(" ", "\\ ");
		}
	}

	public static String escapeShellFileArg(String fileName) {
		return escapeShellArg(fileName);
	}
}
