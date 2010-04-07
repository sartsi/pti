package org.phpsrc.eclipse.pti.validators.externalchecker.ui;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.phpsrc.eclipse.pti.validators.externalchecker.core.Rule;

public class RulesLabelProvider extends LabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return ((Rule) element).getDescription();
		case 1:
			return ((Rule) element).getType();
		default:
			return null;
		}
	}
}
