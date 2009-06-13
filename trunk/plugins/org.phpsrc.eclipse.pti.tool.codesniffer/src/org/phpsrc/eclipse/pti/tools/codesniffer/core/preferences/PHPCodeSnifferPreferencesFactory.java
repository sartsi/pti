/*******************************************************************************
 * Copyright (c) 2009, Sven Kiera
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
	public static PHPCodeSnifferPreferences forFile(IFile file) {
		return forProject(file.getProject());
	}

	public static PHPCodeSnifferPreferences forResource(IResource resource) {
		return forProject(resource.getProject());
	}

	public static PHPCodeSnifferPreferences forProject(IProject project) {
		String phpExe;
		boolean printOutput;
		String standard;
		String standardName;
		int tabWidth;

		Preferences prefs = PHPCodeSnifferPlugin.getDefault().getPluginPreferences();

		phpExe = prefs.getString(PHPCodeSnifferPreferenceNames.PREF_PHP_EXECUTABLE);

		// Check first the standard path. Is it not empty we have a custom
		// standard, so we must use the path instead of the name.
		standard = prefs.getString(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_STANDARD_PATH);
		standardName = prefs.getString(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_STANDARD_NAME);
		if (standard == null || standard.equals("")) {
			standard = standardName;
		}

		tabWidth = prefs.getInt(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_TAB_WITH);
		printOutput = prefs.getBoolean(PHPCodeSnifferPreferenceNames.PREF_DEBUG_PRINT_OUTPUT);

		IScopeContext[] preferenceScopes = createPreferenceScopes(project);
		if (preferenceScopes[0] instanceof ProjectScope) {
			IEclipsePreferences node = preferenceScopes[0].getNode(PHPCodeSnifferPlugin.PLUGIN_ID);
			if (node != null) {
				phpExe = node.get(PHPCodeSnifferPreferenceNames.PREF_PHP_EXECUTABLE, phpExe);

				String projectStandard = node.get(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_STANDARD_PATH, null);
				String projectStandardName = node.get(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_STANDARD_NAME, null);
				if (projectStandard == null || projectStandard.equals(""))
					projectStandard = projectStandardName;
				if (projectStandard != null && !projectStandard.equals("")) {
					standard = projectStandard;
					standardName = projectStandardName;
				}

				tabWidth = node.getInt(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_TAB_WITH, tabWidth);
				printOutput = node.getBoolean(PHPCodeSnifferPreferenceNames.PREF_DEBUG_PRINT_OUTPUT, printOutput);
			}
		}

		return new PHPCodeSnifferPreferences(phpExe, printOutput, standard, standardName, tabWidth);
	}

	protected static IScopeContext[] createPreferenceScopes(IProject project) {
		if (project != null) {
			return new IScopeContext[] { new ProjectScope(project), new InstanceScope(), new DefaultScope() };
		}
		return new IScopeContext[] { new InstanceScope(), new DefaultScope() };
	}
}