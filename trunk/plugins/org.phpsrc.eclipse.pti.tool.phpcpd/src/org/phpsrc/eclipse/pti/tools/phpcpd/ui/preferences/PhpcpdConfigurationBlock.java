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
package org.phpsrc.eclipse.pti.tools.phpcpd.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.php.internal.ui.wizards.fields.StringDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.phpsrc.eclipse.pti.library.pear.ui.preferences.AbstractPEARPHPToolConfigurationBlock;
import org.phpsrc.eclipse.pti.tools.phpcpd.PhpcpdPlugin;
import org.phpsrc.eclipse.pti.tools.phpcpd.core.Phpcpd;
import org.phpsrc.eclipse.pti.ui.widgets.listener.NumberOnlyVerifyListener;

public class PhpcpdConfigurationBlock extends AbstractPEARPHPToolConfigurationBlock {

	private static final Key PREF_PHP_EXECUTABLE = getPhpcpdKey(PhpcpdPreferenceNames.PREF_PHP_EXECUTABLE);
	private static final Key PREF_PEAR_LIBRARY = getPhpcpdKey(PhpcpdPreferenceNames.PREF_PEAR_LIBRARY);
	private static final Key PREF_DEBUG_PRINT_OUTPUT = getPhpcpdKey(PhpcpdPreferenceNames.PREF_DEBUG_PRINT_OUTPUT);
	private static final Key PREF_MIN_LINES = getPhpcpdKey(PhpcpdPreferenceNames.PREF_MIN_LINES);
	private static final Key PREF_MIN_TOKENS = getPhpcpdKey(PhpcpdPreferenceNames.PREF_MIN_TOKENS);
	private static final Key PREF_FILE_SUFFIXES = getPhpcpdKey(PhpcpdPreferenceNames.PREF_FILE_SUFFIXES);

	private final StringDialogField fMinLines;
	private final StringDialogField fMinTokens;
	private final StringDialogField fFileSuffixes;

	public PhpcpdConfigurationBlock(IStatusChangeListener context, IProject project,
			IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);

		fMinLines = new StringDialogField();
		fMinLines.setLabelText("Minimum number of identical lines:");
		unpackMinLines();

		fMinTokens = new StringDialogField();
		fMinTokens.setLabelText("Minimum number of identical tokens:");
		unpackMinTokens();

		fFileSuffixes = new StringDialogField();
		fFileSuffixes.setLabelText("File suffixes to check:");
		unpackFileSuffixes();
	}

	private static Key[] getKeys() {
		return new Key[] { PREF_PHP_EXECUTABLE, PREF_PEAR_LIBRARY, PREF_DEBUG_PRINT_OUTPUT, PREF_MIN_LINES,
				PREF_MIN_TOKENS, PREF_FILE_SUFFIXES };
	}

	@Override
	protected Composite createToolContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);

		Composite phpcpdComposite = createPhpcpdTabContent(composite);
		validateSettings(null, null, null);

		return composite;
	}

	private Composite createPhpcpdTabContent(Composite folder) {
		GridLayout conditionsLayout = new GridLayout();
		conditionsLayout.marginHeight = 5;
		conditionsLayout.marginWidth = 0;
		conditionsLayout.numColumns = 3;
		conditionsLayout.marginLeft = 4;
		conditionsLayout.marginRight = 4;

		Group conditionsGroup = new Group(folder, SWT.NULL);
		conditionsGroup.setText("Requirements");
		conditionsGroup.setLayout(conditionsLayout);

		GridData conditionsData = new GridData(GridData.FILL_HORIZONTAL);
		conditionsGroup.setLayoutData(conditionsData);

		fMinLines.doFillIntoGrid(conditionsGroup, 3);
		fMinLines.getTextControl(null).addListener(SWT.Verify, new NumberOnlyVerifyListener());

		fMinTokens.doFillIntoGrid(conditionsGroup, 3);
		fMinTokens.getTextControl(null).addListener(SWT.Verify, new NumberOnlyVerifyListener());

		GridLayout fileSuffixesLayout = new GridLayout();
		fileSuffixesLayout.marginHeight = 5;
		fileSuffixesLayout.marginWidth = 0;
		fileSuffixesLayout.numColumns = 3;
		fileSuffixesLayout.marginLeft = 4;
		fileSuffixesLayout.marginRight = 4;

		Group fileSuffixesGroup = new Group(folder, SWT.NULL);
		fileSuffixesGroup.setText("File suffixes");
		fileSuffixesGroup.setLayout(fileSuffixesLayout);

		GridData fileSuffixesData = new GridData(GridData.FILL_HORIZONTAL);
		fileSuffixesGroup.setLayoutData(fileSuffixesData);

		fFileSuffixes.doFillIntoGrid(fileSuffixesGroup, 3);

		return conditionsGroup;
	}

	private void unpackMinLines() {
		String minLines = getValue(PREF_MIN_LINES);
		if (minLines != null)
			fMinLines.setText(minLines);
		else
			fMinLines.setText("5");
	}

	private void unpackMinTokens() {
		String minTokens = getValue(PREF_MIN_TOKENS);
		if (minTokens != null)
			fMinTokens.setText(minTokens);
		else
			fMinTokens.setText("70");
	}

	private void unpackFileSuffixes() {
		String fileSuffixes = getValue(PREF_FILE_SUFFIXES);
		if (fileSuffixes != null)
			fFileSuffixes.setText(fileSuffixes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#
	 * updateControls()
	 */
	@Override
	protected void updateControls() {
		super.updateControls();
		unpackMinLines();
		unpackMinTokens();
		unpackFileSuffixes();
	}

	@Override
	protected void validateSettings(Key changedKey, String oldValue, String newValue) {
		// TODO Auto-generated method stub
	}

	@Override
	protected boolean processChanges(IWorkbenchPreferenceContainer container) {
		clearProjectLauncherCache(Phpcpd.QUALIFIED_NAME);

		setValue(PREF_MIN_LINES, fMinLines.getText());
		setValue(PREF_MIN_TOKENS, fMinTokens.getText());
		setValue(PREF_FILE_SUFFIXES, fFileSuffixes.getText());

		return super.processChanges(container);
	}

	@Override
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		return null;
	}

	protected final static Key getPhpcpdKey(String key) {
		return getKey(PhpcpdPlugin.PLUGIN_ID, key);
	}

	@Override
	protected Key getPHPExecutableKey() {
		return PREF_PHP_EXECUTABLE;
	}

	@Override
	protected Key getDebugPrintOutputKey() {
		return PREF_DEBUG_PRINT_OUTPUT;
	}

	@Override
	protected Key getPEARLibraryKey() {
		return PREF_PEAR_LIBRARY;
	}
}