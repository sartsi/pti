/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.core.model;

import org.eclipse.core.runtime.Assert;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.Metric;

public class MetricResult {

	public String id;
	public float value;
	public Metric metric;
	protected boolean error = false;
	protected boolean warning = false;

	public MetricResult(Metric m, float value) {
		Assert.isNotNull(m);
		this.id = m.id;
		this.value = value;
		this.metric = m;
		if ((m.errorMin != null || m.errorMax != null)
				&& (m.errorMin == null || value >= m.errorMin)
				&& (m.errorMax == null || value <= m.errorMax))
			error = true;
		if ((m.warningMin != null || m.warningMax != null)
				&& (m.warningMin == null || value >= m.warningMin)
				&& (m.warningMax == null || value <= m.warningMax))
			warning = true;

	}

	public MetricResult(String id, float value) {
		this.id = id;
		this.value = value;
	}

	public boolean hasError() {
		return error;
	}

	public boolean hasWarning() {
		return warning;
	}
}
