/*******************************************************************************
 * Copyright (c) 2009, 2010 Dejan Spasic
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpmd.core;

import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;

public interface IResourceCollector {
	public void collect();

	public void setSelection(ISelection selection);

	public ISelection getSelection();

	public ArrayList<IResource> getResources();
}
