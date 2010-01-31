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

package org.phpsrc.eclipse.pti.tools.codesniffer.core.preferences;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.phpsrc.eclipse.pti.tools.codesniffer.PHPCodeSnifferPlugin;
import org.phpsrc.eclipse.pti.tools.codesniffer.ui.preferences.PHPCodeSnifferPreferenceNames;

public class PHPCodeSnifferPreferencesFactory {
	public static PHPCodeSnifferPreferences factory(IFile file) {
		return factory(file.getProject());
	}

	public static PHPCodeSnifferPreferences factory(IResource resource) {
		return factory(resource.getProject());
	}

	public static PHPCodeSnifferPreferences factory(IProject project) {
		Preferences prefs = PHPCodeSnifferPlugin.getDefault().getPluginPreferences();

		String phpExe = prefs.getString(PHPCodeSnifferPreferenceNames.PREF_PHP_EXECUTABLE);

		// Check first the standard path. Is it not empty we have a custom
		// standard, so we must use the path instead of the name.
		String standard = prefs.getString(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_STANDARD_PATH);
		String activeStandards = prefs.getString(PHPCodeSnifferPreferenceNames.PREF_ACTIVE_STANDARDS);
		String pearLibraryName = prefs.getString(PHPCodeSnifferPreferenceNames.PREF_PEAR_LIBRARY);

		int tabWidth = prefs.getInt(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_TAB_WITH);
		String fileExtensions = prefs.getString(PHPCodeSnifferPreferenceNames.PREF_FILE_EXTENSIONS);
		boolean printOutput = prefs.getBoolean(PHPCodeSnifferPreferenceNames.PREF_DEBUG_PRINT_OUTPUT);
		String ignorePattern = prefs.getString(PHPCodeSnifferPreferenceNames.PREF_IGNORE_PATTERN);
		String ignoreSniffs = prefs.getString(PHPCodeSnifferPreferenceNames.PREF_IGNORE_SNIFFS);

		IScopeContext[] preferenceScopes = createPreferenceScopes(project);
		if (preferenceScopes[0] instanceof ProjectScope) {
			IEclipsePreferences node = preferenceScopes[0].getNode(PHPCodeSnifferPlugin.PLUGIN_ID);
			if (node != null) {
				phpExe = node.get(PHPCodeSnifferPreferenceNames.PREF_PHP_EXECUTABLE, phpExe);

				standard = node.get(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_STANDARD_PATH, standard);
				activeStandards = node.get(PHPCodeSnifferPreferenceNames.PREF_ACTIVE_STANDARDS, activeStandards);
				pearLibraryName = node.get(PHPCodeSnifferPreferenceNames.PREF_PEAR_LIBRARY, pearLibraryName);
				tabWidth = node.getInt(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_TAB_WITH, tabWidth);
				fileExtensions = node.get(PHPCodeSnifferPreferenceNames.PREF_FILE_EXTENSIONS, fileExtensions);
				printOutput = node.getBoolean(PHPCodeSnifferPreferenceNames.PREF_DEBUG_PRINT_OUTPUT, printOutput);
				ignorePattern = node.get(PHPCodeSnifferPreferenceNames.PREF_IGNORE_PATTERN, ignorePattern);
				ignoreSniffs = node.get(PHPCodeSnifferPreferenceNames.PREF_IGNORE_SNIFFS, ignoreSniffs);
			}
		}

		String[] fileExtensionsList = fileExtensions == null || fileExtensions.length() == 0 ? new String[0]
				: fileExtensions.split(",");

		String[] standards = new String[0];
		if (activeStandards != null) {
			standards = activeStandards.split(";");
		} else if (standard != null) {
			standards = new String[] { standard };
		}

		return new PHPCodeSnifferPreferences(phpExe, printOutput, pearLibraryName, standards, tabWidth,
				fileExtensionsList, ignorePattern, ignoreSniffs == null || ignoreSniffs.length() == 0 ? null
						: ignoreSniffs.split(","));
	}

	protected static IScopeContext[] createPreferenceScopes(IProject project) {
		if (project != null) {
			return new IScopeContext[] { new ProjectScope(project), new InstanceScope(), new DefaultScope() };
		}
		return new IScopeContext[] { new InstanceScope(), new DefaultScope() };
	}
}
