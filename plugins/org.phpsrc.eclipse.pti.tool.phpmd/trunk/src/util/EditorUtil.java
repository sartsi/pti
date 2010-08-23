/*******************************************************************************
 * Copyright (c) 2009, 2010 Dejan Spasic
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package util;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.phpsrc.eclipse.pti.tools.phpmd.PhpmdLog;

public class EditorUtil {
	public static void openEditor(IWorkbenchPage page, ISelection selection) {
		if (false == (selection instanceof IStructuredSelection))
			return;

		Iterator<?> it = ((IStructuredSelection) selection).iterator();
		if (false == it.hasNext())
			return;

		Object elm = it.next();

		if (false == (elm instanceof IAdaptable))
			return;

		IFile file = (IFile) ((IAdaptable) elm).getAdapter(IFile.class);
		if (null == file)
			return;

		try {
			IDE.openEditor(page, file);
		} catch (PartInitException e) {
			PhpmdLog.logError(e);
		}
	}
}
