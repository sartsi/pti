/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.ui.views.metricrunner;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.IMetricElement;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricElement;

public class MetricSessionTreeContentProvider implements ITreeContentProvider {

	private final Object[] NO_CHILDREN = new Object[0];

	public void dispose() {
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IMetricElement)
			return ((IMetricElement) parentElement).getChildren();
		else
			return NO_CHILDREN;
	}

	public Object[] getElements(Object inputElement) {
		return ((MetricElement) inputElement).getChildren();
	}

	public Object getParent(Object element) {
		return ((MetricElement) element).getParent();
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IMetricElement)
			return ((IMetricElement) element).getChildren().length > 0;
		else
			return false;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
