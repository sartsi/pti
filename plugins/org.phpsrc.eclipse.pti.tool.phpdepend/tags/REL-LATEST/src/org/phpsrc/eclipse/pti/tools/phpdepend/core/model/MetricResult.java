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
	public IMetricElement element;
	protected boolean error = false;
	protected boolean warning = false;

	public MetricResult(Metric m, float value) {
		Assert.isNotNull(m);
		this.id = m.id;
		this.value = value;
		this.metric = m;
	}

	public void setElement(IMetricElement element) {
		Assert.isNotNull(element);
		this.element = element;
		if (metric != null && element.getLevel() == metric.level) {
			checkForErrorAndWarning();
		}
	}

	protected void checkForErrorAndWarning() {
		if (metric.enabled) {
			if ((metric.errorMin != null || metric.errorMax != null)
					&& (metric.errorMin == null || value >= metric.errorMin)
					&& (metric.errorMax == null || value <= metric.errorMax))
				error = true;
			if ((metric.warningMin != null || metric.warningMax != null)
					&& (metric.warningMin == null || value >= metric.warningMin)
					&& (metric.warningMax == null || value <= metric.warningMax))
				warning = true;
		}
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
