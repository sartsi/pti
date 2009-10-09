package org.phpsrc.eclipse.pti.tools.codesniffer.core.problem;

import org.eclipse.dltk.compiler.problem.DefaultProblem;

public class CodeSnifferProblem extends DefaultProblem {

	protected String source;

	public CodeSnifferProblem(String originatingFileName, String message, int id, String[] stringArguments,
			int severity, int startPosition, int endPosition, int line, int column, String source) {
		super(originatingFileName, message, id, stringArguments, severity, startPosition, endPosition, line, column);
		this.source = source;
	}

	public String getSource() {
		return source;
	}
}
