/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.Metric;

public class MetricFile extends MetricElement {

	private final static Image IMAGE_PROJECT = PlatformUI.getWorkbench().getSharedImages().getImage(
			ISharedImages.IMG_OBJ_FILE);
	private final static Image IMAGE_NON_PROJECT = PlatformUI.getWorkbench().getSharedImages().getImage(
			ISharedImages.IMG_OBJ_FILE);

	protected IFile file;

	public MetricFile(IMetricElement parent, String name, MetricResult[] results) {
		super(parent, name, results);
		Assert.isNotNull(parent);

		IFile[] files = ResourcesPlugin.getWorkspace().getRoot()
				.findFilesForLocationURI(new java.io.File(name).toURI());
		if (files.length > 0) {
			file = files[0];
			this.name = file.getProject().getName() + " - " + file.getProjectRelativePath().toPortableString();
		}
	}

	public Image getImage() {
		if (file != null)
			return IMAGE_PROJECT;
		else
			return IMAGE_NON_PROJECT;
	}

	public IResource getResource() {
		return file;
	}

	public IMarker getFileMarker() {
		try {
			return file.createMarker(IMarker.TEXT);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getLevel() {
		return Metric.LEVEL_CLASS;
	}
}
