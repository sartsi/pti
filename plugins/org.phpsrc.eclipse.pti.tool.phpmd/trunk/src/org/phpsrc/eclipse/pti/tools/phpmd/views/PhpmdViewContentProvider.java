package org.phpsrc.eclipse.pti.tools.phpmd.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.phpsrc.eclipse.pti.tools.phpmd.model.IViolationManagerListener;
import org.phpsrc.eclipse.pti.tools.phpmd.model.ViolationManager;
import org.phpsrc.eclipse.pti.tools.phpmd.model.ViolationManagerEvent;

public class PhpmdViewContentProvider implements IStructuredContentProvider, IViolationManagerListener {
	private ViolationManager manager;
	private TableViewer viewer;

	public Object[] getElements(Object inputElement) {
		return manager.getViolations();
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) viewer;
		if (null != manager)
			manager.removeViolationManagerListener(this);
		manager = (ViolationManager) newInput;
		if (null != manager)
			manager.addViolationManagerListener(this);
	}

	public void violationsChanged(ViolationManagerEvent event) {
		viewer.getTable().setRedraw(false);
		try {
			viewer.remove(event.getRemovedViolations());
			viewer.add(event.getAddedViolations());
		} finally {
			viewer.getTable().setRedraw(true);
		}
	}

	public void dispose() {
	}
}
