/*******************************************************************************
 * Copyright (c) 2009, 2010 Dejan Spasic
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpmd.core;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

public class TextSelectionResourceCollector extends AbstractResourceCollector {
	public void collect() {
		IEditorInput input = null;
		try {
			input = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
					.getEditorInput();

			if (input != null && input instanceof IFileEditorInput) {
				addResource(((IFileEditorInput) input).getFile());
			}
		} catch (NullPointerException e) {
		}
	}
}
