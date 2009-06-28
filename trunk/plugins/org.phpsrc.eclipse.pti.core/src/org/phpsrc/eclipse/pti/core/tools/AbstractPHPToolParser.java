package org.phpsrc.eclipse.pti.core.tools;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.phpsrc.eclipse.pti.core.launching.PHPToolLauncher;
import org.phpsrc.eclipse.pti.core.php.source.ISourceFile;
import org.phpsrc.eclipse.pti.core.php.source.PHPSourceFile;

public abstract class AbstractPHPToolParser extends AbstractPHPTool {
	public IProblem[] parse(IFile file) throws CoreException, IOException {
		return parseOutput(new PHPSourceFile(file), launchFile(file));
	}

	private String launchFile(IFile file) {
		PHPToolLauncher launcher = getPHPToolLauncher(file.getProject());
		String output = launcher.launch(file);
		if (output == null)
			return "";
		else
			return output;
	}

	protected abstract PHPToolLauncher getPHPToolLauncher(IProject project);

	protected abstract IProblem[] parseOutput(ISourceFile file, String output);
}
