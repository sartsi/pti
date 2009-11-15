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
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.php.internal.ui.util.StatusInfo;
import org.eclipse.php.internal.ui.wizards.fields.DialogField;
import org.eclipse.php.internal.ui.wizards.fields.IDialogFieldListener;
import org.eclipse.php.internal.ui.wizards.fields.LayoutUtil;
import org.eclipse.php.internal.ui.wizards.fields.StringDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.phpsrc.eclipse.pti.tools.phpdepend.preferences.Metric;
import org.phpsrc.eclipse.pti.ui.wizards.fields.ComboStringDialogField;

/**
 * Dialog to enter a na new task tag
 */
public class MetricInputDialog extends StatusDialog {

	private class MetricStandardInputAdapter implements IDialogFieldListener {
		public void dialogFieldChanged(DialogField field) {
			doValidation();
		}
	}

	private final StringDialogField fIdDialogField;
	private final StringDialogField fNameDialogField;
	private final ComboStringDialogField fWarningDialogField;
	private final ComboStringDialogField fErrorDialogField;

	private final List<String> fExistingIds;

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

		fIdDialogField = new StringDialogField();
		fIdDialogField.setLabelText("Id:");

		fNameDialogField = new StringDialogField();
		fNameDialogField.setLabelText("Name:");

		fWarningDialogField = new ComboStringDialogField();
		fWarningDialogField.setLabelText("Warning:");

		fErrorDialogField = new ComboStringDialogField();
		fErrorDialogField.setLabelText("Error:");

		MetricStandardInputAdapter adapter = new MetricStandardInputAdapter();

		fIdDialogField.setDialogFieldListener(adapter);
		fIdDialogField.setText((m != null) ? m.id : ""); //$NON-NLS-1$

		fNameDialogField.setDialogFieldListener(adapter);
		fNameDialogField.setText((m != null) ? m.name : ""); //$NON-NLS-1$	

		fErrorDialogField.setText((m != null) ? "" + m.errorLevel : ""); //$NON-NLS-1$
		fErrorDialogField.setSelectionItems(new String[] { Metric.COMPARE_LESS, Metric.COMPARE_LESS_OR_EQUAL,
				Metric.COMPARE_EQUAL, Metric.COMPARE_GREATER_OR_EQUAL, Metric.COMPARE_GREATER });
		fErrorDialogField.setSelection((m != null) ? m.errorCompare : "");

		fWarningDialogField.setText((m != null) ? "" + m.warningLevel : ""); //$NON-NLS-1$
		fWarningDialogField.setSelectionItems(new String[] { Metric.COMPARE_LESS, Metric.COMPARE_LESS_OR_EQUAL,
				Metric.COMPARE_EQUAL, Metric.COMPARE_GREATER_OR_EQUAL, Metric.COMPARE_GREATER });
		fWarningDialogField.setSelection((m != null) ? m.warningCompare : "");
	}

	public Metric getResult() {
		Metric m = new Metric();
		m.name = fNameDialogField.getText().trim();
		m.id = fIdDialogField.getText().trim();
		m.enabled = true;
		m.warningCompare = fWarningDialogField.getSelection();
		m.warningLevel = Integer.parseInt(fWarningDialogField.getText().trim());
		m.errorCompare = fErrorDialogField.getSelection();
		m.errorLevel = Integer.parseInt(fErrorDialogField.getText().trim());

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

		fNameDialogField.doFillIntoGrid(inner, 3);

		LayoutUtil.setHorizontalGrabbing(fNameDialogField.getTextControl(null));
		LayoutUtil.setWidthHint(fNameDialogField.getTextControl(null), convertWidthInCharsToPixels(60));

		fNameDialogField.postSetFocusOnDialogField(parent.getDisplay());

		fIdDialogField.doFillIntoGrid(inner, 3);
		fWarningDialogField.doFillIntoGrid(inner, 3);
		fErrorDialogField.doFillIntoGrid(inner, 3);

		applyDialogFont(composite);

		return composite;
	}

	private void doValidation() {
		StatusInfo status = new StatusInfo();

		String newName = fNameDialogField.getText();
		String newId = fIdDialogField.getText();

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
