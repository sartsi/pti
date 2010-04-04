/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.core.model;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.swt.graphics.Image;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.Metric;

public class MetricFunction extends MetricElement {

	private final static Image IMAGE = DLTKPluginImages.DESC_METHOD_DEFAULT.createImage();
	private MetricFile file;

	public MetricFunction(IMetricElement parent, String name, MetricResult[] results) {
		super(parent, name, results);
		Assert.isNotNull(parent);
	}

	public Image getImage() {
		return IMAGE;
	}

	public IResource getResource() {
		MetricFile f = getFile();
		if (f != null)
			return f.getResource();

		return null;
	}

	public IMarker getFileMarker() {
		MetricFile f = getFile();
		if (f != null)
			return f.getFileMarker();

		return null;
	}

	protected MetricFile getFile() {
		if (file == null) {
			for (IMetricElement member : getChildren()) {
				if (member instanceof MetricFile) {

				}
				file = (MetricFile) member;
				break;
			}
		}

		return file;
	}

	public int getLevel() {
		return Metric.LEVEL_METHOD;
	}
}
