package org.phpsrc.eclipse.pti.tools.phpdepend.ui.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.phpsrc.eclipse.pti.tools.phpdepend.IPHPDependConstants;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.PHPDepend;

public class TestJob extends WorkspaceJob {

	final IResource[] resources;

	public TestJob(String name, IResource[] resources) {
		super(name);
		this.resources = resources;
	}

	public IStatus runInWorkspace(IProgressMonitor monitor) {
		monitor.beginTask("Validation", resources.length * 2);

		PHPDepend pdepend = PHPDepend.getInstance();

		int completed = 0;
		for (IResource resource : resources) {
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;

			try {
				resource
						.deleteMarkers(IPHPDependConstants.VALIDATOR_PHP_DEPEND_MARKER, false, IResource.DEPTH_INFINITE);
			} catch (CoreException e) {
			}

			monitor.worked(++completed);
			// createFileMarker(pdepend.validateResource(resource));
			pdepend.validateResource(resource);

			monitor.worked(++completed);
		}

		return Status.OK_STATUS;
	}

}
