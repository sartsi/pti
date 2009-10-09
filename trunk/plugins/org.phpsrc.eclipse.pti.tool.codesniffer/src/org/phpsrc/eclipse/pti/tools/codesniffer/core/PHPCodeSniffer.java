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
package org.phpsrc.eclipse.pti.tools.codesniffer.core;

import java.io.StringReader;
import java.util.ArrayList;

import org.apache.xerces.parsers.DOMParser;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.ProblemSeverities;
import org.phpsrc.eclipse.pti.core.launching.OperatingSystem;
import org.phpsrc.eclipse.pti.core.launching.PHPToolLauncher;
import org.phpsrc.eclipse.pti.core.php.inifile.INIFileEntry;
import org.phpsrc.eclipse.pti.core.php.inifile.INIFileUtil;
import org.phpsrc.eclipse.pti.core.php.source.ISourceFile;
import org.phpsrc.eclipse.pti.core.tools.AbstractPHPToolParser;
import org.phpsrc.eclipse.pti.tools.codesniffer.PHPCodeSnifferPlugin;
import org.phpsrc.eclipse.pti.tools.codesniffer.core.preferences.PHPCodeSnifferPreferences;
import org.phpsrc.eclipse.pti.tools.codesniffer.core.preferences.PHPCodeSnifferPreferencesFactory;
import org.phpsrc.eclipse.pti.tools.codesniffer.core.problem.CodeSnifferProblem;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class PHPCodeSniffer extends AbstractPHPToolParser {

	public final static QualifiedName QUALIFIED_NAME = new QualifiedName(PHPCodeSnifferPlugin.PLUGIN_ID,
			"phpCodeSnifferTool");
	private static PHPCodeSniffer instance;

	protected PHPCodeSniffer() {
	}

	public static PHPCodeSniffer getInstance() {
		if (instance == null)
			instance = new PHPCodeSniffer();

		return instance;
	}

	@Override
	protected IProblem[] parseOutput(ISourceFile file, String output) {
		ArrayList<IProblem> problems = new ArrayList<IProblem>();

		try {
			if (output.length() > 0) {
				PHPCodeSnifferPreferences prefs = PHPCodeSnifferPreferencesFactory.factory(file.getFile());

				int tabWidth = 0;
				if (prefs != null)
					tabWidth = prefs.getTabWidth();

				int xmlStart = output.indexOf("<?xml");
				if (xmlStart >= 0) {
					output = output.substring(xmlStart).trim();

					DOMParser parser = new DOMParser();
					parser.parse(new InputSource(new StringReader(output)));

					Document doc = parser.getDocument();
					problems.addAll(createProblemMarker(file, doc.getElementsByTagName("error"),
							ProblemSeverities.Error, tabWidth));
					problems.addAll(createProblemMarker(file, doc.getElementsByTagName("warning"),
							ProblemSeverities.Warning, tabWidth));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return problems.toArray(new IProblem[0]);
	}

	protected ArrayList<IProblem> createProblemMarker(ISourceFile file, NodeList list, int type, int tabWidth) {
		ArrayList<IProblem> problems = new ArrayList<IProblem>();

		int length = list.getLength();
		for (int i = 0; i < length; i++) {
			Node item = list.item(i);

			NamedNodeMap attr = item.getAttributes();

			int lineNr = Integer.parseInt(attr.getNamedItem("line").getTextContent());
			int column = Integer.parseInt(attr.getNamedItem("column").getTextContent());
			String source = attr.getNamedItem("source").getTextContent();
			problems.add(new CodeSnifferProblem(file.toString(), item.getTextContent(), IProblem.Syntax, new String[0],
					type, file.lineStart(lineNr), file.lineEnd(lineNr), lineNr, column, source));
		}

		return problems;
	}

	@Override
	protected PHPToolLauncher getPHPToolLauncher(IProject project) {
		PHPToolLauncher launcher;
		try {
			launcher = (PHPToolLauncher) project.getSessionProperty(QUALIFIED_NAME);
			if (launcher != null)
				return launcher;
		} catch (CoreException e) {
			e.printStackTrace();
		}

		launcher = getProjectPHPToolLauncher(project);

		try {
			project.setSessionProperty(QUALIFIED_NAME, launcher);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return launcher;
	}

	private PHPToolLauncher getProjectPHPToolLauncher(IProject project) {

		PHPCodeSnifferPreferences prefs = PHPCodeSnifferPreferencesFactory.factory(project);

		PHPToolLauncher launcher = new PHPToolLauncher(getPHPExecutable(prefs.getPhpExecutable()), getScriptFile(),
				getCommandLineArgs(prefs.getStandard(), prefs.getTabWidth()), getPHPINIEntries(prefs));

		launcher.setPrintOuput(prefs.isPrintOutput());

		return launcher;
	}

	private INIFileEntry[] getPHPINIEntries(PHPCodeSnifferPreferences prefs) {

		IPath[] includePaths = PHPCodeSnifferPlugin.getDefault().getPluginIncludePaths();

		if (prefs.isCustom()) {
			IPath[] tmpIncludePaths = new IPath[includePaths.length + 2];
			System.arraycopy(includePaths, 0, tmpIncludePaths, 2, includePaths.length);
			tmpIncludePaths[0] = new Path(prefs.getStandard());
			tmpIncludePaths[1] = new Path(prefs.getStandard()).removeLastSegments(1);

			includePaths = tmpIncludePaths;
		}

		INIFileEntry[] entries;
		if (includePaths.length > 0) {
			entries = new INIFileEntry[] { INIFileUtil.createIncludePathEntry(includePaths) };
		} else {
			entries = new INIFileEntry[0];
		}

		return entries;
	}

	private IPath getScriptFile() {
		return PHPCodeSnifferPlugin.getDefault().resolvePluginResource("/php/tools/phpcs.php");
	}

	private String getCommandLineArgs(String standard, int tabWidth) {

		String args = "--report=xml --standard=" + OperatingSystem.escapeShellFileArg(standard);

		if (tabWidth > 0)
			args += " --tab-width=" + tabWidth;

		return args + " " + PHPToolLauncher.COMMANDLINE_PLACEHOLDER_FILE;
	}
}
