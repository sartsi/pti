/*******************************************************************************
 * Copyright (c) 2010, Sven Kiera
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * - Neither the name of the Organisation nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.core.metrics.elements;

import java.util.ArrayList;

import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.Metric;

public abstract class AbstractElement implements IElement {

	protected String name;
	protected IElement parent;
	protected ArrayList<IElement> members = new ArrayList<IElement>();
	protected Metric[] metrics;
	protected MetricResult[] results;
	protected boolean errors = false;
	protected boolean warnings = false;

	public AbstractElement(IElement parent, String name, MetricResult[] results) {
		this.parent = parent;
		if (parent != null && parent instanceof AbstractElement)
			((AbstractElement) parent).addMember(this);
		this.name = name;
		this.results = results;

		for (MetricResult r : results) {
			if (r.hasError()) {
				setHasErrors(true);
			} else if (r.hasWarning()) {
				setHasWarnings(true);
			}
			if (errors)
				break;
		}
	}

	public String getName() {
		return name;
	}

	public IElement getParent() {
		return parent;
	}

	public IElement[] members() {
		return members.toArray(new IElement[0]);
	}

	protected void addMember(IElement child) {
		if (!members.contains(child))
			members.add(child);
	}

	public MetricResult[] getResults() {
		return results;
	}

	protected void setHasErrors(boolean errors) {
		this.errors = errors;
		this.warnings = false;
		if (parent != null && parent instanceof AbstractElement)
			((AbstractElement) parent).setHasErrors(errors);
	}

	public boolean hasErrors() {
		return errors;
	}

	protected void setHasWarnings(boolean warnings) {
		if (!this.errors) {
			this.warnings = warnings;
			if (parent != null && parent instanceof AbstractElement)
				((AbstractElement) parent).setHasWarnings(warnings);
		}
	}

	public boolean hasWarnings() {
		return warnings;
	}
}
