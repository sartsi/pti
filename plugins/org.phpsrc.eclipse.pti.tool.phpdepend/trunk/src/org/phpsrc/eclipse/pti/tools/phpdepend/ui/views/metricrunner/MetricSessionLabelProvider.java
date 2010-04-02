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

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.phpsrc.eclipse.pti.core.PHPToolCorePlugin;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.IMetricElement;
import org.phpsrc.eclipse.pti.ui.images.OverlayImageIcon;

public class MetricSessionLabelProvider extends LabelProvider implements IStyledLabelProvider {

	private static final ImageRegistry imageRegistry = new ImageRegistry();
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

	public Image getImage(Object obj) {
		if (obj instanceof IMetricElement) {
			IMetricElement element = (IMetricElement) obj;
			String key = element.getClass().getName();
			if (element.hasErrors())
				key += "#error";
			else if (element.hasWarnings())
				key += "#warning";
			Image img = imageRegistry.get(key);
			if (img == null) {
				if (element.hasErrors())
					img = new OverlayImageIcon(element.getImage(), PHPToolCorePlugin.getDefault().getImageRegistry()
							.get(PHPToolCorePlugin.IMG_OVERLAY_ERROR), OverlayImageIcon.POS_BOTTOM_LEFT).getImage();
				else if (element.hasWarnings())
					img = new OverlayImageIcon(element.getImage(), PHPToolCorePlugin.getDefault().getImageRegistry()
							.get(PHPToolCorePlugin.IMG_OVERLAY_WARNING), OverlayImageIcon.POS_BOTTOM_LEFT).getImage();
				else
					img = element.getImage();
				imageRegistry.put(key, img);
			}

			return img;
		} else {
			throw new IllegalArgumentException(String.valueOf(obj));
		}
	}

	public void setShowTime(boolean showTime) {
		fShowTime = showTime;
		fireLabelProviderChanged(new LabelProviderChangedEvent(this));
	}

}
