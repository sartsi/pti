/*******************************************************************************
 * Copyright (c) 2009, 2010 Dejan Spasic
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpmd.views;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.phpsrc.eclipse.pti.tools.phpmd.model.IViolation;

public class PhpmdViewLableProvider extends LabelProvider implements ITableLabelProvider {
	public String getColumnText(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0: // Class name column
			if (element instanceof IViolation)
				return ((IViolation) element).getClassName();
			if (null != element)
				return element.toString();
			return "";
		case 1: // RuleSet column
			if (element instanceof IViolation)
				return ((IViolation) element).getRuleSet();
			return "";
		case 2: // priority column
			if (element instanceof IViolation)
				return String.valueOf(((IViolation) element).getPriority());
			return "";
		default:
			return "";
		}
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}
}
