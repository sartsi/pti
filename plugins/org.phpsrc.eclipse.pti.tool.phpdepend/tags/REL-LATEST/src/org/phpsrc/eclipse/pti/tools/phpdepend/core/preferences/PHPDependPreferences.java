/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences;

import org.phpsrc.eclipse.pti.library.pear.core.preferences.AbstractPEARPHPToolPreferences;

public class PHPDependPreferences extends AbstractPEARPHPToolPreferences {
	public boolean withoutAnnotations;
	public boolean badDocumentation;
	public String coderankMode;
	public String optimization;
	public String validFileExtensions;
	public String excludePackages;
	public Metric[] metrics;

	protected PHPDependPreferences(String phpExecutable, boolean printOutput,
			String pearLibraryName, boolean withoutAnnotations, boolean badDocumentation,
			String coderankMode, String optimization, String validFileExtensions,
			String excludePackages, Metric[] metrics) {
		super(phpExecutable, printOutput, pearLibraryName);
		this.withoutAnnotations = withoutAnnotations;
		this.badDocumentation = badDocumentation;
		this.coderankMode = coderankMode;
		this.optimization = optimization;
		this.validFileExtensions = validFileExtensions;
		this.excludePackages = excludePackages;
		this.metrics = metrics;
	}
}