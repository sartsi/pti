package org.phpsrc.eclipse.pti.core.preferences;

public abstract class AbstractPHPToolPreferences {
	protected String phpExecutable;
	protected boolean printOutput;

	protected AbstractPHPToolPreferences(String phpExecutable, boolean printOutput) {
		this.phpExecutable = phpExecutable;
		this.printOutput = printOutput;
	}

	public String getPhpExecutable() {
		return phpExecutable;
	}

	public boolean isPrintOutput() {
		return printOutput;
	}
}
