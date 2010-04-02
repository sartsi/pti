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

public class MetricMethod extends MetricElement {

	private final static Image IMAGE = DLTKPluginImages.DESC_METHOD_DEFAULT.createImage();

	public MetricMethod(IMetricElement parent, String name, MetricResult[] results) {
		super(parent, name, results);
		Assert.isNotNull(parent);
	}

	public Image getImage() {
		return IMAGE;
	}

	public IResource getResource() {
		return getParent().getResource();
	}

	public IMarker getFileMarker() {
		return getParent().getFileMarker();
	}
}
