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

package org.phpsrc.eclipse.pti.library.pear.core.preferences;

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
import org.phpsrc.eclipse.pti.library.pear.PHPLibraryPEARPlugin;
import org.phpsrc.eclipse.pti.library.pear.ui.preferences.PEARPreferenceNames;

public class PEARPreferencesFactory {
	public static PEARPreferences factory(IFile file) {
		return factory(file.getProject());
	}

	public static PEARPreferences factory(IResource resource) {
		return factory(resource.getProject());
	}

	public static PEARPreferences factory(IProject project) {
		Preferences prefs = PHPLibraryPEARPlugin.getDefault().getPluginPreferences();

		// Check first the standard path. Is it not empty we have a custom
		// standard, so we must use the path instead of the name.
		String libPath = prefs.getString(PEARPreferenceNames.PREF_DEFAULT_LIBRARY_PATH);
		String libName = prefs.getString(PEARPreferenceNames.PREF_DEFAULT_LIBRARY_NAME);

		IScopeContext[] preferenceScopes = createPreferenceScopes(project);
		if (preferenceScopes[0] instanceof ProjectScope) {
			IEclipsePreferences node = preferenceScopes[0].getNode(PHPLibraryPEARPlugin.PLUGIN_ID);
			if (node != null) {
				String projectLibPath = node.get(PEARPreferenceNames.PREF_DEFAULT_LIBRARY_PATH, null);
				String projectLibName = node.get(PEARPreferenceNames.PREF_DEFAULT_LIBRARY_NAME, null);

				if (projectLibPath != null && !projectLibPath.equals("")) {
					libPath = projectLibPath;
					libName = projectLibName;
				}
			}
		}

		return new PEARPreferences(libName, libPath);
	}

	public static PEARPreferences factoryByName(String name) {
		if (name != null && !"".equals(name) && !name.startsWith("<")) {
			for (PEARPreferences prefs : getAll()) {
				if (prefs.getLibraryName().equals(name))
					return prefs;
			}
		}

		return null;
	}

	protected static IScopeContext[] createPreferenceScopes(IProject project) {
		if (project != null) {
			return new IScopeContext[] { new ProjectScope(project), new InstanceScope(), new DefaultScope() };
		}
		return new IScopeContext[] { new InstanceScope(), new DefaultScope() };
	}

	public static PEARPreferences[] getAll() {
		Preferences prefs = PHPLibraryPEARPlugin.getDefault().getPluginPreferences();
		String libPath = prefs.getString(PEARPreferenceNames.PREF_CUSTOM_LIBRARY_PATHS);
		String libName = prefs.getString(PEARPreferenceNames.PREF_CUSTOM_LIBRARY_NAMES);

		ArrayList<PEARPreferences> items = new ArrayList<PEARPreferences>(1);
		items.add(new PEARPreferences("<Internal>", ""));

		if (libPath != null && libName != null && libPath.length() > 0 && libName.length() > 0) {
			String[] libPaths = libPath.split(";");
			String[] libNames = libName.split(";");

			for (int i = 0; i < libPaths.length; i++) {
				items.add(new PEARPreferences(libNames[i], libPaths[i]));
			}
		}

		return items.toArray(new PEARPreferences[0]);
	}
}
