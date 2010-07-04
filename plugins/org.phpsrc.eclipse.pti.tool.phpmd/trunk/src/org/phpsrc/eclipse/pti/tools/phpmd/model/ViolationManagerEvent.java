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
