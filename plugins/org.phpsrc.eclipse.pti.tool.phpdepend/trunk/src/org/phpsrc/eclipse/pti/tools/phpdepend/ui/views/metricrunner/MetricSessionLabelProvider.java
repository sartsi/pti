/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brock Janiczak (brockj@tpg.com.au)
 *         - https://bugs.eclipse.org/bugs/show_bug.cgi?id=102236: [JUnit] display execution time next to each test
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.ui.views.metricrunner;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.IMetricElement;

public class MetricSessionLabelProvider extends LabelProvider implements IStyledLabelProvider {

	private final MetricRunnerViewPart fTestRunnerPart;
	private final int fLayoutMode;

	private boolean fShowTime;

	public MetricSessionLabelProvider(MetricRunnerViewPart testRunnerPart, int layoutMode) {
		fTestRunnerPart = testRunnerPart;
		fLayoutMode = layoutMode;
	}

	public StyledString getStyledText(Object element) {
		String label = getSimpleLabel(element);
		if (label == null) {
			return new StyledString(element.toString());
		}
		StyledString text = new StyledString(label);

		return text;
	}

	private String getSimpleLabel(Object element) {
		if (element instanceof IMetricElement) {
			return ((IMetricElement) element).getName();
		}
		return null;
	}

	public String getText(Object element) {
		String label = getSimpleLabel(element);
		if (label == null) {
			return element.toString();
		}
		return label;
	}

	public Image getImage(Object element) {
		if (element instanceof IMetricElement) {
			return ((IMetricElement) element).getImage();
		} else {
			throw new IllegalArgumentException(String.valueOf(element));
		}
	}

	public void setShowTime(boolean showTime) {
		fShowTime = showTime;
		fireLabelProviderChanged(new LabelProviderChangedEvent(this));
	}

}
