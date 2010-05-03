/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
	protected final static String ATTR_FILE_SUMMARY_PYRAMID = "ATTR_FILE_SUMMARY_PYRAMID";
	protected final static String ATTR_FILE_JDEPEND_CHART = "ATTR_FILE_JDEPEND_CHART";

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

		String summaryFile = launcher.getAttribute(ATTR_FILE_SUMMARY_XML);
		String jdependChartFile = launcher.getAttribute(ATTR_FILE_JDEPEND_CHART);
		String summaryPyramidFile = launcher.getAttribute(ATTR_FILE_SUMMARY_PYRAMID);
		importMetricRunSession(new File(summaryFile), new File(jdependChartFile), new File(summaryPyramidFile),
				resource);

		return problems.toArray(new IProblem[0]);
	}

	private void importMetricRunSession(final File summaryFile, final File jdependChartFile,
			final File summaryPyramidFile, final IResource resource) {
		UIJob job = new UIJob("Update Metric Runner") {
			public IStatus runInUIThread(IProgressMonitor monitor) {
				try {
					MetricRunSession session = PHPDependModel.importMetricRunSession(summaryFile, jdependChartFile,
							summaryPyramidFile, resource);
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

		String jdependChartFile = summaryFile.toString().replace(TMP_FILE_SUMMARY_XML, TMP_FILE_JDEPEND_CHART);
		String summaryPyramidFile = summaryFile.toString().replace(TMP_FILE_SUMMARY_XML, TMP_FILE_SUMMARY_PYRAMID);

		cmdLineArgs = "--jdepend-chart=" + OperatingSystem.escapeShellFileArg(jdependChartFile) + " " + cmdLineArgs;
		cmdLineArgs = "--overview-pyramid=" + OperatingSystem.escapeShellFileArg(summaryPyramidFile) + " "
				+ cmdLineArgs;

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
		launcher.setAttribute(ATTR_FILE_SUMMARY_PYRAMID, summaryPyramidFile);
		launcher.setAttribute(ATTR_FILE_JDEPEND_CHART, jdependChartFile);

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
