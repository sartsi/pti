package org.phpsrc.eclipse.pti.tools.phpmd.core;

import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;

public abstract class AbstractResourceCollector implements IResourceCollector {
	ArrayList<IResource> resourceList = new ArrayList<IResource>(1);
	ISelection selection = null;

	public void setSelection(ISelection selection) {
		this.selection = selection;
		resourceList = new ArrayList<IResource>(1);
	}

	public ISelection getSelection() {
		return selection;
	}

	public ArrayList<IResource> getResources() {
		return resourceList;
	}

	protected void addResource(IResource resource) {
		if (resource != null && resource.exists() && !resourceList.contains(resource))
			resourceList.add(resource);
	}
}
