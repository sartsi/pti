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
package org.phpsrc.eclipse.pti.tools.phpunit.ui.preferences;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
import org.phpsrc.eclipse.pti.core.PHPCoreID;
import org.phpsrc.eclipse.pti.tools.phpunit.PHPUnitPlugin;
import org.phpsrc.eclipse.pti.tools.phpunit.core.PHPUnit;
import org.phpsrc.eclipse.pti.ui.preferences.AbstractPHPToolConfigurationBlock;

public class PHPUnitConfigurationBlock extends AbstractPHPToolConfigurationBlock {

	private static final Key PREF_PHP_EXECUTABLE = getPHPUnitKey(PHPUnitPreferenceNames.PREF_PHP_EXECUTABLE);
	private static final Key PREF_DEBUG_PRINT_OUTPUT = getPHPUnitKey(PHPUnitPreferenceNames.PREF_DEBUG_PRINT_OUTPUT);
	private static final Key PREF_BOOSTRAP = getPHPUnitKey(PHPUnitPreferenceNames.PREF_BOOTSTRAP);

	protected Text fBootstrap;
	protected Button fFileButton;

	public PHPUnitConfigurationBlock(IStatusChangeListener context, IProject project,
			IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
	}

	private static Key[] getKeys() {
		return new Key[] { PREF_PHP_EXECUTABLE, PREF_DEBUG_PRINT_OUTPUT, PREF_BOOSTRAP };
	}

	@Override
	protected Composite createToolContents(Composite parent) {
		Composite bootstrapComposite = createBootstrapContent(parent);

		unpackBoostrap();

		validateSettings(null, null, null);

		return bootstrapComposite;
	}

	private Composite createBootstrapContent(Composite folder) {

		final Group bootstrapGroup = new Group(folder, SWT.RESIZE);
		bootstrapGroup.setText("PHPUnit Options");

		final GridLayout bootstrapLayout = new GridLayout();
		bootstrapLayout.numColumns = 3;
		bootstrapLayout.verticalSpacing = 9;
		bootstrapGroup.setLayout(bootstrapLayout);

		Label fileLabel = new Label(bootstrapGroup, SWT.NULL);
		fileLabel.setText("Bootstrap file:");

		fBootstrap = new Text(bootstrapGroup, SWT.BORDER | SWT.SINGLE);
		fBootstrap.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		fFileButton = new Button(bootstrapGroup, SWT.PUSH);
		fFileButton.setText("Browse...");
		fFileButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				handleBrowse();
			}
		});

		return bootstrapGroup;
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

	@Override
	protected void validateSettings(Key changedKey, String oldValue, String newValue) {
		// TODO Auto-generated method stub
	}

	@Override
	protected boolean processChanges(IWorkbenchPreferenceContainer container) {
		clearProjectLauncherCache();

		setValue(PREF_BOOSTRAP, fBootstrap.getText());

		return super.processChanges(container);
	}

	private void clearProjectLauncherCache() {
		IWorkspace root = ResourcesPlugin.getWorkspace();
		IProject[] projects = root.getRoot().getProjects();
		for (IProject project : projects) {
			try {
				IProjectNature nature = project.getNature(PHPCoreID.PHPNatureID);
				if (nature != null) {
					project.setSessionProperty(PHPUnit.QUALIFIED_NAME, null);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void useProjectSpecificSettings(boolean enable) {
		super.useProjectSpecificSettings(enable);
		fBootstrap.setEnabled(enable);
		fFileButton.setEnabled(enable);
	}

	@Override
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		return null;
	}

	protected final static Key getPHPUnitKey(String key) {
		return getKey(PHPUnitPlugin.PLUGIN_ID, key);
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
		unpackBoostrap();
	}

	private void unpackBoostrap() {
		String bootstrap = getValue(PREF_BOOSTRAP);
		if (bootstrap != null)
			fBootstrap.setText(bootstrap);
	}
}