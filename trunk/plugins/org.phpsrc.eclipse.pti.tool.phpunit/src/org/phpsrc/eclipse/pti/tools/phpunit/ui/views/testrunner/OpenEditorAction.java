/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.phpsrc.eclipse.pti.tools.phpunit.ui.views.testrunner;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.internal.junit.ui.JUnitMessages;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Abstract Action for opening a Java editor.
 */
public abstract class OpenEditorAction extends Action {
	protected String fFilePath;
	protected TestRunnerViewPart fTestRunner;
	private final boolean fActivate;

	protected OpenEditorAction(TestRunnerViewPart testRunner, String filePath) {
		this(testRunner, filePath, true);
	}

	public OpenEditorAction(TestRunnerViewPart testRunner, String filePath, boolean activate) {
		super(JUnitMessages.OpenEditorAction_action_label);
		fFilePath = filePath;
		fTestRunner = testRunner;
		fActivate = activate;
	}

	/*
	 * @see IAction#run()
	 */
	public void run() {
		ITextEditor textEditor = null;

		IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(
				new java.io.File(fFilePath).toURI());
		if (files.length > 0) {
			IFile file = files[0];
			if (file.exists()) {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window != null) {
					IWorkbenchPage page = window.getActivePage();

					IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(
							file.getName());

					try {
						IEditorPart editor = page.openEditor(new FileEditorInput(file), desc.getId());
						if (editor instanceof ITextEditor)
							reveal((ITextEditor) editor);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	protected Shell getShell() {
		return fTestRunner.getSite().getShell();
	}

	/**
	 * @return the Java project, or <code>null</code>
	 */
	protected IProject getLaunchedProject() {
		return fTestRunner.getLaunchedProject();
	}

	protected String getFilePath() {
		return fFilePath;
	}

	protected abstract void reveal(ITextEditor editor);
}
