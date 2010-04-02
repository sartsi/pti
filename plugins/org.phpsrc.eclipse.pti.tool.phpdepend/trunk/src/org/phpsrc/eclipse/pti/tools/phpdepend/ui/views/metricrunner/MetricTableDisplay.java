/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.ui.views.metricrunner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.phpsrc.eclipse.pti.tools.phpdepend.PHPDependPlugin;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricResult;

public class MetricTableDisplay implements IMetricDisplay {
	private final Table fTable;

	private final Image fExceptionIcon = PHPDependPlugin.createImage("obj16/exc_catch.gif"); //$NON-NLS-1$

	private final Image fStackIcon = PHPDependPlugin.createImage("obj16/stkfrm_obj.gif"); //$NON-NLS-1$

	public MetricTableDisplay(Table table) {
		fTable = table;
		fTable.getParent().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				disposeIcons();
			}
		});

		fTable.setHeaderVisible(false);
		fTable.setLinesVisible(true);

		fTable.setRedraw(false);

		TableColumn names = new TableColumn(fTable, SWT.NONE);
		names.setText("Name");
		names.setWidth(100);

		TableColumn ids = new TableColumn(fTable, SWT.NONE);
		ids.setText("Id");
		ids.setWidth(50);

		TableColumn values = new TableColumn(fTable, SWT.NONE);
		values.setText("Value");
		values.setWidth(70);

		fTable.setRedraw(true);
		fTable.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
			}

			public void controlResized(ControlEvent e) {
				Table table = ((Table) e.widget);
				TableColumn column = table.getColumn(0);
				column.setWidth(table.getClientArea().width - 50 - 70);
			}
		});
	}

	public void addMetricResult(MetricResult result) {
		TableItem tableItem = newTableItem();

		String name = "";
		Image img = null;
		if (result.metric != null) {
			name = result.metric.name;
			if (result.hasError()) {
				img = PlatformUI.getWorkbench().getSharedImages().getImage(
						ISharedImages.IMG_OBJS_ERROR_TSK);
				tableItem.setForeground(new Color(tableItem.getDisplay(), 255, 0, 0));
			} else if (result.hasWarning()) {
				img = PlatformUI.getWorkbench().getSharedImages().getImage(
						ISharedImages.IMG_OBJS_WARN_TSK);
			}
		}

		tableItem.setText(new String[] { name, result.id, "" + result.value });
		tableItem.setImage(img);
	}

	public Image getExceptionIcon() {
		return fExceptionIcon;
	}

	public Image getStackIcon() {
		return fStackIcon;
	}

	public Table getTable() {
		return fTable;
	}

	private void disposeIcons() {
		if (fExceptionIcon != null && !fExceptionIcon.isDisposed())
			fExceptionIcon.dispose();
		if (fStackIcon != null && !fStackIcon.isDisposed())
			fStackIcon.dispose();
	}

	TableItem newTableItem() {
		return new TableItem(fTable, SWT.NONE);
	}
}
