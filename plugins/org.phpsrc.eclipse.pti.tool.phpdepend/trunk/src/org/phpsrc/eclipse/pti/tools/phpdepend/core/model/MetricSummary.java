/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.core.model;

import java.util.Date;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.graphics.Image;
import org.phpsrc.eclipse.pti.tools.phpdepend.PHPDependPlugin;

public class MetricSummary extends MetricElement {

	private final static Image IMAGE = PHPDependPlugin.getDefault().getImageRegistry().get(
			PHPDependPlugin.IMG_PHP_DEPEND);

	protected Date generated;
	protected String version;

	public MetricSummary(MetricRunSession session, String name, MetricResult[] results,
			Date generated, String version) {
		super(session, name, results);
		this.generated = generated;
		this.version = version;
	}

	public Image getImage() {
		return IMAGE;
	}

	public IResource getResource() {
		return null;
	}

	public IMarker getFileMarker() {
		return null;
	}

	public Date getGenerated() {
		return generated;
	}

	public String getVersion() {
		return version;
	}
}
