/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sebastian Davids: sdavids@gmx.de bug 37333, 26653
 *     Johan Walles: walles@mailblocks.com bug 68737
 *******************************************************************************/
package org.phpsrc.eclipse.pti.tools.phpdepend.ui.views.metricrunner;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.IMetricElement;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricResult;

/**
 * A pane that shows a stack trace of a failed test.
 */
public class MetricTrace implements IMenuListener {
	private static final int MAX_LABEL_LENGTH = 256;

	private Table fTable;
	private MetricRunnerViewPart fMetricRunner;
	private final Clipboard fClipboard;
	private IMetricElement fElement;
	private final MetricTableDisplay fMetricTableDisplay;

	public MetricTrace(Composite parent, Clipboard clipboard, MetricRunnerViewPart testRunner, ToolBar toolBar) {
		Assert.isNotNull(clipboard);

		fTable = new Table(parent, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		fMetricRunner = testRunner;
		fClipboard = clipboard;

		OpenStrategy handler = new OpenStrategy(fTable);
		initMenu();

		fMetricTableDisplay = new MetricTableDisplay(fTable);
	}

	private void initMenu() {
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(this);
		Menu menu = menuMgr.createContextMenu(fTable);
		fTable.setMenu(menu);
	}

	public void menuAboutToShow(IMenuManager manager) {
	}

	private String getSelectedText() {
		return fTable.getSelection()[0].getText();
	}

	private Action createOpenEditorAction(String traceLine) {
		try {
			int pos = traceLine.lastIndexOf(':');
			if (pos != -1) {
				String fileName = traceLine.substring(0, pos);
				int lineNumber = Integer.parseInt(traceLine.substring(pos + 1));
				return new OpenEditorAtLineAction(fMetricRunner, fileName, lineNumber);
			}
		} catch (NumberFormatException e) {
		} catch (IndexOutOfBoundsException e) {
		}
		return null;
	}

	/**
	 * Returns the composite used to present the trace
	 * 
	 * @return The composite
	 */
	Composite getComposite() {
		return fTable;
	}

	/**
	 * Refresh the table from the trace.
	 */
	public void refresh() {
		updateTable(fElement);
	}

	/**
	 * Shows a TestFailure
	 * 
	 * @param test
	 *            the failed test
	 */
	public void showElement(IMetricElement element) {
		fElement = element;
		updateTable(element);
	}

	public void updateEnablement(IMetricElement test) {
	}

	private void updateTable(IMetricElement fElement) {
		if (fElement == null) { //$NON-NLS-1$
			clear();
			return;
		}

		fTable.setRedraw(false);
		fTable.removeAll();

		for (MetricResult result : fElement.getResults()) {
			fMetricTableDisplay.addMetricResult(result);
		}

		fTable.setRedraw(true);
	}

	private String[] getFilterPatterns() {
		return new String[0];
	}

	/**
	 * Shows other information than a stack trace.
	 * 
	 * @param text
	 *            the informational message to be shown
	 */
	public void setInformation(String text) {
		clear();
		TableItem tableItem = fMetricTableDisplay.newTableItem();
		tableItem.setText(text);
	}

	/**
	 * Clears the non-stack trace info
	 */
	public void clear() {
		fTable.removeAll();
	}

	public Shell getShell() {
		return fTable.getShell();
	}

	public MetricTableDisplay getFailureTableDisplay() {
		return fMetricTableDisplay;
	}
}
