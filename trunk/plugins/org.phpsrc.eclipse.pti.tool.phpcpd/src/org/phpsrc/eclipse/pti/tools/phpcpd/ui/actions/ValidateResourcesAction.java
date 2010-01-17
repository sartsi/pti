/*******************************************************************************
 * Copyright (c) 2010, Sven Kiera
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.jface.action.IAction;
import org.phpsrc.eclipse.pti.core.compiler.problem.FileProblem;
import org.phpsrc.eclipse.pti.tools.phpcpd.IPhpcpdConstants;
import org.phpsrc.eclipse.pti.tools.phpcpd.core.Phpcpd;
import org.phpsrc.eclipse.pti.ui.Logger;
import org.phpsrc.eclipse.pti.ui.actions.ResourceAction;

public class ValidateResourcesAction extends ResourceAction {

	@Override
	public void run(IAction action) {
		final IResource[] resources = getSelectedResources();
		Job job = new Job("PHP Copy/Paste Detector") {
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
