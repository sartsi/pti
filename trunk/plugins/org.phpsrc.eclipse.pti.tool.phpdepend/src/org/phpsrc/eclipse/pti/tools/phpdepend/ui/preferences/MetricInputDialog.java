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
package org.phpsrc.eclipse.pti.tools.phpdepend.ui.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.php.internal.ui.util.StatusInfo;
import org.eclipse.php.internal.ui.wizards.fields.ComboDialogField;
import org.eclipse.php.internal.ui.wizards.fields.DialogField;
import org.eclipse.php.internal.ui.wizards.fields.IDialogFieldListener;
import org.eclipse.php.internal.ui.wizards.fields.LayoutUtil;
import org.eclipse.php.internal.ui.wizards.fields.StringDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.Metric;
import org.phpsrc.eclipse.pti.ui.widgets.listener.NumberOnlyVerifyListener;

/**
 * Dialog to enter a na new task tag
 */
public class MetricInputDialog extends StatusDialog {

	private class MetricStandardInputAdapter implements IDialogFieldListener {
		public void dialogFieldChanged(DialogField field) {
			doValidation();
		}
	}

	private final StringDialogField fId;
	private final StringDialogField fName;
	private final StringDialogField fWarningMin;
	private final StringDialogField fWarningMax;
	private final StringDialogField fErrorMin;
	private final StringDialogField fErrorMax;
	private final ComboDialogField fType;

	private final List<String> fExistingIds;
	private final String[] fMetricTypes;

	public MetricInputDialog(Shell parent, Metric m, List<Metric> existingEntries) {
		super(parent);

		fExistingIds = new ArrayList<String>(existingEntries.size());
		for (int i = 0; i < existingEntries.size(); i++) {
			Metric curr = existingEntries.get(i);
			if (!curr.equals(m)) {
				fExistingIds.add(curr.id);
			}
		}

		if (m == null) {
			setTitle("New Metric");
		} else {
			setTitle("Edit Metric");
		}

		fId = new StringDialogField();
		fId.setLabelText("Id:");

		fName = new StringDialogField();
		fName.setLabelText("Name:");

		fWarningMin = new StringDialogField();
		fWarningMin.setLabelText("Warning min:");

		fWarningMax = new StringDialogField();
		fWarningMax.setLabelText("Warning max:");

		fErrorMin = new StringDialogField();
		fErrorMin.setLabelText("Error min:");

		fErrorMax = new StringDialogField();
		fErrorMax.setLabelText("Error max:");

		fType = new ComboDialogField(SWT.READ_ONLY);
		fType.setLabelText("Metric type");

		MetricStandardInputAdapter adapter = new MetricStandardInputAdapter();

		fId.setDialogFieldListener(adapter);
		fId.setText((m != null) ? m.id : ""); //$NON-NLS-1$

		fName.setDialogFieldListener(adapter);
		fName.setText((m != null) ? m.name : ""); //$NON-NLS-1$	

		fWarningMin.setDialogFieldListener(adapter);
		fWarningMin.setText((m != null && m.warningMin != null) ? "" + m.warningMin : ""); //$NON-NLS-1
		fWarningMax.setDialogFieldListener(adapter);
		fWarningMax.setText((m != null && m.warningMax != null) ? "" + m.warningMax : ""); //$NON-NLS-1$

		fErrorMin.setDialogFieldListener(adapter);
		fErrorMin.setText((m != null && m.errorMin != null) ? "" + m.errorMin : ""); //$NON-NLS-1$
		fErrorMax.setDialogFieldListener(adapter);
		fErrorMax.setText((m != null && m.errorMax != null) ? "" + m.errorMax : ""); //$NON-NLS-1$

		fMetricTypes = new String[] { "File", "File with type hierachy", "Folder" };
		fType.setItems(fMetricTypes);
		fType.selectItem(m != null && m.type > 0 ? m.type - 1 : 0);
	}

	public Metric getResult() {
		Metric m = new Metric();
		m.name = fName.getText().trim();
		m.id = fId.getText().trim();
		m.enabled = true;
		String text = fWarningMin.getText().trim();
		m.warningMin = text.length() == 0 ? null : Float.parseFloat(text);
		text = fWarningMax.getText().trim();
		m.warningMax = text.length() == 0 ? null : Float.parseFloat(text);
		text = fErrorMin.getText().trim();
		m.errorMin = text.length() == 0 ? null : Float.parseFloat(text);
		text = fErrorMax.getText().trim();
		m.errorMax = text.length() == 0 ? null : Float.parseFloat(text);

		// m.type = 1; // fType.getSelectionIndex() + 1;

		return m;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		Composite inner = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 3;
		inner.setLayout(layout);

		fName.doFillIntoGrid(inner, 3);

		LayoutUtil.setHorizontalGrabbing(fName.getTextControl(null));
		LayoutUtil.setWidthHint(fName.getTextControl(null), convertWidthInCharsToPixels(60));

		fName.postSetFocusOnDialogField(parent.getDisplay());
		fId.doFillIntoGrid(inner, 3);

		NumberOnlyVerifyListener numberOnlyListener = new NumberOnlyVerifyListener(NumberOnlyVerifyListener.TYPE_FLOAT,
				NumberOnlyVerifyListener.SIGNED);

		fWarningMin.doFillIntoGrid(inner, 3);
		fWarningMin.getTextControl(null).addListener(SWT.Verify, numberOnlyListener);
		fWarningMax.doFillIntoGrid(inner, 3);
		fWarningMax.getTextControl(null).addListener(SWT.Verify, numberOnlyListener);
		fErrorMin.doFillIntoGrid(inner, 3);
		fErrorMin.getTextControl(null).addListener(SWT.Verify, numberOnlyListener);
		fErrorMax.doFillIntoGrid(inner, 3);
		fErrorMax.getTextControl(null).addListener(SWT.Verify, numberOnlyListener);

		// fType.doFillIntoGrid(inner, 3);

		applyDialogFont(composite);

		return composite;
	}

	private void doValidation() {
		StatusInfo status = new StatusInfo();

		String newName = fName.getText().trim();
		String newId = fId.getText().trim();

		if (newName.length() == 0) {
			status.setError("Enter metric name.");
		} else if (!Pattern.matches("^[a-zA-Z0-9_ ]+$", newName)) {
			status.setError("Id can only contain letters, numbers, underscores and whitespaces");
		}

		if (newId.length() == 0) {
			status.setError("Enter metric id.");
		} else {
			if (!Pattern.matches("^[a-zA-Z0-9_]+$", newId)) {
				status.setError("Id can only contain letters, numbers and underscores");
			} else if (fExistingIds.contains(newName)) {
				status.setError("An entry with the same id already exists");
			}
		}

		Float wMin = null;
		try {
			wMin = fWarningMin.getText().trim().length() > 0 ? Float.parseFloat(fWarningMin.getText().trim()) : null;
		} catch (NumberFormatException e) {
			status.setError("Warning min is not a number");
		}

		Float wMax = null;
		try {
			wMax = fWarningMax.getText().trim().length() > 0 ? Float.parseFloat(fWarningMax.getText().trim()) : null;
		} catch (NumberFormatException e) {
			status.setError("Warning max is not a number");
		}

		if (wMin != null && wMax != null && wMin > wMax)
			status.setError("Warning min can not be higher than warning max");

		Float eMin = null;
		try {
			eMin = fErrorMin.getText().trim().length() > 0 ? Float.parseFloat(fErrorMin.getText().trim()) : null;
		} catch (NumberFormatException e) {
			status.setError("Error min is not a number");
		}

		Float eMax = null;
		try {
			eMax = fErrorMax.getText().trim().length() > 0 ? Float.parseFloat(fErrorMax.getText().trim()) : null;
		} catch (NumberFormatException e) {
			status.setError("Error max is not a number");
		}

		if (eMin != null && eMax != null && eMin > eMax)
			status.setError("Error min can not be higher than error max");

		updateStatus(status);
	}

	/*
	 * @see org.eclipse.jface.window.Window#configureShell(Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// TODO - Add the Help contex id
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell,
		// IPHPHelpContextIds.TODO_TASK_INPUT_DIALOG);
	}
}
