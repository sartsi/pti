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
package org.phpsrc.eclipse.pti.tools.phpunit.core;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.ProblemSeverities;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.SearchMatch;
import org.phpsrc.eclipse.pti.core.PHPToolkitUtil;
import org.phpsrc.eclipse.pti.core.launching.OperatingSystem;
import org.phpsrc.eclipse.pti.core.launching.PHPToolLauncher;
import org.phpsrc.eclipse.pti.core.php.inifile.INIFileEntry;
import org.phpsrc.eclipse.pti.core.php.inifile.INIFileUtil;
import org.phpsrc.eclipse.pti.core.php.source.PHPSourceFile;
import org.phpsrc.eclipse.pti.core.search.PHPSearchEngine;
import org.phpsrc.eclipse.pti.core.tools.AbstractPHPTool;
import org.phpsrc.eclipse.pti.tools.phpunit.PHPUnitPlugin;
import org.phpsrc.eclipse.pti.tools.phpunit.core.preferences.PHPUnitPreferences;
import org.phpsrc.eclipse.pti.tools.phpunit.core.preferences.PHPUnitPreferencesFactory;

public class PHPUnit extends AbstractPHPTool {

	public final static QualifiedName QUALIFIED_NAME = new QualifiedName(PHPUnitPlugin.PLUGIN_ID, "PHPUnit");
	private static PHPUnit instance;

	protected PHPUnit() {
	}

	public static PHPUnit getInstance() {
		if (instance == null)
			instance = new PHPUnit();

		return instance;
	}

	public boolean createTestSkeleton(String className, IFile classFile, String testClassFilePath)
			throws InvalidObjectException, CoreException {
		Path path = new Path(testClassFilePath);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		IProject project = file.getProject();
		if (project == null)
			throw new InvalidObjectException("no project found");

		IFolder folder = (IFolder) file.getParent();
		createFolder(folder);

		String cmdLineArgs = "--skeleton-test " + className;
		cmdLineArgs += " " + OperatingSystem.escapeShellFileArg(classFile.getLocation().toOSString());
		cmdLineArgs += " " + className + "Test";
		cmdLineArgs += " " + OperatingSystem.escapeShellFileArg(file.getLocation().toOSString());

		PHPToolLauncher launcher = getProjectPHPToolLauncher(project, cmdLineArgs, classFile.getParent().getLocation());
		String output = launcher.launch(project);

		folder.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

		return (output.indexOf("Wrote skeleton for ") >= 0 ? true : false);
	}

	public IProblem[] runTestCase(IFile testFile) {

		ArrayList<IProblem> problems = new ArrayList<IProblem>();

		try {
			PHPSourceFile sourceFile = new PHPSourceFile(testFile);

			ISourceModule module = PHPToolkitUtil.getSourceModule(testFile);
			IType[] types = module.getAllTypes();
			for (IType type : types) {
				String cmdLineArgs = type.getElementName();
				cmdLineArgs += " " + OperatingSystem.escapeShellFileArg(testFile.getLocation().toOSString());

				PHPToolLauncher launcher = getProjectPHPToolLauncher(testFile.getProject(), cmdLineArgs, testFile
						.getParent().getLocation());

				String output = launcher.launch(testFile.getProject());
				if (output != null && output.length() > 0) {
					Pattern pFailed = Pattern.compile("(Failed .*)");

					String[] lines = output.split("\n");
					for (int i = 0; i < lines.length; i++) {
						Matcher m = pFailed.matcher(lines[i].trim());
						if (m.matches()) {
							String msg = lines[i].trim();

							String lineFailureLocation = lines[i + 2];
							if (lineFailureLocation.lastIndexOf(":") != -1) {
								int lineNumber = Integer.parseInt(lineFailureLocation.substring(lineFailureLocation
										.lastIndexOf(":") + 1));
								problems.add(new DefaultProblem(testFile.getFullPath().toOSString(), msg,
										IProblem.Task, new String[0], ProblemSeverities.Error, sourceFile
												.lineStart(lineNumber), sourceFile.lineEnd(lineNumber), lineNumber));

								++i;
							}
						}
					}
				}
			}
		} catch (ModelException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return problems.toArray(new IProblem[0]);
	}

	private void createFolder(IFolder folder) throws CoreException {
		IContainer parent = folder.getParent();
		if (parent instanceof IFolder) {
			createFolder((IFolder) parent);
		}
		if (!folder.exists()) {
			folder.create(true, true, new NullProgressMonitor());
		}
	}

	private PHPToolLauncher getProjectPHPToolLauncher(IProject project, String cmdLineArgs, IPath fileIncludePath) {

		PHPUnitPreferences prefs = PHPUnitPreferencesFactory.factory(project);

		String bootstrap = prefs.getBootstrap();
		if (bootstrap != null && bootstrap.length() > 0) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IResource resource = root.findMember(bootstrap);
			if (resource.exists()) {
				cmdLineArgs = "--bootstrap " + OperatingSystem.escapeShellFileArg(resource.getLocation().toOSString())
						+ " " + cmdLineArgs;
			}
		}

		PHPToolLauncher launcher = new PHPToolLauncher(getPHPExecutable(prefs.getPhpExecutable()), getScriptFile(),
				cmdLineArgs, getPHPINIEntries(fileIncludePath));

		launcher.setPrintOuput(prefs.isPrintOutput());

		return launcher;
	}

	private INIFileEntry[] getPHPINIEntries() {
		IPath[] includePaths = PHPUnitPlugin.getDefault().getPluginIncludePaths();
		return getPHPINIEntries(includePaths);
	}

	private INIFileEntry[] getPHPINIEntries(IPath fileIncludePath) {
		IPath[] pluginIncludePaths = PHPUnitPlugin.getDefault().getPluginIncludePaths();

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

	private IPath getScriptFile() {
		return PHPUnitPlugin.getDefault().resolvePluginResource("/php/tools/phpunit.php");
	}

	static public IFile searchTestCase(IFile file) {
		ISourceModule module = PHPToolkitUtil.getSourceModule(file);

		IType[] types;
		try {
			types = module.getAllTypes();
			if (types.length > 0) {
				String[] classes = types[0].getSuperClasses();
				if (classes.length > 0 && classes[0].equals("PHPUnit_Framework_TestCase")) {
					return file;
				} else {
					SearchMatch[] matches = PHPSearchEngine.findClass(types[0].getElementName() + "Test",
							PHPSearchEngine.createProjectScope(file.getProject()));

					if (matches.length > 0)
						return (IFile) matches[0].getResource();
				}
			}
		} catch (ModelException e) {
			e.printStackTrace();
		}

		return null;
	}
}
