/*******************************************************************************
 * Copyright (c) 2009, 2010 Dejan Spasic
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpmd.model;

import java.util.EventObject;

public class ViolationManagerEvent extends EventObject {
	private static final long serialVersionUID = -4006322450590636271L;

	private final IViolation[] added;
	private final IViolation[] removed;

	public ViolationManagerEvent(ViolationManager source, IViolation[] added, IViolation[] removed) {
		super(source);
		this.added = added;
		this.removed = removed;
	}

	public IViolation[] getAddedViolations() {
		return added;
	}

	public IViolation[] getRemovedViolations() {
		return removed;
	}
}
