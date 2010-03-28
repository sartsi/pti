/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.phpsrc.eclipse.pti.tools.phpdepend.ui.views.metricrunner;

import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.phpsrc.eclipse.pti.core.PHPToolkitUtil;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricClass;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricFile;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricMethod;

/**
 * Open a class on a Test method.
 */
public class OpenFileAction extends OpenEditorAction {

	private String fClassName;
	private String fMethodName;
	private IMethod fMethod;
	private int fLineNumber = -1;

	public OpenFileAction(MetricRunnerViewPart testRunnerPart, MetricMethod method) {
		this(testRunnerPart, (MetricClass) method.getParent());
		fMethodName = method.getName();
	}

	public OpenFileAction(MetricRunnerViewPart testRunnerPart, MetricClass c) {
		this(testRunnerPart, (IFile) c.getResource(), true);
		fClassName = c.getName();
	}

	public OpenFileAction(MetricRunnerViewPart testRunnerPart, MetricFile file) {
		this(testRunnerPart, (IFile) file.getResource(), true);
	}

	public OpenFileAction(MetricRunnerViewPart testRunner, String className) {
		this(testRunner, className, null, true);
	}

	private OpenFileAction(MetricRunnerViewPart testRunner, String className, String method, boolean activate) {
		this(testRunner, getFileForClassName(className, method), activate);
		fMethodName = method;
		fClassName = className;
	}

	private OpenFileAction(MetricRunnerViewPart testRunner, IFile file, boolean activate) {
		super(testRunner, file, activate);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IPHPDependHelpContextIds.OPENTEST_ACTION);
	}

	protected void reveal(ITextEditor textEditor) {
		if (fClassName != null) {
			ISourceModule modul = PHPToolkitUtil.getSourceModule(getFile());
			int offset = -1;
			try {
				for (IType c : modul.getTypes()) {
					if (fClassName.equals(c.getElementName())) {
						offset = c.getSourceRange().getOffset();

						if (fMethodName != null) {
							for (IModelElement m : c.getChildren()) {
								if (m instanceof IMethod && fMethodName.equals(m.getElementName())) {
									offset = ((IMethod) m).getNameRange().getOffset();
									break;
								}
							}
						}

						break;
					}
				}
			} catch (ModelException e) {
			}

			if (offset >= 0) {
				IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
				try {
					int line = document.getLineOfOffset(offset);
					textEditor.selectAndReveal(document.getLineOffset(line), document.getLineLength(line));
				} catch (BadLocationException e) {
				}
			}
		}
	}
}
