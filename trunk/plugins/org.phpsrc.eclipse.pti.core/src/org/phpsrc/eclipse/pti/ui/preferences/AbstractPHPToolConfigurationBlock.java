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
package org.phpsrc.eclipse.pti.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;
import org.eclipse.php.internal.debug.core.preferences.PHPexes;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.OptionsConfigurationBlock;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

public abstract class AbstractPHPToolConfigurationBlock extends OptionsConfigurationBlock {

	private static final String PHP_EXE_PAGE_ID = "org.eclipse.php.debug.ui.preferencesphps.PHPsPreferencePage"; //$NON-NLS-1$

	protected Label phpExecutableNameLabel;
	protected Combo phpExecutableCombo;

	protected Button debugPrintOutputCheckbox;

	public AbstractPHPToolConfigurationBlock(IStatusChangeListener context, IProject project, Key[] allKeys,
			IWorkbenchPreferenceContainer container) {
		super(context, project, allKeys, container);
	}

	@Override
	public Control createContents(Composite parent) {
		setShell(parent.getShell());

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 10;
		composite.setLayout(layout);

		createVersionContent(composite);
		unpackPHPExecutable();

		createDebugContent(composite);

		Composite toolComposite = createToolContents(composite);
		toolComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		return composite;
	}

	protected abstract Composite createToolContents(Composite parent);

	protected Composite createVersionContent(Composite parent) {
		Group composite = new Group(parent, SWT.RESIZE);
		composite.setText("PHP Executable");

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		phpExecutableNameLabel = new Label(composite, SWT.NONE);
		phpExecutableNameLabel.setText("PHP Executable:");

		phpExecutableCombo = new Combo(composite, SWT.READ_ONLY);

		phpExecutableCombo.setItems(preparePHPExecutableEntryList());
		phpExecutableCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				String selectedValue = phpExecutableCombo.getText();
				setPhpExecutable(selectedValue);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		IPageChangedListener listener = new IPageChangedListener() {
			public void pageChanged(PageChangedEvent event) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						phpExecutableCombo.setItems(preparePHPExecutableEntryList());
					}
				});
			}
		};

		addLink(composite, "<a>PHP Executables...</a>", PHP_EXE_PAGE_ID, listener);

		return composite;
	}

	protected Composite createDebugContent(Composite parent) {
		Group composite = new Group(parent, SWT.RESIZE);
		composite.setText("Debug");

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		debugPrintOutputCheckbox = new Button(composite, SWT.CHECK);
		debugPrintOutputCheckbox.setText("print PHP output to console");
		debugPrintOutputCheckbox.setSelection(getBooleanValue(getDebugPrintOutputKey()));
		debugPrintOutputCheckbox.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				boolean selection = debugPrintOutputCheckbox.getSelection();
				setValue(getDebugPrintOutputKey(), selection);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		return composite;
	}

	protected void setPhpExecutable(String value) {
		PHPexes exes = PHPexes.getInstance();
		PHPexeItem[] items = exes.getAllItems();

		for (int i = 0; i < items.length; i++) {
			String name = items[i].getName();
			if (name.equals(value)) {
				phpExecutableCombo.setText(name);
				setValue(getPHPExecutableKey(), name);
				validateSettings(getPHPExecutableKey(), null, null);
				return;
			}
		}
	}

	protected String[] preparePHPExecutableEntryList() {
		PHPexes exes = PHPexes.getInstance();
		PHPexeItem[] items = exes.getAllItems();

		if (items == null || items.length == 0) {
			return new String[] { "None Defined" };
		}

		String[] entryList = new String[items.length];

		for (int i = 0; i < items.length; i++) {
			entryList[i] = items[i].getName();
		}

		return entryList;
	}

	protected void unpackPHPExecutable() {
		String value = getValue(getPHPExecutableKey());
		if (value != null)
			phpExecutableCombo.setText(value);
	}

	protected abstract Key getPHPExecutableKey();

	protected abstract Key getDebugPrintOutputKey();

	@Override
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		return null;
	}

	protected void addLink(Composite parent, String label, final String propertyPageID) {
		addLink(parent, label, propertyPageID, null);
	}

	protected void addLink(Composite parent, String label, final String propertyPageID,
			final IPageChangedListener listener) {
		Link link = new Link(parent, SWT.NONE);
		link.setFont(parent.getFont());
		link.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, true, false));
		link.setText(label);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(getShell(), propertyPageID, null,
						null);
				dialog.setBlockOnOpen(true);
				if (listener != null) {
					dialog.addPageChangedListener(listener);
				}
				dialog.open();
			}
		});
	}

	protected void makeFontItalic(Label label) {
		Font font = label.getFont();
		FontData[] data = font.getFontData();
		if (data.length > 0) {
			data[0].setStyle(data[0].getStyle() | SWT.ITALIC);
		}
		label.setFont(new Font(font.getDevice(), data));
	}
}