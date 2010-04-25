package org.phpsrc.eclipse.pti.tools.phpmd.handlers;

import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.phpsrc.eclipse.pti.tools.phpmd.PhpmdPlugin;
import org.phpsrc.eclipse.pti.tools.phpmd.core.IResourceCollector;
import org.phpsrc.eclipse.pti.tools.phpmd.core.ResourceCollectorFactory;

public class PhpmdHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
		final IResource resource = determineFirstResourceOrNull(currentSelection);

		if (null == resource) {
			return null;
		}

		Job job = createJob(resource);
		job.schedule();

		return null;
	}

	private IResource determineFirstResourceOrNull(ISelection currentSelection) {
		IResourceCollector collector = ResourceCollectorFactory.factory(currentSelection);

		if (null == collector) {
			return null;
		}

		collector.collect();
		ArrayList<IResource> resourceList = collector.getResources();

		if (resourceList.isEmpty()) {
			return null;
		}

		return resourceList.get(0);
	}

	private Job createJob(final IResource resource) {
		Job job = new Job("PHP Mess Detection") {
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Begin Mess Detection", 2);

				monitor.worked(1);
				PhpmdPlugin.getDefault().getPhpmd().execute(resource);
				monitor.worked(2);

				return Status.OK_STATUS;
			}
		};

		job.setUser(false);
		return job;
	}
}