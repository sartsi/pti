/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.ui.views.metricrunner;

import org.phpsrc.eclipse.pti.tools.phpdepend.PHPDependPlugin;

/**
 * Defines constants which are used to refer to values in the plugin's
 * preference store.
 */
public class PHPDependPreferencesConstants {
	/**
	 * Boolean preference controlling whether the failure stack should be
	 * filtered.
	 */
	public static final String DO_FILTER_STACK = PHPDependPlugin.PLUGIN_ID + ".do_filter_stack"; //$NON-NLS-1$

	/**
	 * Boolean preference controlling whether the JUnit view should be shown on
	 * errors only.
	 */
	public static final String SHOW_ON_ERROR_ONLY = PHPDependPlugin.PLUGIN_ID + ".show_on_error"; //$NON-NLS-1$

	/**
	 * Boolean preference controlling whether the JUnit view should be shown on
	 * errors only.
	 */
	public static final String ENABLE_ASSERTIONS = PHPDependPlugin.PLUGIN_ID + ".enable_assertions"; //$NON-NLS-1$

	/**
	 * Maximum number of remembered test runs.
	 */
	public static final String MAX_METRIC_RUNS = PHPDependPlugin.PLUGIN_ID + ".max_metric_runs"; //$NON-NLS-1$

	/**
	 * Javadoc location for org.hamcrest.core (JUnit 4)
	 */
	public static final String HAMCREST_CORE_JAVADOC = PHPDependPlugin.PLUGIN_ID
			+ ".junit4.hamcrest.core.javadoclocation"; //$NON-NLS-1$

	private PHPDependPreferencesConstants() {
		// no instance
	}

	/**
	 * Serializes the array of strings into one comma separated string.
	 * 
	 * @param list
	 *            array of strings
	 * @return a single string composed of the given list
	 */
	public static String serializeList(String[] list) {
		if (list == null)
			return ""; //$NON-NLS-1$

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < list.length; i++) {
			if (i > 0)
				buffer.append(',');

			buffer.append(list[i]);
		}
		return buffer.toString();
	}
}
