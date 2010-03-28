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

package org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences;

public class Metric {
	public static final int TYPE_FILE = 1;
	public static final int TYPE_FILE_WITH_HIERACHY = 2;
	public static final int TYPE_PACKAGE = 3;

	public static final int PROPERTY_PROJECT = 1;
	public static final int PROPERTY_PACKAGE = 2;
	public static final int PROPERTY_CLASS = 4;
	public static final int PROPERTY_INTERFACE = 8;
	public static final int PROPERTY_METHOD = 16;
	public static final int PROPERTY_FUNCTION = 32;

	public static final int PROPERTY_ALL = Metric.PROPERTY_PROJECT | Metric.PROPERTY_PACKAGE | Metric.PROPERTY_CLASS
			| Metric.PROPERTY_INTERFACE | Metric.PROPERTY_METHOD | Metric.PROPERTY_FUNCTION;

	public boolean enabled;
	public String id;
	public String name;
	public Float warningMin;
	public Float warningMax;
	public Float errorMax;
	public Float errorMin;
	public int type;
	public int properties;

	public Metric() {
		this(null, null);
	}

	public Metric(String id, String name) {
		this(id, name, 0);
	}

	public Metric(String id, String name, int properties) {
		this.enabled = false;
		this.id = id;
		this.name = name;
		this.properties = properties;
	}

	public String toString() {
		return name;
	}
}
