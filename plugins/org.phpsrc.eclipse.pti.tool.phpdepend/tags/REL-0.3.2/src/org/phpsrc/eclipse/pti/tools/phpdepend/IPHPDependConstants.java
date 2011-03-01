/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend;

public interface IPHPDependConstants {
	public static final String PLUGIN_ID = PHPDependPlugin.PLUGIN_ID;

	public static final String PREFERENCE_PAGE_ID = PLUGIN_ID
			+ ".preferences.PHPToolsPreferencePage"; //$NON-NLS-1$
	public static final String PROJECT_PAGE_ID = PLUGIN_ID + ".properties.PHPToolsPreferencePage"; //$NON-NLS-1$

	public static final String VALIDATOR_PHP_DEPEND_MARKER = PLUGIN_ID
			+ ".validator.phpToolPHPDependMarker"; //$NON-NLS-1$
}
