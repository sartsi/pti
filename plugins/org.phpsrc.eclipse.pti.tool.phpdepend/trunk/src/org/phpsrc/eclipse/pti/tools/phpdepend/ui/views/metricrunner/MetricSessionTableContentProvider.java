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

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.IMetricElement;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricElement;

public class MetricSessionTableContentProvider implements IStructuredContentProvider {

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object[] getElements(Object inputElement) {
		ArrayList all = new ArrayList();
		addAll(all, (MetricElement) inputElement);
		return all.toArray();
	}

	private void addAll(ArrayList all, MetricElement suite) {
		IMetricElement[] children = suite.getChildren();
		for (int i = 0; i < children.length; i++) {
			IMetricElement element = children[i];
			all.add(element);
			addAll(all, (MetricElement) element);
		}
	}

	public void dispose() {
	}
}
