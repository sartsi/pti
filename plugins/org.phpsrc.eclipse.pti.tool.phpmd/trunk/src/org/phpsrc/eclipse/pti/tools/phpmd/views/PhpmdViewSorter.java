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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableColumn;

public class PhpmdViewSorter extends ViewerSorter {
	private class SortInfo {
		int columnIndex;
		Comparator<Object> comparator;
		boolean descending;
	}

	private TableViewer viewer;
	private SortInfo[] sortInfos;

	public PhpmdViewSorter(TableViewer viewer, TableColumn[] columns, Comparator<Object>[] comparators) {
		this.viewer = viewer;
		sortInfos = new SortInfo[columns.length];
		for (int index = 0; index < columns.length; ++index) {
			sortInfos[index] = new SortInfo();
			sortInfos[index].columnIndex = index;
			sortInfos[index].comparator = comparators[index];
			sortInfos[index].descending = false;
			createSelectionListener(columns[index], sortInfos[index]);
		}
	}

	private void createSelectionListener(final TableColumn tableColumn, final SortInfo sortInfo) {
		tableColumn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sortUsing(sortInfo);
			}
		});
	}

	protected void sortUsing(SortInfo sortInfo) {
		if (sortInfo == sortInfos[0])
			sortInfo.descending = !sortInfo.descending;
		else {
			for (int index = 0; index < sortInfos.length; ++index) {
				if (sortInfo == sortInfos[index]) {
					System.arraycopy(sortInfos, 0, sortInfos, 1, index);
					sortInfos[0] = sortInfo;
					break;
				}
			}
		}
		viewer.refresh();
	}

	public int compare(Viewer viewer, Object v1, Object v2) {
		for (int index = 0; index < sortInfos.length; ++index) {
			int result = sortInfos[index].comparator.compare(v1, v2);
			if (0 != result) {
				if (sortInfos[index].descending) {
					return -result;
				}
				return result;
			}
		}
		return 0;
	}
}
