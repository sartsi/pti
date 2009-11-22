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
package org.phpsrc.eclipse.pti.library.pear.ui.preferences;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.OptionsConfigurationBlock;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.php.internal.ui.util.PixelConverter;
import org.eclipse.php.internal.ui.wizards.fields.DialogField;
import org.eclipse.php.internal.ui.wizards.fields.IDialogFieldListener;
import org.eclipse.php.internal.ui.wizards.fields.IListAdapter;
import org.eclipse.php.internal.ui.wizards.fields.ListDialogField;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.phpsrc.eclipse.pti.library.pear.PHPLibraryPEARPlugin;

public class PEARConfigurationBlock extends OptionsConfigurationBlock {

	private static final Key PREF_DEFAULT_LIBRARY_NAME = getPEARLibraryKey(PEARPreferenceNames.PREF_DEFAULT_LIBRARY_NAME);
	private static final Key PREF_DEFAULT_LIBRARY_PATH = getPEARLibraryKey(PEARPreferenceNames.PREF_DEFAULT_LIBRARY_PATH);
	private static final Key PREF_CUSTOM_LIBRARY_NAMES = getPEARLibraryKey(PEARPreferenceNames.PREF_CUSTOM_LIBRARY_NAMES);
	private static final Key PREF_CUSTOM_LIBRARY_PATHS = getPEARLibraryKey(PEARPreferenceNames.PREF_CUSTOM_LIBRARY_PATHS);

	private static final int IDX_ADD = 0;
	private static final int IDX_EDIT = 1;
	private static final int IDX_REMOVE = 2;
	private static final int IDX_DEFAULT = 4;
	private static final int IDX_VERSIONS = 6;

	private final ListDialogField<Library> fLibraryList;

	public static class Library {
		public String name;
		public boolean custom;
		public String path;

		@Override
		public String toString() {
			return name;
		}
	}

	private class PEARLabelProvider extends LabelProvider implements ITableLabelProvider, IFontProvider {

		public PEARLabelProvider() {
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
			Library lib = (Library) element;
			if (columnIndex == 0) {
				String name = lib.name;
				if (isDefaultLibrary(lib)) {
					name = MessageFormat.format("{0} (default)", new Object[] { name });
				}
				return name;
			} else if (columnIndex == 1) {
				return lib.path;
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
			if (isDefaultLibrary((Library) element)) {
				return JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT);
			}
			return null;
		}
	}

	private class LibraryListAdapter implements IListAdapter<Object>, IDialogFieldListener {

		private boolean canEdit(List<Object> selectedElements) {
			return selectedElements.size() == 1 && !isInternalLibrary((Library) selectedElements.get(0));
		}

		private boolean canRemove(List<Object> selectedElements) {
			int count = selectedElements.size();

			if (count == 0)
				return false;

			for (int i = 0; i < count; i++) {
				if (isInternalLibrary((Library) selectedElements.get(i)))
					return false;
			}

			return true;
		}

		private boolean canSetToDefault(List<Object> selectedElements) {
			return selectedElements.size() == 1 && !isDefaultLibrary((Library) selectedElements.get(0));
		}

		private boolean canShowVersions(List<Object> selectedElements) {
			return selectedElements.size() == 1 && isInternalLibrary((Library) selectedElements.get(0));
		}

		public void customButtonPressed(ListDialogField<Object> field, int index) {
			doStandardButtonPressed(index);
		}

		public void selectionChanged(ListDialogField<Object> field) {
			List<Object> selectedElements = field.getSelectedElements();
			field.enableButton(IDX_EDIT, canEdit(selectedElements));
			field.enableButton(IDX_DEFAULT, canSetToDefault(selectedElements));
			field.enableButton(IDX_REMOVE, canRemove(selectedElements));
			field.enableButton(IDX_VERSIONS, canShowVersions(selectedElements));
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

	public PEARConfigurationBlock(IStatusChangeListener context, IProject project,
			IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);

		LibraryListAdapter adapter = new LibraryListAdapter();

		String[] buttons = new String[] { "New...", "Edit...", "Remove", null, "Default", null, "Versions" };

		fLibraryList = new ListDialogField<Library>(adapter, buttons, new PEARLabelProvider());
		fLibraryList.setDialogFieldListener(adapter);
		fLibraryList.setRemoveButtonIndex(IDX_REMOVE);

		String[] columnsHeaders = new String[] { "Name", "Path" };

		fLibraryList.setTableColumns(new ListDialogField.ColumnsDescription(columnsHeaders, true));
		fLibraryList.setViewerSorter(new ViewerSorter());

		unpackLibraries();

		if (fLibraryList.getSize() > 0) {
			fLibraryList.selectFirstElement();
		} else {
			fLibraryList.enableButton(IDX_EDIT, false);
			fLibraryList.enableButton(IDX_DEFAULT, false);
		}
	}

	private static Key[] getKeys() {
		return new Key[] { PREF_CUSTOM_LIBRARY_NAMES, PREF_CUSTOM_LIBRARY_PATHS, PREF_DEFAULT_LIBRARY_NAME };
	}

	@Override
	protected Control createContents(Composite parent) {
		PixelConverter conv = new PixelConverter(parent);

		GridLayout markersLayout = new GridLayout();
		markersLayout.marginHeight = 5;
		markersLayout.marginWidth = 0;
		markersLayout.numColumns = 3;
		parent.setLayout(markersLayout);

		GridData listData = new GridData(GridData.FILL_BOTH);
		listData.widthHint = conv.convertWidthInCharsToPixels(50);
		Control listControl = fLibraryList.getListControl(parent);
		listControl.setLayoutData(listData);

		Control buttonsControl = fLibraryList.getButtonBox(parent);
		buttonsControl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));

		GridLayout tabWidthLayout = new GridLayout();
		tabWidthLayout.marginHeight = 5;
		tabWidthLayout.marginWidth = 0;
		tabWidthLayout.numColumns = 3;
		tabWidthLayout.marginLeft = 4;
		tabWidthLayout.marginRight = 4;

		return buttonsControl;
	}

	final boolean isDefaultLibrary(Library lib) {
		return fLibraryList.getIndexOfElement(lib) == 0;
	}

	final boolean isInternalLibrary(Library lib) {
		return lib.path == "";
	}

	@Override
	protected void validateSettings(Key changedKey, String oldValue, String newValue) {
		// TODO Auto-generated method stub
	}

	private void doStandardButtonPressed(int index) {
		Library edited = null;
		if (index != IDX_ADD) {
			edited = fLibraryList.getSelectedElements().get(0);
		}
		if (index == IDX_ADD || index == IDX_EDIT) {
			LibraryInputDialog dialog = new LibraryInputDialog(getShell(), edited, fLibraryList.getElements());
			if (dialog.open() == Window.OK) {
				if (edited != null) {
					fLibraryList.replaceElement(edited, dialog.getResult());
				} else {
					fLibraryList.addElement(dialog.getResult());
				}
			}
		} else if (index == IDX_DEFAULT) {
			setToDefaultLibrary(edited);
		} else if (index == IDX_VERSIONS) {
			InternalLibraryVersionsDialog dialog = new InternalLibraryVersionsDialog(getShell());
			dialog.open();
		}
	}

	protected final void updateModel(DialogField field) {
		if (field == fLibraryList) {
			StringBuffer customLibs = new StringBuffer();
			StringBuffer customPaths = new StringBuffer();

			List<Library> list = fLibraryList.getElements();
			for (int i = 0; i < list.size(); i++) {
				Library elem = list.get(i);
				if (elem.custom) {
					if (customLibs.length() > 0) {
						customLibs.append(';');
						customPaths.append(';');
					}

					customLibs.append(elem.name);
					customPaths.append(elem.path);
				}
			}

			Library defaultLibrary = (Library) fLibraryList.getElement(0);
			if (defaultLibrary != null) {
				setValue(PREF_DEFAULT_LIBRARY_NAME, defaultLibrary.name);
				setValue(PREF_DEFAULT_LIBRARY_PATH, defaultLibrary.path);
			}
			setValue(PREF_CUSTOM_LIBRARY_NAMES, customLibs.toString());
			setValue(PREF_CUSTOM_LIBRARY_PATHS, customPaths.toString());

			validateSettings(PREF_CUSTOM_LIBRARY_NAMES, null, null);
		}
	}

	private void setToDefaultLibrary(Library lib) {
		List<Library> elements = fLibraryList.getElements();
		elements.remove(lib);
		elements.add(0, lib);
		fLibraryList.setElements(elements);
		fLibraryList.enableButton(IDX_DEFAULT, false);
	}

	@Override
	public void useProjectSpecificSettings(boolean enable) {
		super.useProjectSpecificSettings(enable);
		fLibraryList.setEnabled(enable);
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

	protected final static Key getPEARLibraryKey(String key) {
		return getKey(PHPLibraryPEARPlugin.PLUGIN_ID, key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#
	 * updateControls()
	 */
	@Override
	protected void updateControls() {
		unpackLibraries();
	}

	private void unpackLibraries() {
		String defaultName = getValue(PREF_DEFAULT_LIBRARY_NAME);
		Library defaultLibrary = null;

		String customStandardPrefs = getValue(PREF_CUSTOM_LIBRARY_NAMES);

		String[] customStandards = {};
		String[] customPaths = {};

		if (customStandardPrefs != null) {
			customStandards = getTokens(customStandardPrefs, ";"); //$NON-NLS-1$

			String customPathPrefs = getValue(PREF_CUSTOM_LIBRARY_PATHS);
			customPaths = getTokens(customPathPrefs, ";"); //$NON-NLS-1$
		}

		ArrayList<Library> elements = new ArrayList<Library>(customStandards.length + 1);

		// internal lib
		Library lib = new Library();
		lib.name = "<Internal>";
		lib.custom = false;
		lib.path = "";

		elements.add(lib);

		if (lib.name.equals(defaultName))
			defaultLibrary = lib;

		// Custom libs
		for (int i = 0; i < customStandards.length; i++) {
			lib = new Library();
			lib.name = customStandards[i].trim();
			lib.custom = true;
			lib.path = customPaths[i].trim();

			elements.add(lib);

			if (lib.name.equals(defaultName))
				defaultLibrary = lib;
		}

		fLibraryList.setElements(elements);

		if (defaultLibrary != null)
			setToDefaultLibrary(defaultLibrary);
	}
}