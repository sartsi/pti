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

package org.phpsrc.eclipse.pti.tools.phpdepend.ui.views.metricrunner;

import org.eclipse.jface.action.Action;
import org.phpsrc.eclipse.pti.tools.phpdepend.PHPDependPlugin;

class ShowPreviousFailureAction extends Action {

	private MetricRunnerViewPart fPart;

	public ShowPreviousFailureAction(MetricRunnerViewPart part) {
		super("Previous Failure");
		setDisabledImageDescriptor(PHPDependPlugin.getImageDescriptor("dlcl16/select_prev.gif")); //$NON-NLS-1$
		setHoverImageDescriptor(PHPDependPlugin.getImageDescriptor("elcl16/select_prev.gif")); //$NON-NLS-1$
		setImageDescriptor(PHPDependPlugin.getImageDescriptor("elcl16/select_prev.gif")); //$NON-NLS-1$
		setToolTipText("Previous Failure");
		fPart = part;
	}

	public void run() {
		fPart.selectPreviousFailure();
	}
}
