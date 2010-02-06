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

import org.eclipse.core.resources.IProject;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.phpsrc.eclipse.pti.library.pear.ui.preferences.AbstractPEARPHPToolConfigurationBlock;
import org.phpsrc.eclipse.pti.tools.phpdepend.PHPDependPlugin;

public class PHPDependConfigurationBlock extends AbstractPEARPHPToolConfigurationBlock {

	private static final Key PREF_PHP_EXECUTABLE = getPHPDependKey(PHPDependPreferenceNames.PREF_PHP_EXECUTABLE);
	private static final Key PREF_PEAR_LIBRARY = getPHPDependKey(PHPDependPreferenceNames.PREF_PEAR_LIBRARY);
	private static final Key PREF_DEBUG_PRINT_OUTPUT = getPHPDependKey(PHPDependPreferenceNames.PREF_DEBUG_PRINT_OUTPUT);

	public PHPDependConfigurationBlock(IStatusChangeListener context, IProject project,
			IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
	}

	private static Key[] getKeys() {
		return new Key[] { PREF_PHP_EXECUTABLE, PREF_PEAR_LIBRARY, PREF_DEBUG_PRINT_OUTPUT };
	}

	@Override
	protected Composite createToolContents(Composite parent) {
		return parent;
	}

	@Override
	protected void validateSettings(Key changedKey, String oldValue, String newValue) {
		// TODO Auto-generated method stub
	}

	@Override
	protected boolean processChanges(IWorkbenchPreferenceContainer container) {
		// clearProjectLauncherCache(Phpcpd.QUALIFIED_NAME);

		return super.processChanges(container);
	}

	@Override
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		return null;
	}

	protected final static Key getPHPDependKey(String key) {
		return getKey(PHPDependPlugin.PLUGIN_ID, key);
	}

	@Override
	protected Key getPHPExecutableKey() {
		return PREF_PHP_EXECUTABLE;
	}

	@Override
	protected Key getDebugPrintOutputKey() {
		return PREF_DEBUG_PRINT_OUTPUT;
	}

	@Override
	protected Key getPEARLibraryKey() {
		return PREF_PEAR_LIBRARY;
	}
}