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
		if ((m.errorMin != null || m.errorMax != null) && (m.errorMin == null || value >= m.errorMin)
				&& (m.errorMax == null || value <= m.errorMax))
			error = true;
		if ((m.warningMin != null || m.warningMax != null) && (m.warningMin == null || value >= m.warningMin)
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
