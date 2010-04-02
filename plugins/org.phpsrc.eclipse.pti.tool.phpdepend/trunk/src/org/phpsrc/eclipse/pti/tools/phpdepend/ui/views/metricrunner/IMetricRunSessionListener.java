/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.ui.views.metricrunner;

import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricRunSession;

public interface IMetricRunSessionListener {

	/**
	 * @param testRunSession
	 *            the new session, or <code>null</code>
	 */
	void sessionAdded(MetricRunSession metricRunSession);

	void sessionRemoved(MetricRunSession metricRunSession);

}
