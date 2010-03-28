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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.ui.progress.UIJob;
import org.phpsrc.eclipse.pti.core.launching.OperatingSystem;
import org.phpsrc.eclipse.pti.core.launching.PHPToolLauncher;
import org.phpsrc.eclipse.pti.core.php.inifile.INIFileEntry;
import org.phpsrc.eclipse.pti.core.php.inifile.INIFileUtil;
import org.phpsrc.eclipse.pti.core.tools.AbstractPHPTool;
import org.phpsrc.eclipse.pti.tools.phpdepend.PHPDependPlugin;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricRunSession;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.PHPDependModel;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.PHPDependPreferences;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.PHPDependPreferencesFactory;
import org.phpsrc.eclipse.pti.ui.Logger;

public class PHPDepend extends AbstractPHPTool {

	public final static QualifiedName QUALIFIED_NAME = new QualifiedName(PHPDependPlugin.PLUGIN_ID, "phpdepend");
	protected final static String TMP_FILE_SUMMARY_XML = "summary.xml";
	protected final static String TMP_FILE_SUMMARY_PYRAMID = "pyramid.svg";
	protected final static String TMP_FILE_JDEPEND_CHART = "jdepend.svg";
	protected final static String ATTR_FILE_SUMMARY_XML = "ATTR_FILE_SUMMARY_XML";

	private static PHPDepend instance;

	protected PHPDepend() {
	}

	public static PHPDepend getInstance() {
		if (instance == null)
			instance = new PHPDepend();

		return instance;
	}

	protected IProblem[] parseOutput(IResource resource, PHPToolLauncher launcher, String output) {
		ArrayList<IProblem> problems = new ArrayList<IProblem>();

		if (output != null && output.length() > 0) {
		}

		String summaryFile = launcher.getAttribute(ATTR_FILE_SUMMARY_XML);
		importMetricRunSession(new File(summaryFile), resource);

		return problems.toArray(new IProblem[0]);
	}

	private void importMetricRunSession(final File summaryFile, final IResource resource) {
		UIJob job = new UIJob("Update Metric Runner") {
			public IStatus runInUIThread(IProgressMonitor monitor) {
				try {
					MetricRunSession session = PHPDependModel.importMetricRunSession(summaryFile, resource);
					notifyResultListener(session);
				} catch (CoreException e) {
					e.printStackTrace();
					Logger.logException(e);
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	public IProblem[] validateResource(IResource resource) {
		String cmdLineArgs = OperatingSystem.escapeShellFileArg(resource.getLocation().toOSString());
		try {
			PHPToolLauncher launcher = getProjectPHPToolLauncher(resource.getProject(), cmdLineArgs, resource
					.getLocation());
			return parseOutput(resource, launcher, launcher.launch(resource.getProject()));
		} catch (IOException e) {
			Logger.logException(e);
		}
		return new IProblem[0];
	}

	private PHPToolLauncher getProjectPHPToolLauncher(IProject project, String cmdLineArgs, IPath fileIncludePath)
			throws IOException {

		PHPDependPreferences prefs = PHPDependPreferencesFactory.factory(project);

		File tempDir = new File(System.getProperty("java.io.tmpdir"), "pti_php_depend"); //$NON-NLS-2$
		if (!tempDir.exists()) {
			tempDir.mkdir();
			tempDir.deleteOnExit();
		}

		File summaryFile = new File(tempDir, TMP_FILE_SUMMARY_XML);
		if (summaryFile.exists())
			summaryFile.delete();
		summaryFile.createNewFile();
		summaryFile.deleteOnExit();

		cmdLineArgs = "--summary-xml=" + OperatingSystem.escapeShellFileArg(summaryFile.toString()) + " " + cmdLineArgs;

		cmdLineArgs = "--jdepend-chart="
				+ OperatingSystem.escapeShellFileArg(summaryFile.toString().replace(TMP_FILE_SUMMARY_XML,
						TMP_FILE_JDEPEND_CHART)) + " " + cmdLineArgs;
		cmdLineArgs = "--overview-pyramid="
				+ OperatingSystem.escapeShellFileArg(summaryFile.toString().replace(TMP_FILE_SUMMARY_XML,
						TMP_FILE_SUMMARY_PYRAMID)) + " " + cmdLineArgs;

		if (prefs.badDocumentation)
			cmdLineArgs = "--bad-documentation " + cmdLineArgs;
		if (prefs.withoutAnnotations)
			cmdLineArgs = "--without-annotations " + cmdLineArgs;
		if (prefs.coderankMode != null && !"".equals(prefs.coderankMode))
			cmdLineArgs = "--coderank-mode=" + OperatingSystem.escapeShellArg(prefs.coderankMode) + " " + cmdLineArgs;
		if (prefs.optimization != null && !"".equals(prefs.optimization))
			cmdLineArgs = "--optimization=" + OperatingSystem.escapeShellArg(prefs.optimization) + " " + cmdLineArgs;
		if (prefs.validFileExtensions != null && !"".equals(prefs.validFileExtensions))
			cmdLineArgs = "--suffix=" + OperatingSystem.escapeShellArg(prefs.validFileExtensions) + " " + cmdLineArgs;
		if (prefs.excludePackages != null && !"".equals(prefs.excludePackages))
			cmdLineArgs = "--exclude=" + OperatingSystem.escapeShellArg(prefs.excludePackages) + " " + cmdLineArgs;

		PHPToolLauncher launcher = new PHPToolLauncher(QUALIFIED_NAME, getPHPExecutable(prefs.getPhpExecutable()),
				getScriptFile(), cmdLineArgs, getPHPINIEntries(project, fileIncludePath));

		launcher.setAttribute(ATTR_FILE_SUMMARY_XML, summaryFile.toString());

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
