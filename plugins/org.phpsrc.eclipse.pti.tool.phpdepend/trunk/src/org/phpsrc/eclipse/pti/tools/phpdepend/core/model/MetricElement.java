/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.core.model;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.Metric;

public abstract class MetricElement implements IMetricElement {
	private Status fStatus;
	protected String name;
	protected IMetricElement parent;
	protected ArrayList<IMetricElement> members = new ArrayList<IMetricElement>();
	protected Metric[] metrics;
	protected MetricResult[] results;
	protected boolean errors = false;
	protected boolean warnings = false;

	public MetricElement(IMetricElement parent, String name, MetricResult[] results) {
		this.parent = parent;
		if (parent != null && parent instanceof MetricElement)
			((MetricElement) parent).addChild(this);
		this.name = name;
		this.results = results;

		fStatus = Status.OK;

		for (MetricResult r : results) {
			if (r.hasError()) {
				setHasErrors(true);
				fStatus = Status.ERROR;
			} else if (r.hasWarning()) {
				setHasWarnings(true);
				fStatus = Status.WARNING;
			}
			if (errors)
				break;
		}

	}

	public String getName() {
		return name;
	}

	public IMetricElement getParent() {
		return parent;
	}

	public IMetricElement[] getChildren() {
		return members.toArray(new IMetricElement[0]);
	}

	public boolean hasChildren() {
		return members.size() > 0;
	}

	protected IMetricElement getFirstChild() {
		return members.size() > 0 ? members.get(0) : null;
	}

	protected void addChild(IMetricElement child) {
		Assert.isNotNull(child);
		if (!members.contains(child))
			members.add(child);
	}

	public MetricResult[] getResults() {
		return results;
	}

	protected void setHasErrors(boolean errors) {
		this.errors = errors;
		this.warnings = false;
		if (parent != null && parent instanceof MetricElement)
			((MetricElement) parent).setHasErrors(errors);
	}

	public boolean hasErrors() {
		return errors;
	}

	protected void setHasWarnings(boolean warnings) {
		if (!this.errors) {
			this.warnings = warnings;
			if (parent != null && parent instanceof MetricElement)
				((MetricElement) parent).setHasWarnings(warnings);
		}
	}

	public boolean hasWarnings() {
		return warnings;
	}

	public Status getStatus() {
		return fStatus;
	}
}
