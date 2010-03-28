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

/**
 * A pane that shows a stack trace of a failed test.
 */
public class FailureTrace implements IMenuListener {
	private static final int MAX_LABEL_LENGTH = 256;

	private Table fTable;
	private MetricRunnerViewPart fTestRunner;
	private String fInputTrace;
	private final Clipboard fClipboard;
	private IMetricElement fFailure;
	private final FailureTableDisplay fFailureTableDisplay;

	public FailureTrace(Composite parent, Clipboard clipboard, MetricRunnerViewPart testRunner, ToolBar toolBar) {
		Assert.isNotNull(clipboard);

		fTable = new Table(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		fTestRunner = testRunner;
		fClipboard = clipboard;

		OpenStrategy handler = new OpenStrategy(fTable);
		initMenu();

		fFailureTableDisplay = new FailureTableDisplay(fTable);
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

	public String getTrace() {
		return fInputTrace;
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
				return new OpenEditorAtLineAction(fTestRunner, fileName, lineNumber);
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
		updateTable(fInputTrace);
	}

	/**
	 * Shows a TestFailure
	 * 
	 * @param test
	 *            the failed test
	 */
	public void showFailure(IMetricElement test) {
		fFailure = test;
		String trace = ""; //$NON-NLS-1$
		updateEnablement(test);
		// if (test != null)
		// trace = test.getTrace();
		if (fInputTrace == trace)
			return;
		fInputTrace = trace;
		updateTable(trace);
	}

	public void updateEnablement(IMetricElement test) {
	}

	private void updateTable(String trace) {
		if (trace == null || trace.trim().equals("")) { //$NON-NLS-1$
			clear();
			return;
		}
		trace = trace.trim();
		fTable.setRedraw(false);
		fTable.removeAll();
		new TextualTrace(trace, getFilterPatterns()).display(fFailureTableDisplay, MAX_LABEL_LENGTH);
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
		TableItem tableItem = fFailureTableDisplay.newTableItem();
		tableItem.setText(text);
	}

	/**
	 * Clears the non-stack trace info
	 */
	public void clear() {
		fTable.removeAll();
		fInputTrace = null;
	}

	public IMetricElement getFailedTest() {
		return fFailure;
	}

	public Shell getShell() {
		return fTable.getShell();
	}

	public FailureTableDisplay getFailureTableDisplay() {
		return fFailureTableDisplay;
	}
}
