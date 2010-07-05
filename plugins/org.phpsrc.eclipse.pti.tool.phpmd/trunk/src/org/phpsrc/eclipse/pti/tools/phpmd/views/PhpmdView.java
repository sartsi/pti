/*******************************************************************************
 * Copyright (c) 2009, 2010 Dejan Spasic
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpmd.views;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.phpsrc.eclipse.pti.tools.phpmd.model.ViolationManager;

public class PhpmdView extends ViewPart {
	private TableViewer tableViewer;

	@Override
	public void createPartControl(Composite parent) {
		createTableViewer(parent);

		final Table table = createTable();
		createNameColumn(table);
		createRuleSetColumn(table);
		createPriorityColumn(table);
	}

	private void createTableViewer(Composite parent) {
		tableViewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.setContentProvider(new PhpmdViewContentProvider());
		tableViewer.setLabelProvider(new PhpmdViewLableProvider());
		tableViewer.setInput(ViolationManager.getManager());
	}

	private Table createTable() {
		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		return table;
	}

	private TableColumn createNameColumn(Table table) {
		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setText("Name");
		column.setWidth(450);
		return column;
	}

	private TableColumn createRuleSetColumn(Table table) {
		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setText("Ruleset");
		column.setWidth(180);
		return column;
	}

	private TableColumn createPriorityColumn(Table table) {
		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setText("Priority");
		column.setWidth(180);
		return column;
	}

	@Override
	public void setFocus() {
	}
}
