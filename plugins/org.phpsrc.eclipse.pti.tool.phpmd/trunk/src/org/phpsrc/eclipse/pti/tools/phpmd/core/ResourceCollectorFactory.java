/*******************************************************************************
 * Copyright (c) 2009, 2010 Dejan Spasic
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpmd.core;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

public class ResourceCollectorFactory {
	public static IResourceCollector factory(ISelection selection) {
		IResourceCollector collector = null;

		if (selection instanceof ITextSelection) {
			collector = new TextSelectionResourceCollector();
		} else if (selection instanceof IStructuredSelection) {
			collector = new StructuredSelectionResourceCollector();
		}

		if (null != collector) {
			collector.setSelection(selection);
		}

		return collector;
	}
}
