package org.phpsrc.eclipse.pti.tools.codesniffer.core.preferences;

import org.phpsrc.eclipse.pti.core.preferences.AbstractPHPToolPreferences;

public class PHPCodeSnifferPreferences extends AbstractPHPToolPreferences {
	protected String standard;
	protected String standardName;
	protected int tabWidth;

	public PHPCodeSnifferPreferences(String phpExecutable, boolean printOutput, String standard, String standardName,
			int tabWidth) {
		super(phpExecutable, printOutput);
		this.standard = standard;
		this.standardName = standardName;
		this.tabWidth = tabWidth;
	}

	public String getStandard() {
		return standard;
	}

	public String getStandardName() {
		return standardName;
	}

	public int getTabWidth() {
		return tabWidth;
	}
}