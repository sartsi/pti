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
package org.phpsrc.eclipse.pti.tools.phpunit.ui.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.core.IOpenable;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.validation.ValidationState;
import org.phpsrc.eclipse.pti.core.PHPToolCorePlugin;
import org.phpsrc.eclipse.pti.core.PHPToolkitUtil;
import org.phpsrc.eclipse.pti.core.search.PHPSearchEngine;
import org.phpsrc.eclipse.pti.tools.phpunit.PHPUnitPlugin;
import org.phpsrc.eclipse.pti.tools.phpunit.validator.PHPUnitValidator;
import org.phpsrc.eclipse.pti.ui.Logger;

public class RunTestCaseAction implements IObjectActionDelegate, IEditorActionDelegate {
	private IResource[] files;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		ISelection selection = targetPart.getSite().getSelectionProvider().getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			files = new IResource[structuredSelection.size()];

			ArrayList<IResource> resources = new ArrayList<IResource>(structuredSelection.size());

			Iterator<?> iterator = structuredSelection.iterator();
			while (iterator.hasNext()) {
				Object entry = iterator.next();
				try {
					if (entry instanceof ISourceModule) {
						IFile file = (IFile) ((ISourceModule) entry).getCorrespondingResource();

						if (PHPToolkitUtil.isPhpFile(file)) {
							resources.add(((ISourceModule) entry).getCorrespondingResource());
						}
					} else if (entry instanceof IOpenable) {
						resources.add(((IOpenable) entry).getCorrespondingResource());
					}
				} catch (ModelException e) {
					Logger.logException(e);
				}
			}

			files = resources.toArray(new IResource[0]);
		}
	}

	public void run(IAction action) {
		if (files != null) {
			for (IResource file : files) {
				if (file instanceof IFile) {
					searchTestCase((IFile) file);
				}
			}
		}
	}

	protected void searchTestCase(IFile file) {
		ISourceModule module = PHPToolkitUtil.getSourceModule(file);

		ArrayList<IFile> testFiles = new ArrayList<IFile>();

		IType[] types;
		try {
			types = module.getAllTypes();
			if (types.length > 0) {
				String[] classes = types[0].getSuperClasses();
				if (classes.length > 0 && classes[0].equals("PHPUnit_Framework_TestCase")) {
					testFiles.add(file);
				} else {
					SearchMatch[] matches = PHPSearchEngine.findClass(types[0].getElementName() + "Test",
							PHPSearchEngine.createProjectScope(file.getProject()));

					if (matches.length > 0)
						testFiles.add((IFile) matches[0].getResource());
				}
			}
		} catch (ModelException e) {
			e.printStackTrace();
		}

		if (testFiles.size() > 0) {
			Iterator<IFile> iterator = testFiles.iterator();
			while (iterator.hasNext())
				runFile(iterator.next());
		} else {
			System.out.println("not found!");
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					ErrorDialog.openError(PHPToolCorePlugin.getActiveWorkbenchShell(), "Error",
							"Can't execute test case.", new Status(IStatus.ERROR, PHPUnitPlugin.PLUGIN_ID,
									"No test case class found."));
				}
			});
		}
	}

	protected void runFile(final IFile file) {
		Job job = new Job("PHPUnit") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Run " + file.getProjectRelativePath().toString(), IProgressMonitor.UNKNOWN);

				PHPUnitValidator validator = new PHPUnitValidator();
				validator.validate(file, IResourceDelta.NO_CHANGE, new ValidationState(), monitor);

				return Status.OK_STATUS;
			}
		};

		job.setUser(false);
		job.schedule();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void setActiveEditor(IAction action, IEditorPart targetPart) {
		if (targetPart != null) {
			IEditorInput iei = targetPart.getEditorInput();
			if (iei instanceof IFileEditorInput) {
				IFileEditorInput ifei = (IFileEditorInput) iei;
				files = new IResource[] { ifei.getFile() };
			}
		}
	}
}
