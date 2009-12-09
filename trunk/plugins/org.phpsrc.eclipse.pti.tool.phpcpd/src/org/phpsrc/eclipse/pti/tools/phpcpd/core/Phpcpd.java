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
package org.phpsrc.eclipse.pti.tools.phpcpd.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.ProblemSeverities;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.phpsrc.eclipse.pti.core.compiler.problem.FileProblem;
import org.phpsrc.eclipse.pti.core.launching.OperatingSystem;
import org.phpsrc.eclipse.pti.core.launching.PHPToolLauncher;
import org.phpsrc.eclipse.pti.core.php.inifile.INIFileEntry;
import org.phpsrc.eclipse.pti.core.php.inifile.INIFileUtil;
import org.phpsrc.eclipse.pti.core.php.source.PHPSourceFile;
import org.phpsrc.eclipse.pti.core.tools.AbstractPHPTool;
import org.phpsrc.eclipse.pti.tools.phpcpd.PhpcpdPlugin;
import org.phpsrc.eclipse.pti.tools.phpcpd.core.preferences.PhpcpdPreferences;
import org.phpsrc.eclipse.pti.tools.phpcpd.core.preferences.PhpcpdPreferencesFactory;

public class Phpcpd extends AbstractPHPTool {

	public final static QualifiedName QUALIFIED_NAME = new QualifiedName(PhpcpdPlugin.PLUGIN_ID, "phpcpd");
	private static Phpcpd instance;

	protected Phpcpd() {
	}

	public static Phpcpd getInstance() {
		if (instance == null)
			instance = new Phpcpd();

		return instance;
	}

	protected IProblem[] parseOutput(IProject project, String output) {
		ArrayList<IProblem> problems = new ArrayList<IProblem>();

		if (output != null && output.length() > 0) {
			String statusMsg = "";

			Pattern pFile = Pattern.compile("-?(.*):([0-9]+)-([0-9]+)");

			String[] lines = output.split("\n");
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i].trim();

				if (line.startsWith("-")) {
					try {
						Matcher m1 = pFile.matcher(line);
						Matcher m2 = pFile.matcher(lines[i + 1].trim());
						if (m1.matches() && m2.matches()) {
							problems.addAll(createDuplicateMarker(project, m1.group(1).trim(), Integer.parseInt(m1
									.group(2)), Integer.parseInt(m1.group(3)), m2.group(1).trim(), Integer.parseInt(m2
									.group(2)), Integer.parseInt(m2.group(3))));
							++i;
						}
					} catch (CoreException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (i == lines.length - 1) {
					statusMsg += line;
				} else if (line.startsWith("Found")) {
					statusMsg += line.substring(0, line.length() - 1) + ".\n";
				}
			}

			displayStatus(statusMsg);
		}

		return problems.toArray(new IProblem[0]);
	}

	private Collection<IProblem> createDuplicateMarker(IProject project, String file1, int lineStart1, int lineEnd1,
			String file2, int lineStart2, int lineEnd2) throws CoreException, IOException {
		String projectLocation = project.getLocation().toOSString();

		IResource rFile1 = project.findMember(file1.substring(projectLocation.length()));
		IResource rFile2 = project.findMember(file2.substring(projectLocation.length()));
		if (rFile1 == null || !rFile1.exists() || rFile2 == null || !rFile2.exists())
			throw new CoreException(Status.CANCEL_STATUS);

		PHPSourceFile sourceFile1 = new PHPSourceFile((IFile) rFile1);
		PHPSourceFile sourceFile2 = new PHPSourceFile((IFile) rFile2);

		ArrayList<IProblem> problems = new ArrayList<IProblem>();

		problems.add(new FileProblem((IFile) rFile1, "Copy/Paste Detection: " + rFile2.getFullPath().toPortableString()
				+ " (" + lineStart1 + "-" + lineEnd1 + ")", IProblem.Task, new String[0], ProblemSeverities.Error,
				sourceFile1.lineStart(lineStart1), sourceFile1.lineEnd(lineEnd1), lineStart1));
		problems.add(new FileProblem((IFile) rFile2, "Copy/Paste Detection: " + rFile1.getFullPath().toPortableString()
				+ " (" + lineStart2 + "-" + lineEnd2 + ")", IProblem.Task, new String[0], ProblemSeverities.Error,
				sourceFile2.lineStart(lineStart2), sourceFile2.lineEnd(lineEnd2), lineStart2));

		return problems;
	}

	private void displayStatus(final String message) {
		final Display display = Display.getDefault();
		display.asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(display.getActiveShell(), "PHP Copy/Paste Detection", message);
			}
		});
	}

	public IProblem[] validateResource(IResource folder) {
		String cmdLineArgs = OperatingSystem.escapeShellFileArg(folder.getLocation().toOSString());
		PHPToolLauncher launcher = getProjectPHPToolLauncher(folder.getProject(), cmdLineArgs, folder.getLocation());
		return parseOutput(folder.getProject(), launcher.launch(folder.getProject()));
	}

	private PHPToolLauncher getProjectPHPToolLauncher(IProject project, String cmdLineArgs, IPath fileIncludePath) {

		PhpcpdPreferences prefs = PhpcpdPreferencesFactory.factory(project);

		PHPToolLauncher launcher = new PHPToolLauncher(getPHPExecutable(prefs.getPhpExecutable()), getScriptFile(),
				cmdLineArgs, getPHPINIEntries(project, fileIncludePath));

		launcher.setPrintOuput(prefs.isPrintOutput());

		return launcher;
	}

	private INIFileEntry[] getPHPINIEntries(IProject project) {
		IPath[] includePaths = PhpcpdPlugin.getDefault().getPluginIncludePaths(project);
		return getPHPINIEntries(includePaths);
	}

	private INIFileEntry[] getPHPINIEntries(IProject project, IPath fileIncludePath) {
		IPath[] pluginIncludePaths = PhpcpdPlugin.getDefault().getPluginIncludePaths(project);

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
		return PhpcpdPlugin.getDefault().resolvePluginResource("/php/tools/phpcpd.php");
	}
}
