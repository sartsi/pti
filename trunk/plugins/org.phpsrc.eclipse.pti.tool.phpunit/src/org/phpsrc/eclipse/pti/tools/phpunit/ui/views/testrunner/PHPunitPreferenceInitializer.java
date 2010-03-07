/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Davids <sdavids@gmx.de> - initial API and implementation
 *******************************************************************************/
package org.phpsrc.eclipse.pti.tools.phpunit.ui.views.testrunner;

import java.util.List;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jdt.internal.junit.ui.JUnitPlugin;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Default preference value initialization for the
 * <code>org.eclipse.jdt.junit</code> plug-in.
 */
public class PHPunitPreferenceInitializer extends AbstractPreferenceInitializer {

	/** {@inheritDoc} */
	public void initializeDefaultPreferences() {
		IPreferenceStore prefs = JUnitPlugin.getDefault().getPreferenceStore();

		prefs.setDefault(PHPUnitPreferencesConstants.DO_FILTER_STACK, true);

		prefs.setDefault(PHPUnitPreferencesConstants.SHOW_ON_ERROR_ONLY, false);
		prefs.setDefault(PHPUnitPreferencesConstants.ENABLE_ASSERTIONS, false);

		List defaults = PHPUnitPreferencesConstants.createDefaultStackFiltersList();
		String[] filters = (String[]) defaults.toArray(new String[defaults.size()]);
		String active = PHPUnitPreferencesConstants.serializeList(filters);
		prefs.setDefault(PHPUnitPreferencesConstants.PREF_ACTIVE_FILTERS_LIST, active);
		prefs.setDefault(PHPUnitPreferencesConstants.PREF_INACTIVE_FILTERS_LIST, ""); //$NON-NLS-1$
		prefs.setDefault(PHPUnitPreferencesConstants.MAX_TEST_RUNS, 10);

		// see
		// http://sourceforge.net/tracker/index.php?func=detail&aid=1877429&group_id=15278&atid=115278
		prefs.setDefault(PHPUnitPreferencesConstants.JUNIT3_JAVADOC, "http://www.junit.org/junit/javadoc/3.8.1"); //$NON-NLS-1$
		prefs.setDefault(PHPUnitPreferencesConstants.JUNIT4_JAVADOC, "http://www.junit.org/junit/javadoc/4.5"); //$NON-NLS-1$
	}
}
