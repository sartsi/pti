/*******************************************************************************
 * Copyright (c) 2009, 2010 Dejan Spasic
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpmd.views;

import java.util.Comparator;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.phpsrc.eclipse.pti.tools.phpmd.model.IViolation;
import org.phpsrc.eclipse.pti.tools.phpmd.model.ViolationManager;

public class PhpmdView extends ViewPart {
	private TableViewer tableViewer;

	private TableColumn filenaColumn;
	private TableColumn priorityColumn;
	private TableColumn categoryColumn;
	private TableColumn ruleColumn;

	private PhpmdViewSorter sorter;

	@Override
	public void createPartControl(Composite parent) {
		createTableViewer(parent);

		final Table table = createTable();
		createFileNameColumn(table);
		createPriorityColumn(table);
		createRuleColumn(table);
		createCategoryColumn(table);

		createTableSorter();
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

	private TableColumn createFileNameColumn(Table table) {
		if (null == filenaColumn) {
			filenaColumn = new TableColumn(table, SWT.LEFT);
			filenaColumn.setText("Filename");
			filenaColumn.setWidth(200);
		}
		return filenaColumn;
	}

	private TableColumn createPriorityColumn(Table table) {
		if (null == priorityColumn) {
			priorityColumn = new TableColumn(table, SWT.LEFT);
			priorityColumn.setText("Priority");
			priorityColumn.setWidth(180);
		}
		return priorityColumn;
	}

	private TableColumn createRuleColumn(Table table) {
		if (null == ruleColumn) {
			ruleColumn = new TableColumn(table, SWT.LEFT);
			ruleColumn.setText("Rule");
			ruleColumn.setWidth(180);
		}
		return ruleColumn;
	}

	private TableColumn createCategoryColumn(Table table) {
		if (null == categoryColumn) {
			categoryColumn = new TableColumn(table, SWT.LEFT);
			categoryColumn.setText("Category");
			categoryColumn.setWidth(180);
		}
		return categoryColumn;
	}

	private void createTableSorter() {
		Comparator<IViolation> filenameComparator = new Comparator<IViolation>() {
			public int compare(IViolation v1, IViolation v2) {
				return v1.getFileName().compareTo(v2.getFileName());
			}
		};

		Comparator<IViolation> priorityComparator = new Comparator<IViolation>() {
			public int compare(IViolation v1, IViolation v2) {
				if (v1.getPriority() == v2.getPriority())
					return 0;
				return v1.getPriority() > v2.getPriority() ? 1 : -1;
			}
		};

		Comparator<IViolation> categoryComparator = new Comparator<IViolation>() {
			public int compare(IViolation v1, IViolation v2) {
				return v1.getRuleSet().compareTo(v2.getRuleSet());
			}
		};

		Comparator<IViolation> ruleComparator = new Comparator<IViolation>() {
			public int compare(IViolation v1, IViolation v2) {
				return v1.getRule().compareTo(v2.getRule());
			}
		};

		sorter = new PhpmdViewSorter(tableViewer, new TableColumn[] { filenaColumn, priorityColumn, ruleColumn,
				categoryColumn, }, new Comparator[] { filenameComparator, priorityComparator, ruleComparator,
				categoryComparator, });

		// tableViewer.setSorter(sorter);
	}

	@Override
	public void setFocus() {
	}
}
