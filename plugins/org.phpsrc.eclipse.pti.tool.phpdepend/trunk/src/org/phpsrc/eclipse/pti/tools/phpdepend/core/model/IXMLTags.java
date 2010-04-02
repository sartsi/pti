/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.core.model;

public interface IXMLTags {

	public static final String NODE_METRICS = "metrics"; //$NON-NLS-1$
	public static final String NODE_PACKAGE = "package"; //$NON-NLS-1$
	public static final String NODE_CLASS = "class"; //$NON-NLS-1$
	public static final String NODE_METHOD = "method"; //$NON-NLS-1$
	public static final String NODE_FUNCTION = "function"; //$NON-NLS-1$
	public static final String NODE_FILE = "file"; //$NON-NLS-1$
	public static final String NODE_FILES = "files"; //$NON-NLS-1$

	/**
	 * value: String
	 */
	public static final String ATTR_NAME = "name"; //$NON-NLS-1$

	/**
	 * value: String
	 */
	public static final String ATTR_GENERATED = "generated"; //$NON-NLS-1$

	/**
	 * value: String
	 */
	public static final String ATTR_PDEPEND = "pdepend"; //$NON-NLS-1$

}
