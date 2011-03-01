/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.php.internal.ui.util.PixelConverter;
import org.eclipse.php.internal.ui.wizards.fields.DialogField;
import org.eclipse.php.internal.ui.wizards.fields.SelectionButtonDialogField;
import org.eclipse.php.internal.ui.wizards.fields.StringDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.phpsrc.eclipse.pti.library.pear.ui.preferences.AbstractPEARPHPToolConfigurationBlock;
import org.phpsrc.eclipse.pti.tools.phpdepend.PHPDependPlugin;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.PHPDepend;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.PHPDependPreferenceNames;

public class PHPDependConfigurationBlock extends AbstractPEARPHPToolConfigurationBlock {

	private static final Key PREF_PHP_EXECUTABLE = getPHPDependKey(PHPDependPreferenceNames.PREF_PHPDEPEND_PHP_EXECUTABLE);
	private static final Key PREF_PEAR_LIBRARY = getPHPDependKey(PHPDependPreferenceNames.PREF_PHPDEPEND_PEAR_LIBRARY);
	private static final Key PREF_DEBUG_PRINT_OUTPUT = getPHPDependKey(PHPDependPreferenceNames.PREF_PHPDEPEND_DEBUG_PRINT_OUTPUT);
	private static final Key PREF_WITHOUT_ANNOTATIONS = getPHPDependKey(PHPDependPreferenceNames.PREF_PHPDEPEND_WITHOUT_ANNOTATIONS);
	private static final Key PREF_BAD_DOCUMENTATION = getPHPDependKey(PHPDependPreferenceNames.PREF_PHPDEPEND_BAD_DOCUMENTATION);
	private static final Key PREF_CODERANK_MODE = getPHPDependKey(PHPDependPreferenceNames.PREF_PHPDEPEND_CODERANK_MODE);
	private static final Key PREF_OPTIMIZATION = getPHPDependKey(PHPDependPreferenceNames.PREF_PHPDEPEND_OPTIMIZATION);
	private static final Key PREF_VALID_FILE_EXTENSIONS = getPHPDependKey(PHPDependPreferenceNames.PREF_PHPDEPEND_VALID_FILE_EXTENSIONS);
	private static final Key PREF_EXCLUDE_PACKAGES = getPHPDependKey(PHPDependPreferenceNames.PREF_PHPDEPEND_EXCLUDE_PACKAGES);

	protected final SelectionButtonDialogField fWithoutAnnotations;
	protected final SelectionButtonDialogField fBadDocumentation;
	protected final StringDialogField fCoderankMode;
	protected final SelectionButtonDialogField fOptimizationBest;
	protected final SelectionButtonDialogField fOptimizationNone;
	protected final StringDialogField fValidFileExtenions;
	protected final StringDialogField fExcludePackages;

	public PHPDependConfigurationBlock(IStatusChangeListener context, IProject project,
			IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);

		fWithoutAnnotations = new SelectionButtonDialogField(SWT.CHECK);
		fWithoutAnnotations.setLabelText("Without Annotations");

		fBadDocumentation = new SelectionButtonDialogField(SWT.CHECK);
		fBadDocumentation.setLabelText("Bad Documentation");

		fCoderankMode = new StringDialogField();
		fCoderankMode.setLabelText("CodeRank strategies:");

		fOptimizationBest = new SelectionButtonDialogField(SWT.RADIO);
		fOptimizationBest
				.setLabelText("best (Provides lowest memory usage with best possible performance)");

		fOptimizationNone = new SelectionButtonDialogField(SWT.RADIO);
		fOptimizationNone.setLabelText("none (Highest memory usage without any caching)");

		fValidFileExtenions = new StringDialogField();
		fValidFileExtenions.setLabelText("Valid PHP file extensions:");

		fExcludePackages = new StringDialogField();
		fExcludePackages.setLabelText("Exclude packages:");

		unpackOptions();
	}

	private static Key[] getKeys() {
		return new Key[] { PREF_PHP_EXECUTABLE, PREF_PEAR_LIBRARY, PREF_DEBUG_PRINT_OUTPUT,
				PREF_WITHOUT_ANNOTATIONS, PREF_BAD_DOCUMENTATION, PREF_CODERANK_MODE,
				PREF_OPTIMIZATION, PREF_VALID_FILE_EXTENSIONS, PREF_EXCLUDE_PACKAGES };
	}

	protected Composite createToolContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		PixelConverter conv = new PixelConverter(composite);

		createDialogFieldsWithInfoText(composite, new DialogField[] { fWithoutAnnotations,
				fBadDocumentation }, "Options", null);

		createDialogFieldsWithInfoText(
				composite,
				new DialogField[] { fCoderankMode },
				"CodeRank",
				new String[] { "Comma separated list of 'inheritance'(default), 'property' and 'method'" });

		createDialogFieldsWithInfoText(composite, new DialogField[] { fOptimizationNone,
				fOptimizationBest }, "Optimization", null);

		createDialogFieldsWithInfoText(composite, new DialogField[] { fValidFileExtenions,
				fExcludePackages }, "Filter", new String[] { "Extensions are separated by a comma",
				"Packages are separated by a comma" });

		return composite;
	}

	protected void validateSettings(Key changedKey, String oldValue, String newValue) {
		// TODO Auto-generated method stub
	}

	protected boolean processChanges(IWorkbenchPreferenceContainer container) {
		clearProjectLauncherCache(PHPDepend.QUALIFIED_NAME);

		setValue(PREF_WITHOUT_ANNOTATIONS, fWithoutAnnotations.isSelected());
		setValue(PREF_BAD_DOCUMENTATION, fBadDocumentation.isSelected());
		setValue(PREF_CODERANK_MODE, fCoderankMode.getText());
		setValue(PREF_OPTIMIZATION, fOptimizationBest.isSelected() ? "best" : "none");
		setValue(PREF_VALID_FILE_EXTENSIONS, fValidFileExtenions.getText());
		setValue(PREF_EXCLUDE_PACKAGES, fExcludePackages.getText());

		return super.processChanges(container);
	}

	protected void unpackOptions() {
		fWithoutAnnotations.setSelection(getBooleanValue(PREF_WITHOUT_ANNOTATIONS));
		fBadDocumentation.setSelection(getBooleanValue(PREF_BAD_DOCUMENTATION));
		unpackPrefValue(fCoderankMode, PREF_CODERANK_MODE);
		unpackPrefValue(fValidFileExtenions, PREF_VALID_FILE_EXTENSIONS);
		unpackPrefValue(fExcludePackages, PREF_EXCLUDE_PACKAGES);
		String optimization = getValue(PREF_OPTIMIZATION);
		if (optimization == null || "".equals(optimization) || "none".equals(optimization)) {
			fOptimizationNone.setSelection(true);
			fOptimizationBest.setSelection(false);
		} else {
			fOptimizationNone.setSelection(false);
			fOptimizationBest.setSelection(true);
		}
	}

	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		return null;
	}

	protected final static Key getPHPDependKey(String key) {
		return getKey(PHPDependPlugin.PLUGIN_ID, key);
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
}