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
package org.phpsrc.eclipse.pti.tools.phpunit.ui.preferences;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.window.Window;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ResourceSelectionDialog;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.phpsrc.eclipse.pti.library.pear.ui.preferences.AbstractPEARPHPToolConfigurationBlock;
import org.phpsrc.eclipse.pti.tools.phpunit.IPHPUnitConstants;
import org.phpsrc.eclipse.pti.tools.phpunit.PHPUnitPlugin;
import org.phpsrc.eclipse.pti.tools.phpunit.core.PHPUnit;

public class PHPUnitConfigurationBlock extends AbstractPEARPHPToolConfigurationBlock {

	private static final Key PREF_PHP_EXECUTABLE = getPHPUnitKey(PHPUnitPreferenceNames.PREF_PHP_EXECUTABLE);
	private static final Key PREF_PEAR_LIBRARY = getPHPUnitKey(PHPUnitPreferenceNames.PREF_PEAR_LIBRARY);
	private static final Key PREF_DEBUG_PRINT_OUTPUT = getPHPUnitKey(PHPUnitPreferenceNames.PREF_DEBUG_PRINT_OUTPUT);
	private static final Key PREF_BOOSTRAP = getPHPUnitKey(PHPUnitPreferenceNames.PREF_BOOTSTRAP);
	private static final Key PREF_TEST_FILE_PATTERN_FOLDER = getPHPUnitKey(PHPUnitPreferenceNames.PREF_TEST_FILE_PATTERN_FOLDER);
	private static final Key PREF_TEST_FILE_PATTERN_FILE = getPHPUnitKey(PHPUnitPreferenceNames.PREF_TEST_FILE_PATTERN_FILE);

	public static final String TEST_FILE_PATTERN_FOLDER_DEFAULT = File.separatorChar
			+ IPHPUnitConstants.TEST_FILE_PATTERN_PLACEHOLDER_PROJECT + File.separatorChar + "tests"
			+ File.separatorChar + IPHPUnitConstants.TEST_FILE_PATTERN_PLACEHOLDER_DIR;
	public static final String TEST_FILE_PATTERN_FILE_DEFAULT = IPHPUnitConstants.TEST_FILE_PATTERN_PLACEHOLDER_FILENAME
			+ "Test." + IPHPUnitConstants.TEST_FILE_PATTERN_PLACEHOLDER_FILE_EXTENSION;

	protected Text fBootstrap;
	protected Button fFileButton;
	protected Text fTestFilePatternFolder;
	protected Text fTestFilePatternFile;

	public PHPUnitConfigurationBlock(IStatusChangeListener context, IProject project,
			IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
	}

	private static Key[] getKeys() {
		return new Key[] { PREF_PHP_EXECUTABLE, PREF_PEAR_LIBRARY, PREF_DEBUG_PRINT_OUTPUT, PREF_BOOSTRAP,
				PREF_TEST_FILE_PATTERN_FOLDER, PREF_TEST_FILE_PATTERN_FILE };
	}

	
	protected Composite createToolContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);

		Group patternGroup = createTestFilePatternGroup(composite);
		patternGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Group optionGroup = createPHPOptionsGroup(composite);
		optionGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		unpackTestFilePattern();
		unpackBootstrap();

		validateSettings(null, null, null);

		return composite;
	}

	private Group createTestFilePatternGroup(Composite folder) {
		final Group testFilePatternGroup = new Group(folder, SWT.RESIZE);
		testFilePatternGroup.setText("Test File Pattern");

		final GridLayout testFilePatternLayout = new GridLayout();
		testFilePatternLayout.numColumns = 3;
		testFilePatternLayout.verticalSpacing = 9;
		testFilePatternGroup.setLayout(testFilePatternLayout);

		Label testFilePatternFolderLabel = new Label(testFilePatternGroup, SWT.NULL);
		testFilePatternFolderLabel.setText("Source Folder:");

		fTestFilePatternFolder = new Text(testFilePatternGroup, SWT.BORDER | SWT.SINGLE);
		fTestFilePatternFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button fTestFilePatternFolderDefaultButton = new Button(testFilePatternGroup, SWT.PUSH);
		fTestFilePatternFolderDefaultButton.setText("Default");
		fTestFilePatternFolderDefaultButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				fTestFilePatternFolder.setText(TEST_FILE_PATTERN_FOLDER_DEFAULT);
			}
		});

		Label testFilePatternFolderInfoLabel = new Label(testFilePatternGroup, SWT.NONE);
		testFilePatternFolderInfoLabel.setText("Use placeholder %p for project and %d for directory.");
		GridData folderInfoData = new GridData(GridData.FILL_HORIZONTAL);
		folderInfoData.horizontalSpan = 3;
		testFilePatternFolderInfoLabel.setLayoutData(folderInfoData);
		makeFontItalic(testFilePatternFolderInfoLabel);

		Label testFilePatternFileLabel = new Label(testFilePatternGroup, SWT.NULL);
		testFilePatternFileLabel.setText("File Name:");

		fTestFilePatternFile = new Text(testFilePatternGroup, SWT.BORDER | SWT.SINGLE);
		fTestFilePatternFile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button fTestFilePatternFileDefaultButton = new Button(testFilePatternGroup, SWT.PUSH);
		fTestFilePatternFileDefaultButton.setText("Default");
		fTestFilePatternFileDefaultButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				fTestFilePatternFile.setText(TEST_FILE_PATTERN_FILE_DEFAULT);
			}
		});

		Label testFilePatternFileInfoLabel = new Label(testFilePatternGroup, SWT.NONE);
		testFilePatternFileInfoLabel
				.setText("Use placeholder %f for filename without extension and %e for file extension.");
		GridData fileInfoData = new GridData(GridData.FILL_HORIZONTAL);
		fileInfoData.horizontalSpan = 3;
		testFilePatternFileInfoLabel.setLayoutData(fileInfoData);
		makeFontItalic(testFilePatternFileInfoLabel);

		return testFilePatternGroup;
	}

	private Group createPHPOptionsGroup(Composite folder) {
		final Group phpUnitOptionsGroup = new Group(folder, SWT.RESIZE);
		phpUnitOptionsGroup.setText("PHPUnit Options");

		final GridLayout phpUnitOptionsLayout = new GridLayout();
		phpUnitOptionsLayout.numColumns = 3;
		phpUnitOptionsLayout.verticalSpacing = 9;
		phpUnitOptionsGroup.setLayout(phpUnitOptionsLayout);

		Label fileLabel = new Label(phpUnitOptionsGroup, SWT.NULL);
		fileLabel.setText("Bootstrap file:");

		fBootstrap = new Text(phpUnitOptionsGroup, SWT.BORDER | SWT.SINGLE);
		fBootstrap.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		fFileButton = new Button(phpUnitOptionsGroup, SWT.PUSH);
		fFileButton.setText("Browse...");
		fFileButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				handleBrowse();
			}
		});

		return phpUnitOptionsGroup;
	}

	private void handleBrowse() {

		final ResourceSelectionDialog dialog = new ResourceSelectionDialog(getShell(), ResourcesPlugin.getWorkspace()
				.getRoot(), "Select Bootstrap File");

		if (dialog.open() == Window.OK) {
			final Object[] result = dialog.getResult();
			if (result.length > 0) {
				fBootstrap.setText(((IFile) result[0]).getFullPath().toOSString());
			}
		}
	}

	
	protected void validateSettings(Key changedKey, String oldValue, String newValue) {
		// TODO Auto-generated method stub
	}

	
	protected boolean processChanges(IWorkbenchPreferenceContainer container) {
		clearProjectLauncherCache(PHPUnit.QUALIFIED_NAME);

		setValue(PREF_BOOSTRAP, fBootstrap.getText());
		setValue(PREF_TEST_FILE_PATTERN_FOLDER, fTestFilePatternFolder.getText());
		setValue(PREF_TEST_FILE_PATTERN_FILE, fTestFilePatternFile.getText());

		return super.processChanges(container);
	}

	
	public void useProjectSpecificSettings(boolean enable) {
		super.useProjectSpecificSettings(enable);
		fBootstrap.setEnabled(enable);
		fFileButton.setEnabled(enable);
	}

	
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		return null;
	}

	protected final static Key getPHPUnitKey(String key) {
		return getKey(PHPUnitPlugin.PLUGIN_ID, key);
	}

	
	protected Key getPHPExecutableKey() {
		return PREF_PHP_EXECUTABLE;
	}

	
	protected Key getDebugPrintOutputKey() {
		return PREF_DEBUG_PRINT_OUTPUT;
	}

	
	protected Key getPEARLibraryKey() {
		return PREF_PEAR_LIBRARY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#
	 * updateControls()
	 */
	
	protected void updateControls() {
		super.updateControls();
		unpackBootstrap();
		unpackTestFilePattern();
	}

	private void unpackBootstrap() {
		String bootstrap = getValue(PREF_BOOSTRAP);
		if (bootstrap != null)
			fBootstrap.setText(bootstrap);
	}

	private void unpackTestFilePattern() {
		String folder = getValue(PREF_TEST_FILE_PATTERN_FOLDER);
		if (folder == null)
			folder = TEST_FILE_PATTERN_FOLDER_DEFAULT;
		fTestFilePatternFolder.setText(folder);

		String file = getValue(PREF_TEST_FILE_PATTERN_FILE);
		if (file == null)
			file = TEST_FILE_PATTERN_FILE_DEFAULT;
		fTestFilePatternFile.setText(file);
	}
}