/*******************************************************************************
 * Copyright (c) 2010, Sven Kiera
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * - Neither the name of the Organisation nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.phpsrc.eclipse.pti.tools.phpdepend.PHPDependPlugin;

public class PHPDependPreferencesFactory {
	public static PHPDependPreferences factory(IFile file) {
		return factory(file.getProject());
	}

	public static PHPDependPreferences factory(IProject project) {
		Preferences prefs = PHPDependPlugin.getDefault().getPluginPreferences();

		String phpExe = prefs.getString(PHPDependPreferenceNames.PREF_PHPDEPEND_PHP_EXECUTABLE);
		boolean printOutput = prefs
				.getBoolean(PHPDependPreferenceNames.PREF_PHPDEPEND_DEBUG_PRINT_OUTPUT);
		String pearLibraryName = prefs
				.getString(PHPDependPreferenceNames.PREF_PHPDEPEND_PEAR_LIBRARY);
		boolean withoutAnnotation = prefs
				.getBoolean(PHPDependPreferenceNames.PREF_PHPDEPEND_WITHOUT_ANNOTATIONS);
		boolean badDocumentation = prefs
				.getBoolean(PHPDependPreferenceNames.PREF_PHPDEPEND_BAD_DOCUMENTATION);
		String coderankMode = prefs
				.getString(PHPDependPreferenceNames.PREF_PHPDEPEND_CODERANK_MODE);
		String optimization = prefs.getString(PHPDependPreferenceNames.PREF_PHPDEPEND_OPTIMIZATION);
		String validFileExtensions = prefs
				.getString(PHPDependPreferenceNames.PREF_PHPDEPEND_VALID_FILE_EXTENSIONS);
		String excludePackages = prefs
				.getString(PHPDependPreferenceNames.PREF_PHPDEPEND_EXCLUDE_PACKAGES);

		String enabledPrefs = prefs.getString(PHPDependPreferenceNames.PREF_METRICS_ENABLED);
		String idPrefs = prefs.getString(PHPDependPreferenceNames.PREF_METRICS_IDS);
		String namePrefs = prefs.getString(PHPDependPreferenceNames.PREF_METRICS_NAMES);
		String warningMinPrefs = prefs.getString(PHPDependPreferenceNames.PREF_METRICS_WARNING_MIN);
		String warningMaxPrefs = prefs.getString(PHPDependPreferenceNames.PREF_METRICS_WARNING_MAX);
		String errorMinPrefs = prefs.getString(PHPDependPreferenceNames.PREF_METRICS_ERROR_MIN);
		String errorMaxPrefs = prefs.getString(PHPDependPreferenceNames.PREF_METRICS_ERROR_MAX);
		String typePrefs = prefs.getString(PHPDependPreferenceNames.PREF_METRICS_TYPES);

		IScopeContext[] preferenceScopes = createPreferenceScopes(project);
		if (preferenceScopes[0] instanceof ProjectScope) {
			IEclipsePreferences node = preferenceScopes[0].getNode(PHPDependPlugin.PLUGIN_ID);
			if (node != null) {
				phpExe = node.get(PHPDependPreferenceNames.PREF_PHPDEPEND_PHP_EXECUTABLE, phpExe);
				printOutput = node.getBoolean(
						PHPDependPreferenceNames.PREF_PHPDEPEND_DEBUG_PRINT_OUTPUT, printOutput);
				pearLibraryName = node.get(PHPDependPreferenceNames.PREF_PHPDEPEND_PEAR_LIBRARY,
						pearLibraryName);
				withoutAnnotation = node.getBoolean(
						PHPDependPreferenceNames.PREF_PHPDEPEND_WITHOUT_ANNOTATIONS,
						withoutAnnotation);
				badDocumentation = node
						.getBoolean(PHPDependPreferenceNames.PREF_PHPDEPEND_BAD_DOCUMENTATION,
								badDocumentation);
				coderankMode = node.get(PHPDependPreferenceNames.PREF_PHPDEPEND_CODERANK_MODE,
						coderankMode);
				optimization = node.get(PHPDependPreferenceNames.PREF_PHPDEPEND_OPTIMIZATION,
						optimization);
				validFileExtensions = node.get(
						PHPDependPreferenceNames.PREF_PHPDEPEND_VALID_FILE_EXTENSIONS,
						validFileExtensions);
				excludePackages = node.get(
						PHPDependPreferenceNames.PREF_PHPDEPEND_EXCLUDE_PACKAGES, excludePackages);

				enabledPrefs = node
						.get(PHPDependPreferenceNames.PREF_METRICS_ENABLED, enabledPrefs);
				idPrefs = node.get(PHPDependPreferenceNames.PREF_METRICS_IDS, idPrefs);
				namePrefs = node.get(PHPDependPreferenceNames.PREF_METRICS_NAMES, namePrefs);
				warningMinPrefs = node.get(PHPDependPreferenceNames.PREF_METRICS_WARNING_MIN,
						warningMinPrefs);
				warningMaxPrefs = node.get(PHPDependPreferenceNames.PREF_METRICS_WARNING_MAX,
						warningMaxPrefs);
				errorMinPrefs = node.get(PHPDependPreferenceNames.PREF_METRICS_ERROR_MIN,
						errorMinPrefs);
				errorMaxPrefs = node.get(PHPDependPreferenceNames.PREF_METRICS_ERROR_MAX,
						errorMaxPrefs);
				typePrefs = node.get(PHPDependPreferenceNames.PREF_METRICS_TYPES, typePrefs);
			}
		}

		Metric[] metrics = new Metric[0];

		if (enabledPrefs != null && !"".equals(enabledPrefs)) {
			String[] enabledList = enabledPrefs.split(";");
			String[] idList = idPrefs.split(";");
			String[] nameList = namePrefs.split(";");
			String[] warningMinList = warningMinPrefs.split(";");
			String[] warningMaxList = warningMaxPrefs.split(";");
			String[] errorMinList = errorMinPrefs.split(";");
			String[] errorMaxList = errorMaxPrefs.split(";");

			ArrayList<Metric> metricList = new ArrayList<Metric>(enabledList.length);
			for (int i = 0; i < enabledList.length; i++) {
				Metric m = new Metric();
				m.enabled = "1".equals(enabledList[i]) ? true : false;
				m.id = idList[i];
				m.name = nameList[i];
				try {
					m.warningMin = Float.parseFloat(warningMinList[i]);
				} catch (Exception e) {
				}
				try {
					m.warningMax = Float.parseFloat(warningMaxList[i]);
				} catch (Exception e) {
				}
				try {
					m.errorMin = Float.parseFloat(errorMinList[i]);
				} catch (Exception e) {
				}
				try {
					m.errorMax = Float.parseFloat(errorMaxList[i]);
				} catch (Exception e) {
				}

				metricList.add(m);
			}

			metrics = metricList.toArray(metrics);
		}

		return new PHPDependPreferences(phpExe, printOutput, pearLibraryName, withoutAnnotation,
				badDocumentation, coderankMode, optimization, validFileExtensions, excludePackages,
				metrics);
	}

	public static PHPDependPreferences factory(IResource resource) {
		return factory(resource.getProject());
	}

	protected static IScopeContext[] createPreferenceScopes(IProject project) {
		if (project != null) {
			return new IScopeContext[] { new ProjectScope(project), new InstanceScope(),
					new DefaultScope() };
		}
		return new IScopeContext[] { new InstanceScope(), new DefaultScope() };
	}
}
