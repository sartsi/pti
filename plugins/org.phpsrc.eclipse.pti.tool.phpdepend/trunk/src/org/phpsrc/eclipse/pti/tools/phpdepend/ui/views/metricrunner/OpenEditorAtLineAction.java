/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.ui.views.metricrunner;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Open a test in the Java editor and reveal a given line
 */
public class OpenEditorAtLineAction extends OpenEditorAction {

	private int fLineNumber;

	public OpenEditorAtLineAction(MetricRunnerViewPart testRunner, String filePath, int line) {
		super(testRunner, filePath);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
				IPHPDependHelpContextIds.OPENEDITORATLINE_ACTION);
		fLineNumber = line;
	}

	protected void reveal(ITextEditor textEditor) {
		if (fLineNumber >= 0) {

			IDocument document = textEditor.getDocumentProvider().getDocument(
					textEditor.getEditorInput());
			try {
				textEditor.selectAndReveal(document.getLineOffset(fLineNumber - 1), document
						.getLineLength(fLineNumber - 1));
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
}
