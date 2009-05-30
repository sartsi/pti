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
