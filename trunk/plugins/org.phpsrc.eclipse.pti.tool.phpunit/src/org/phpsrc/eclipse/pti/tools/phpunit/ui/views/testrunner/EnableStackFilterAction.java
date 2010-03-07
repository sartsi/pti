/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.phpsrc.eclipse.pti.tools.phpunit.ui.views.testrunner;

import org.eclipse.jdt.internal.junit.ui.JUnitPlugin;
import org.eclipse.jdt.internal.junit.ui.JUnitPreferencePage;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

/**
 * Action to enable/disable stack trace filtering.
 */
public class EnableStackFilterAction extends Action {

	private FailureTrace fView;

	public EnableStackFilterAction(FailureTrace view) {
		super(PHPUnitMessages.EnableStackFilterAction_action_label);
		setDescription(PHPUnitMessages.EnableStackFilterAction_action_description);
		setToolTipText(PHPUnitMessages.EnableStackFilterAction_action_tooltip);

		setDisabledImageDescriptor(JUnitPlugin.getImageDescriptor("dlcl16/cfilter.gif")); //$NON-NLS-1$
		setHoverImageDescriptor(JUnitPlugin.getImageDescriptor("elcl16/cfilter.gif")); //$NON-NLS-1$
		setImageDescriptor(JUnitPlugin.getImageDescriptor("elcl16/cfilter.gif")); //$NON-NLS-1$
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IPHPUnitHelpContextIds.ENABLEFILTER_ACTION);

		fView = view;
		setChecked(JUnitPreferencePage.getFilterStack());
	}

	/*
	 * @see Action#actionPerformed
	 */
	public void run() {
		JUnitPreferencePage.setFilterStack(isChecked());
		fView.refresh();
	}
}
