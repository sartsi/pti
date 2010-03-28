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
package org.phpsrc.eclipse.pti.tools.phpdepend.core.model;

public interface IMetricRunListener {

	/**
	 * Status constant indicating that a test passed (constant value 0).
	 */
	public static final int STATUS_OK = 0;
	/**
	 * Status constant indicating that a test had an error an unanticipated
	 * exception (constant value 1).
	 */
	public static final int STATUS_ERROR = 1;
	/**
	 * Status constant indicating that a test failed an assertion (constant
	 * value 2).
	 */
	public static final int STATUS_FAILURE = 2;

}
