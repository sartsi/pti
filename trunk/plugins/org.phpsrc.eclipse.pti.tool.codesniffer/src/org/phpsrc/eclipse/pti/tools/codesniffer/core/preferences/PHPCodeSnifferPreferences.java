/*******************************************************************************
 * Copyright (c) 2010, Sven Kiera
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

package org.phpsrc.eclipse.pti.tools.codesniffer.core.preferences;

import org.phpsrc.eclipse.pti.library.pear.core.preferences.AbstractPEARPHPToolPreferences;

public class PHPCodeSnifferPreferences extends AbstractPEARPHPToolPreferences {
	protected String standard;
	protected String standardName;
	protected int tabWidth;
	protected String ignorePattern;
	protected String[] ignoreSniffs;

	public PHPCodeSnifferPreferences(String phpExecutable, boolean printOutput, String pearLibraryName,
			String standard, String standardName, int tabWidth, String ignorePattern, String[] ignoreSniffs) {
		super(phpExecutable, printOutput, pearLibraryName);
		this.standard = standard;
		this.standardName = standardName;
		this.tabWidth = tabWidth;
		if (ignorePattern != null && ignorePattern.length() > 0)
			this.ignorePattern = ignorePattern;
		else
			this.ignorePattern = null;
		this.ignoreSniffs = ignoreSniffs;
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

	public boolean isCustom() {
		return !standard.equals(standardName);
	}

	public String getIgnorePattern() {
		return ignorePattern;
	}

	public String[] getIgnoreSniffs() {
		return ignoreSniffs;
	}
}