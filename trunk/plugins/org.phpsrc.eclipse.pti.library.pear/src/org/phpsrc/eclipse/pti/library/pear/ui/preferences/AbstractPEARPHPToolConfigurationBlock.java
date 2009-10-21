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

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.phpsrc.eclipse.pti.library.pear.core.preferences.PEARPreferences;
import org.phpsrc.eclipse.pti.library.pear.core.preferences.PEARPreferencesFactory;
import org.phpsrc.eclipse.pti.ui.preferences.AbstractPHPToolConfigurationBlock;

public abstract class AbstractPEARPHPToolConfigurationBlock extends AbstractPHPToolConfigurationBlock {

	private static final String PEAR_PAGE_ID = "rg.phpsrc.eclipse.pti.library.pear.ui.preferences.PEARPreferencePage"; //$NON-NLS-1$

	protected Combo pearLibraryCombo;

	public AbstractPEARPHPToolConfigurationBlock(IStatusChangeListener context, IProject project, Key[] allKeys,
			IWorkbenchPreferenceContainer container) {
		super(context, project, allKeys, container);
	}

	public Control createContents(Composite parent) {
		setShell(parent.getShell());

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 10;
		composite.setLayout(layout);

		createVersionContent(composite);
		unpackPHPExecutable();

		createLibraryContent(composite);
		unpackPEARLibrary();

		createDebugContent(composite);

		Composite toolComposite = createToolContents(composite);
		toolComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		return composite;
	}

	protected Composite createLibraryContent(Composite parent) {
		Group composite = new Group(parent, SWT.RESIZE);
		composite.setText("PEAR Library");

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label pearLibraryNameLabel = new Label(composite, SWT.NONE);
		pearLibraryNameLabel.setText("PEAR Library:");

		pearLibraryCombo = new Combo(composite, SWT.READ_ONLY);

		pearLibraryCombo.setItems(preparePEARLibraryEntryList());
		pearLibraryCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				String selectedValue = pearLibraryCombo.getText();
				setPEARLibrary(selectedValue);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		IPageChangedListener listener = new IPageChangedListener() {
			public void pageChanged(PageChangedEvent event) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						// pearLibraryCombo.setItems(preparePHPExecutableEntryList());
					}
				});
			}
		};

		addLink(composite, "<a>PEAR Libraries ...</a>", PEAR_PAGE_ID, listener);

		return composite;
	}

	protected void setPEARLibrary(String value) {
		PEARPreferences[] prefs = PEARPreferencesFactory.getAll();

		for (int i = 0; i < prefs.length; i++) {
			String name = prefs[i].getLibraryName();
			if (name.equals(value)) {
				pearLibraryCombo.setText(name);
				setValue(getPEARLibraryKey(), name);
				validateSettings(getPEARLibraryKey(), null, null);
				return;
			}
		}
	}

	protected String[] preparePEARLibraryEntryList() {
		PEARPreferences[] prefs = PEARPreferencesFactory.getAll();

		if (prefs == null || prefs.length == 0) {
			return new String[] { "None Defined" };
		}

		String[] entryList = new String[prefs.length];

		for (int i = 0; i < prefs.length; i++) {
			entryList[i] = prefs[i].getLibraryName();
		}

		return entryList;
	}

	protected void unpackPEARLibrary() {
		String value = getValue(getPEARLibraryKey());
		if (value != null)
			pearLibraryCombo.setText(value);
	}

	protected abstract Key getPEARLibraryKey();
}