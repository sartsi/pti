/*******************************************************************************
 * Copyright (c) 2009, Sven Kiera
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
package org.phpsrc.eclipse.pti.tools.phpcpd.ui.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.core.IOpenable;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.phpsrc.eclipse.pti.core.compiler.problem.FileProblem;
import org.phpsrc.eclipse.pti.tools.phpcpd.IPhpcpdConstants;
import org.phpsrc.eclipse.pti.tools.phpcpd.core.Phpcpd;
import org.phpsrc.eclipse.pti.ui.Logger;

public class ValidateResourcesAction implements IObjectActionDelegate {
	private IResource[] resources;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		ISelection selection = targetPart.getSite().getSelectionProvider().getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			resources = new IResource[structuredSelection.size()];

			ArrayList<IResource> entries = new ArrayList<IResource>(structuredSelection.size());

			Iterator<?> iterator = structuredSelection.iterator();
			while (iterator.hasNext()) {
				Object entry = iterator.next();
				try {
					if (entry instanceof IScriptProject) {
						entries.add((IProject) ((IScriptProject) entry).getResource());
					} else if (entry instanceof IOpenable) {
						IResource resource = ((IOpenable) entry).getCorrespondingResource();
						if (resource != null)
							entries.add(resource);
					}
				} catch (ModelException e) {
					Logger.logException(e);
				}
			}

			resources = entries.toArray(new IResource[0]);
		}
	}

	public void run(IAction action) {
		Job job = new Job("PHP Copy/Paste Detection") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Validation", resources.length * 2);

				Phpcpd phpcpd = Phpcpd.getInstance();

				int completed = 0;
				for (IResource resource : resources) {
					if (monitor.isCanceled())
						return Status.CANCEL_STATUS;

					try {
						resource.deleteMarkers(IPhpcpdConstants.VALIDATOR_PHPCPD_MARKER, false,
								IResource.DEPTH_INFINITE);
					} catch (CoreException e) {
					}

					monitor.worked(++completed);

					createFileMarker(phpcpd.validateResource(resource));

					monitor.worked(++completed);
				}

				return Status.OK_STATUS;
			}
		};

		job.setUser(false);
		job.schedule();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	protected void createFileMarker(IProblem[] problems) {
		for (IProblem problem : problems) {
			IFile file = ((FileProblem) problem).getOriginatingFile();

			try {
				IMarker marker = file.createMarker(IPhpcpdConstants.VALIDATOR_PHPCPD_MARKER);
				marker.setAttribute(IMarker.PROBLEM, true);
				marker.setAttribute(IMarker.LINE_NUMBER, problem.getSourceLineNumber());

				if (problem.isWarning()) {
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
				} else {
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				}

				marker.setAttribute(IMarker.CHAR_START, problem.getSourceStart());
				marker.setAttribute(IMarker.CHAR_END, problem.getSourceEnd());
				marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
			} catch (CoreException e) {
				Logger.logException(e);
			}
		}
	}
}