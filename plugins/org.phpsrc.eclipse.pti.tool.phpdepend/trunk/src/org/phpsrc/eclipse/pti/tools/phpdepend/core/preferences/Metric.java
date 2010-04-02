/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

	public static final int PROPERTY_ALL = Metric.PROPERTY_PROJECT | Metric.PROPERTY_PACKAGE
			| Metric.PROPERTY_CLASS | Metric.PROPERTY_INTERFACE | Metric.PROPERTY_METHOD
			| Metric.PROPERTY_FUNCTION;

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
