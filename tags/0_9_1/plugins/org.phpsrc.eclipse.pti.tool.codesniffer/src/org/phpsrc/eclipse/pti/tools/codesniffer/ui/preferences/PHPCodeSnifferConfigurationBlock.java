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
package org.phpsrc.eclipse.pti.tools.codesniffer.ui.preferences;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.php.internal.core.project.PHPNature;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.php.internal.ui.util.PixelConverter;
import org.eclipse.php.internal.ui.wizards.fields.DialogField;
import org.eclipse.php.internal.ui.wizards.fields.IDialogFieldListener;
import org.eclipse.php.internal.ui.wizards.fields.IListAdapter;
import org.eclipse.php.internal.ui.wizards.fields.ListDialogField;
import org.eclipse.php.internal.ui.wizards.fields.StringDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.phpsrc.eclipse.pti.tools.codesniffer.PHPCodeSnifferPlugin;
import org.phpsrc.eclipse.pti.tools.codesniffer.core.PHPCodeSniffer;
import org.phpsrc.eclipse.pti.ui.preferences.AbstractPHPToolConfigurationBlock;

public class PHPCodeSnifferConfigurationBlock extends AbstractPHPToolConfigurationBlock {

	private static final Key PREF_PHP_EXECUTABLE = getCodeSnifferKey(PHPCodeSnifferPreferenceNames.PREF_PHP_EXECUTABLE);
	private static final Key PREF_DEBUG_PRINT_OUTPUT = getCodeSnifferKey(PHPCodeSnifferPreferenceNames.PREF_DEBUG_PRINT_OUTPUT);
	private static final Key PREF_CUSTOM_STANDARD_NAMES = getCodeSnifferKey(PHPCodeSnifferPreferenceNames.PREF_CUSTOM_STANDARD_NAMES);
	private static final Key PREF_CUSTOM_STANDARD_PATHS = getCodeSnifferKey(PHPCodeSnifferPreferenceNames.PREF_CUSTOM_STANDARD_PATHS);
	private static final Key PREF_DEFAULT_STANDARD_NAME = getCodeSnifferKey(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_STANDARD_NAME);
	private static final Key PREF_DEFAULT_STANDARD_PATH = getCodeSnifferKey(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_STANDARD_PATH);
	private static final Key PREF_DEFAULT_TAB_WITH = getCodeSnifferKey(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_TAB_WITH);

	private static final int IDX_ADD = 0;
	private static final int IDX_EDIT = 1;
	private static final int IDX_REMOVE = 2;
	private static final int IDX_DEFAULT = 4;

	private final ListDialogField<Standard> fStandardsList;
	private final StringDialogField fTabWidth;

	public static class Standard {
		public String name;
		public boolean custom;
		public String path;

		@Override
		public String toString() {
			return name;
		}
	}

	private class CodeSnifferLabelProvider extends LabelProvider implements ITableLabelProvider, IFontProvider {

		public CodeSnifferLabelProvider() {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		@Override
		public Image getImage(Object element) {
			return null; // JavaPluginImages.get(JavaPluginImages.IMG_OBJS_REFACTORING_INFO);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			return getColumnText(element, 0);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java
		 * .lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.
		 * lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			Standard standard = (Standard) element;
			if (columnIndex == 0) {
				String name = standard.name;
				if (isDefaultStandard(standard)) {
					name = MessageFormat.format("{0} (default)", new Object[] { name });
				}
				return name;
			} else if (columnIndex == 1) {
				return standard.custom ? "yes" : "no";
			} else if (columnIndex == 2) {
				return standard.path;
			} else {
				return "";
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IFontProvider#getFont(java.lang.Object)
		 */
		public Font getFont(Object element) {
			if (isDefaultStandard((Standard) element)) {
				return JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT);
			}
			return null;
		}
	}

	private class StandardAdapter implements IListAdapter<Object>, IDialogFieldListener {

		private boolean canEdit(List<Object> selectedElements) {
			return selectedElements.size() == 1 && ((Standard) selectedElements.get(0)).custom;
		}

		private boolean canRemove(List<Object> selectedElements) {
			int count = selectedElements.size();

			if (count == 0)
				return false;

			for (int i = 0; i < count; i++) {
				if (!((Standard) selectedElements.get(i)).custom)
					return false;
			}

			return true;
		}

		private boolean canSetToDefault(List<Object> selectedElements) {
			return selectedElements.size() == 1 && !isDefaultStandard((Standard) selectedElements.get(0));
		}

		public void customButtonPressed(ListDialogField<Object> field, int index) {
			doStandardButtonPressed(index);
		}

		public void selectionChanged(ListDialogField<Object> field) {
			List<Object> selectedElements = field.getSelectedElements();
			field.enableButton(IDX_EDIT, canEdit(selectedElements));
			field.enableButton(IDX_DEFAULT, canSetToDefault(selectedElements));
			field.enableButton(IDX_REMOVE, canRemove(selectedElements));
		}

		public void doubleClicked(ListDialogField<Object> field) {
			if (canEdit(field.getSelectedElements())) {
				doStandardButtonPressed(IDX_EDIT);
			}
		}

		public void dialogFieldChanged(DialogField field) {
			updateModel(field);
		}

	}

	public PHPCodeSnifferConfigurationBlock(IStatusChangeListener context, IProject project,
			IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);

		StandardAdapter adapter = new StandardAdapter();

		String[] buttons = new String[] { "New...", "Edit...", "Remove", null, "Default", };

		fStandardsList = new ListDialogField<Standard>(adapter, buttons, new CodeSnifferLabelProvider());
		fStandardsList.setDialogFieldListener(adapter);
		fStandardsList.setRemoveButtonIndex(IDX_REMOVE);

		String[] columnsHeaders = new String[] { "Name", "Custom", "Path" };

		fStandardsList.setTableColumns(new ListDialogField.ColumnsDescription(columnsHeaders, true));
		fStandardsList.setViewerSorter(new ViewerSorter());

		unpackStandards();

		if (fStandardsList.getSize() > 0) {
			fStandardsList.selectFirstElement();
		} else {
			fStandardsList.enableButton(IDX_EDIT, false);
			fStandardsList.enableButton(IDX_DEFAULT, false);
		}

		fTabWidth = new StringDialogField();
		fTabWidth.setLabelText("Tab width:");

		unpackTabWidth();
	}

	private static Key[] getKeys() {
		return new Key[] { PREF_PHP_EXECUTABLE, PREF_CUSTOM_STANDARD_NAMES, PREF_CUSTOM_STANDARD_PATHS,
				PREF_DEFAULT_STANDARD_NAME, PREF_DEFAULT_STANDARD_PATH, PREF_DEFAULT_TAB_WITH };
	}

	@Override
	protected Composite createToolContents(Composite parent) {
		Composite standardsComposite = createStandardsTabContent(parent);
		validateSettings(null, null, null);

		return standardsComposite;
	}

	private Composite createStandardsTabContent(Composite folder) {

		PixelConverter conv = new PixelConverter(folder);

		GridLayout markersLayout = new GridLayout();
		markersLayout.marginHeight = 5;
		markersLayout.marginWidth = 0;
		markersLayout.numColumns = 3;

		Group markersGroup = new Group(folder, SWT.NULL);
		markersGroup.setText("CodeSniffer Standards");
		markersGroup.setLayout(markersLayout);
		markersGroup.setFont(folder.getFont());

		GridData listData = new GridData(GridData.FILL_BOTH);
		listData.widthHint = conv.convertWidthInCharsToPixels(50);
		Control listControl = fStandardsList.getListControl(markersGroup);
		listControl.setLayoutData(listData);

		Control buttonsControl = fStandardsList.getButtonBox(markersGroup);
		buttonsControl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));

		GridLayout tabWidthLayout = new GridLayout();
		tabWidthLayout.marginHeight = 5;
		tabWidthLayout.marginWidth = 0;
		tabWidthLayout.numColumns = 3;
		tabWidthLayout.marginLeft = 4;
		tabWidthLayout.marginRight = 4;

		Group tabWidthGroup = new Group(folder, SWT.NULL);
		tabWidthGroup.setText("Standard Tab Width");
		tabWidthGroup.setLayout(tabWidthLayout);

		GridData tabWidthData = new GridData(GridData.FILL_HORIZONTAL);
		tabWidthGroup.setLayoutData(tabWidthData);

		fTabWidth.doFillIntoGrid(tabWidthGroup, 3);

		return markersGroup;
	}

	final boolean isDefaultStandard(Standard standard) {
		return fStandardsList.getIndexOfElement(standard) == 0;
	}

	@Override
	protected void validateSettings(Key changedKey, String oldValue, String newValue) {
		// TODO Auto-generated method stub
	}

	private void doStandardButtonPressed(int index) {
		Standard edited = null;
		if (index != IDX_ADD) {
			edited = fStandardsList.getSelectedElements().get(0);
		}
		if (index == IDX_ADD || index == IDX_EDIT) {
			StandardInputDialog dialog = new StandardInputDialog(getShell(), edited, fStandardsList.getElements());
			if (dialog.open() == Window.OK) {
				if (edited != null) {
					fStandardsList.replaceElement(edited, dialog.getResult());
				} else {
					fStandardsList.addElement(dialog.getResult());
				}
			}
		} else if (index == IDX_DEFAULT) {
			setToDefaultStandard(edited);
		}
	}

	protected final void updateModel(DialogField field) {
		if (field == fStandardsList) {
			StringBuffer customStandards = new StringBuffer();
			StringBuffer customPaths = new StringBuffer();

			List<Standard> list = fStandardsList.getElements();
			for (int i = 0; i < list.size(); i++) {
				Standard elem = list.get(i);
				if (elem.custom) {
					if (customStandards.length() > 0) {
						customStandards.append(';');
						customPaths.append(';');
					}

					customStandards.append(elem.name);
					customPaths.append(elem.path);
				}
			}

			Standard defaultStandard = (Standard) fStandardsList.getElement(0);
			if (defaultStandard != null) {
				setValue(PREF_DEFAULT_STANDARD_NAME, defaultStandard.name);
				setValue(PREF_DEFAULT_STANDARD_PATH, defaultStandard.path);
			}
			setValue(PREF_CUSTOM_STANDARD_NAMES, customStandards.toString());
			setValue(PREF_CUSTOM_STANDARD_PATHS, customPaths.toString());

			validateSettings(PREF_CUSTOM_STANDARD_NAMES, null, null);
		}
	}

	@Override
	protected boolean processChanges(IWorkbenchPreferenceContainer container) {
		clearProjectLauncherCache();

		int tabWidth = 0;
		try {
			tabWidth = Integer.parseInt(fTabWidth.getText());
		} catch (Exception e) {
		}
		setValue(PREF_DEFAULT_TAB_WITH, "" + tabWidth);

		return super.processChanges(container);
	}

	private void clearProjectLauncherCache() {
		IWorkspace root = ResourcesPlugin.getWorkspace();
		IProject[] projects = root.getRoot().getProjects();
		for (IProject project : projects) {
			try {
				IProjectNature nature = project.getNature(PHPNature.ID);
				if (nature != null) {
					project.setSessionProperty(PHPCodeSniffer.QUALIFIED_NAME, null);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	private void setToDefaultStandard(Standard standard) {
		List<Standard> elements = fStandardsList.getElements();
		elements.remove(standard);
		elements.add(0, standard);
		fStandardsList.setElements(elements);
		fStandardsList.enableButton(IDX_DEFAULT, false);
	}

	@Override
	public void useProjectSpecificSettings(boolean enable) {
		super.useProjectSpecificSettings(enable);
		fStandardsList.setEnabled(enable);
	}

	@Override
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		String title = "CodeSniffer Settings Changed";
		String message;
		if (fProject == null) {
			message = "The settings have changed. A full rebuild is required for changes to take effect. Execute the full build now?";
		} else {
			message = "The settings have changed. A rebuild of the project is required for changes to take effect. Build the project now?";
		}
		return new String[] { title, message };
	}

	protected final static Key getCodeSnifferKey(String key) {
		return getKey(PHPCodeSnifferPlugin.PLUGIN_ID, key);
	}

	@Override
	protected Key getPHPExecutableKey() {
		return PREF_PHP_EXECUTABLE;
	}

	@Override
	protected Key getDebugPrintOutputKey() {
		return PREF_DEBUG_PRINT_OUTPUT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#
	 * updateControls()
	 */
	@Override
	protected void updateControls() {
		unpackStandards();
		unpackTabWidth();
	}

	private void unpackTabWidth() {
		String tabWidth = getValue(PREF_DEFAULT_TAB_WITH);
		if (tabWidth != null)
			fTabWidth.setText(tabWidth);
	}

	private void unpackStandards() {
		String defaultName = getValue(PREF_DEFAULT_STANDARD_NAME);
		Standard defaultStandard = null;

		String customStandardPrefs = getValue(PREF_CUSTOM_STANDARD_NAMES);

		String[] standards = PHPCodeSnifferPlugin.getDefault().getCodeSnifferStandards();
		String[] customStandards = {};
		String[] customPaths = {};

		if (customStandardPrefs != null) {
			customStandards = getTokens(customStandardPrefs, ";"); //$NON-NLS-1$

			String customPathPrefs = getValue(PREF_CUSTOM_STANDARD_PATHS);
			customPaths = getTokens(customPathPrefs, ";"); //$NON-NLS-1$
		}

		ArrayList<Standard> elements = new ArrayList<Standard>(standards.length + customStandards.length);

		// CodeSniffer own standards
		for (int i = 0; i < standards.length; i++) {
			Standard standard = new Standard();
			standard.name = standards[i].trim();
			standard.custom = false;
			standard.path = "";

			elements.add(standard);

			if (standard.name.equals(defaultName))
				defaultStandard = standard;
		}

		// Custom standards
		for (int i = 0; i < customStandards.length; i++) {
			Standard standard = new Standard();
			standard.name = customStandards[i].trim();
			standard.custom = true;
			standard.path = customPaths[i].trim();

			elements.add(standard);

			if (standard.name.equals(defaultName))
				defaultStandard = standard;
		}

		fStandardsList.setElements(elements);

		if (defaultStandard != null)
			setToDefaultStandard(defaultStandard);
	}
}