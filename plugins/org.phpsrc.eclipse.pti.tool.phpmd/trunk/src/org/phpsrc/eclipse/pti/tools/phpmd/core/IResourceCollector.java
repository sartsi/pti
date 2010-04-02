package org.phpsrc.eclipse.pti.tools.phpmd.core;

import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;

public interface IResourceCollector {
	public void collect();

	public void setSelection(ISelection selection);

	public ISelection getSelection();

	public ArrayList<IResource> getResources();
}
