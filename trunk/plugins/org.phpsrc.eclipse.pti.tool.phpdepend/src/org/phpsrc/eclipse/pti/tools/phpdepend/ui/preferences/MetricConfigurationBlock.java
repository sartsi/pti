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
package org.phpsrc.eclipse.pti.tools.phpdepend.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.OptionsConfigurationBlock;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.php.internal.ui.util.PixelConverter;
import org.eclipse.php.internal.ui.wizards.fields.CheckedListDialogField;
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
import org.phpsrc.eclipse.pti.tools.phpdepend.PHPDependPlugin;
import org.phpsrc.eclipse.pti.tools.phpdepend.preferences.Metric;

public class MetricConfigurationBlock extends OptionsConfigurationBlock {
	private static final Key PREF_METRICS_ENABLED = getMetricKey(PHPDependPreferenceNames.PREF_METRICS_ENABLED);
	private static final Key PREF_METRICS_NAMES = getMetricKey(PHPDependPreferenceNames.PREF_METRICS_NAMES);
	private static final Key PREF_METRICS_IDS = getMetricKey(PHPDependPreferenceNames.PREF_METRICS_IDS);
	private static final Key PREF_METRICS_WARNINGS = getMetricKey(PHPDependPreferenceNames.PREF_METRICS_WARNINGS);
	private static final Key PREF_METRICS_ERRORS = getMetricKey(PHPDependPreferenceNames.PREF_METRICS_ERRORS);

	private static final int IDX_ADD = 0;
	private static final int IDX_EDIT = 1;
	private static final int IDX_REMOVE = 2;

	private final CheckedListDialogField fMetricList;

	private class MetricLabelProvider extends LabelProvider implements ITableLabelProvider, IFontProvider {

		public MetricLabelProvider() {
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
			Metric m = (Metric) element;
			if (columnIndex == 0) {
				return m.name;
			} else if (columnIndex == 1) {
				return m.id;
			} else if (columnIndex == 2) {
				return (m.warningCompare + " " + m.warningLevel).trim();
			} else if (columnIndex == 3) {
				return (m.errorCompare + " " + m.errorLevel).trim();
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
			return null;
		}
	}

	private class MetricListAdapter implements IListAdapter<Object>, IDialogFieldListener {

		private boolean canEdit(List<Object> selectedElements) {
			return true;
		}

		private boolean canRemove(List<Object> selectedElements) {
			int count = selectedElements.size();

			if (count == 0)
				return false;

			return true;
		}

		public void customButtonPressed(ListDialogField<Object> field, int index) {
			doStandardButtonPressed(index);
		}

		public void selectionChanged(ListDialogField<Object> field) {
			List<Object> selectedElements = field.getSelectedElements();
			field.enableButton(IDX_EDIT, canEdit(selectedElements));
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

	public MetricConfigurationBlock(IStatusChangeListener context, IProject project,
			IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);

		MetricListAdapter adapter = new MetricListAdapter();

		String[] buttons = new String[] { "New...", "Edit...", "Remove" };

		fMetricList = new CheckedListDialogField(adapter, buttons, new MetricLabelProvider());
		fMetricList.setDialogFieldListener(adapter);
		fMetricList.setRemoveButtonIndex(IDX_REMOVE);

		String[] columnsHeaders = new String[] { "Name", "Id", "Warning", "Error" };

		fMetricList.setTableColumns(new ListDialogField.ColumnsDescription(columnsHeaders, true));
		fMetricList.setViewerSorter(new ViewerSorter());

		unpackMetrics();

		if (fMetricList.getSize() > 0) {
			fMetricList.selectFirstElement();
		} else {
			fMetricList.enableButton(IDX_EDIT, false);
		}
	}

	private static Key[] getKeys() {
		return new Key[] {};
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
		Control listControl = fMetricList.getListControl(parent);
		listControl.setLayoutData(listData);

		Control buttonsControl = fMetricList.getButtonBox(parent);
		buttonsControl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));

		GridLayout tabWidthLayout = new GridLayout();
		tabWidthLayout.marginHeight = 5;
		tabWidthLayout.marginWidth = 0;
		tabWidthLayout.numColumns = 3;
		tabWidthLayout.marginLeft = 4;
		tabWidthLayout.marginRight = 4;

		return buttonsControl;
	}

	final boolean isDefaultLibrary(Metric lib) {
		return fMetricList.getIndexOfElement(lib) == 0;
	}

	@Override
	protected void validateSettings(Key changedKey, String oldValue, String newValue) {
		// TODO Auto-generated method stub
	}

	private void doStandardButtonPressed(int index) {
		Metric edited = null;
		if (index != IDX_ADD) {
			edited = (Metric) fMetricList.getSelectedElements().get(0);
		}
		if (index == IDX_ADD || index == IDX_EDIT) {
			MetricInputDialog dialog = new MetricInputDialog(getShell(), edited, fMetricList.getElements());
			if (dialog.open() == Window.OK) {
				if (edited != null) {
					fMetricList.replaceElement(edited, dialog.getResult());
				} else {
					fMetricList.addElement(dialog.getResult());
				}
			}
		}
	}

	protected final void updateModel(DialogField field) {
		if (field == fMetricList) {

			StringBuffer metricIds = new StringBuffer();
			StringBuffer metricNames = new StringBuffer();
			StringBuffer metricEnabled = new StringBuffer();
			StringBuffer metricWarnings = new StringBuffer();
			StringBuffer metricErrors = new StringBuffer();

			List<Metric> list = fMetricList.getElements();
			for (int i = 0; i < list.size(); i++) {
				Metric elem = list.get(i);
				if (i > 0) {
					metricIds.append(';');
					metricNames.append(';');
					metricEnabled.append(';');
					metricWarnings.append(';');
					metricErrors.append(';');
				}

				metricIds.append(elem.id);
				metricNames.append(elem.name);
				metricEnabled.append(elem.enabled ? '1' : '0');
				metricWarnings.append(elem.warningCompare + ',' + elem.warningLevel);
				metricErrors.append(elem.errorCompare + ',' + elem.errorLevel);
			}

			setValue(PREF_METRICS_IDS, metricIds.toString());
			setValue(PREF_METRICS_NAMES, metricNames.toString());
			setValue(PREF_METRICS_ENABLED, metricEnabled.toString());
			setValue(PREF_METRICS_WARNINGS, metricWarnings.toString());
			setValue(PREF_METRICS_ERRORS, metricErrors.toString());

			// validateSettings(PREF_METRICS, null, null);
		}
	}

	private void setToDefaultLibrary(Metric m) {
		List<Metric> elements = fMetricList.getElements();
		elements.remove(m);
		elements.add(0, m);
		fMetricList.setElements(elements);
	}

	@Override
	public void useProjectSpecificSettings(boolean enable) {
		super.useProjectSpecificSettings(enable);
		fMetricList.setEnabled(enable);
	}

	@Override
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		String title = "PHP Depend Metrics Settings Changed";
		String message;
		if (fProject == null) {
			message = "The settings have changed. A full rebuild is required for changes to take effect. Execute the full build now?";
		} else {
			message = "The settings have changed. A rebuild of the project is required for changes to take effect. Build the project now?";
		}
		return new String[] { title, message };
	}

	protected final static Key getMetricKey(String key) {
		return getKey(PHPDependPlugin.PLUGIN_ID, key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#
	 * updateControls()
	 */
	@Override
	protected void updateControls() {
		unpackMetrics();
	}

	private void unpackMetrics() {
		String idPrefs = getValue(PREF_METRICS_IDS);
		String namePrefs = getValue(PREF_METRICS_NAMES);
		String enabledPrefs = getValue(PREF_METRICS_ENABLED);
		String warningPrefs = getValue(PREF_METRICS_WARNINGS);
		String errorPrefs = getValue(PREF_METRICS_ERRORS);

		if (idPrefs != null) {
			String[] ids = getTokens(idPrefs, ";");
			String[] names = getTokens(namePrefs, ";");
			String[] enabled = getTokens(enabledPrefs, ";");
			String[] warnings = getTokens(warningPrefs, ";");
			String[] errors = getTokens(errorPrefs, ";");

			ArrayList<Metric> elements = new ArrayList<Metric>(ids.length);

			for (int i = 0; i < ids.length; i++) {
				Metric m = new Metric();
				m.id = ids[i];
				m.name = names[i];
				m.enabled = enabled[i].equals("1");

				String[] warning = getTokens(warnings[i], ",");
				if (warning != null && warning.length == 2) {
					m.warningCompare = warning[0];
					m.warningLevel = Integer.parseInt(warning[1]);
				}

				String[] error = getTokens(errors[i], ",");
				if (error != null && error.length == 2) {
					m.errorCompare = error[0];
					m.errorLevel = Integer.parseInt(error[1]);
				}

				elements.add(m);
			}

			fMetricList.setElements(elements);
		}
	}
}