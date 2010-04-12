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

	public static final int LEVEL_PROJECT = 1;
	public static final int LEVEL_PACKAGE = 2;
	public static final int LEVEL_CLASS = 4;
	public static final int LEVEL_METHOD = 8;

	public boolean enabled;
	public String id;
	public String name;
	public Float warningMin;
	public Float warningMax;
	public Float errorMax;
	public Float errorMin;
	public int type;
	public int level;

	public Metric() {
		this(null, null);
	}

	public Metric(String id, String name) {
		this(id, name, 0);
	}

	public Metric(String id, String name, int level) {
		this.enabled = false;
		this.id = id;
		this.name = name;
		this.level = level;
	}

	public String toString() {
		return name;
	}
}
