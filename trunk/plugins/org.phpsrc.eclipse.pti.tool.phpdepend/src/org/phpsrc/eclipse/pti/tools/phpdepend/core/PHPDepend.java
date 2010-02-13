/*******************************************************************************
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
package org.phpsrc.eclipse.pti.tools.phpdepend.core;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.phpsrc.eclipse.pti.core.launching.OperatingSystem;
import org.phpsrc.eclipse.pti.core.launching.PHPToolLauncher;
import org.phpsrc.eclipse.pti.core.php.inifile.INIFileEntry;
import org.phpsrc.eclipse.pti.core.php.inifile.INIFileUtil;
import org.phpsrc.eclipse.pti.core.tools.AbstractPHPTool;
import org.phpsrc.eclipse.pti.tools.phpdepend.PHPDependPlugin;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.PHPDependPreferences;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.PHPDependPreferencesFactory;

public class PHPDepend extends AbstractPHPTool {

	public final static QualifiedName QUALIFIED_NAME = new QualifiedName(PHPDependPlugin.PLUGIN_ID, "phpdepend");
	private static PHPDepend instance;

	protected PHPDepend() {
	}

	public static PHPDepend getInstance() {
		if (instance == null)
			instance = new PHPDepend();

		return instance;
	}

	protected IProblem[] parseOutput(IProject project, String output) {
		ArrayList<IProblem> problems = new ArrayList<IProblem>();

		if (output != null && output.length() > 0) {
			System.out.println(output);
		}

		return problems.toArray(new IProblem[0]);
	}

	public IProblem[] validateResource(IResource folder) {
		String cmdLineArgs = OperatingSystem.escapeShellFileArg(folder.getLocation().toOSString());
		PHPToolLauncher launcher = getProjectPHPToolLauncher(folder.getProject(), cmdLineArgs, folder.getLocation());
		return parseOutput(folder.getProject(), launcher.launch(folder.getProject()));
	}

	private PHPToolLauncher getProjectPHPToolLauncher(IProject project, String cmdLineArgs, IPath fileIncludePath) {

		PHPDependPreferences prefs = PHPDependPreferencesFactory.factory(project);

		PHPToolLauncher launcher = new PHPToolLauncher(getPHPExecutable(prefs.getPhpExecutable()), getScriptFile(),
				cmdLineArgs, getPHPINIEntries(project, fileIncludePath));

		launcher.setPrintOuput(prefs.isPrintOutput());

		return launcher;
	}

	private INIFileEntry[] getPHPINIEntries(IProject project) {
		IPath[] includePaths = PHPDependPlugin.getDefault().getPluginIncludePaths(project);
		return getPHPINIEntries(includePaths);
	}

	private INIFileEntry[] getPHPINIEntries(IProject project, IPath fileIncludePath) {
		IPath[] pluginIncludePaths = PHPDependPlugin.getDefault().getPluginIncludePaths(project);

		IPath[] includePaths = new IPath[pluginIncludePaths.length + 1];
		System.arraycopy(pluginIncludePaths, 0, includePaths, 0, pluginIncludePaths.length);
		includePaths[includePaths.length - 1] = fileIncludePath;

		return getPHPINIEntries(includePaths);
	}

	private INIFileEntry[] getPHPINIEntries(IPath[] includePaths) {

		INIFileEntry[] entries;
		if (includePaths.length > 0) {
			entries = new INIFileEntry[] { INIFileUtil.createIncludePathEntry(includePaths) };
		} else {
			entries = new INIFileEntry[0];
		}

		return entries;
	}

	public static IPath getScriptFile() {
		return PHPDependPlugin.getDefault().resolvePluginResource("/php/tools/pdepend.php");
	}
}
