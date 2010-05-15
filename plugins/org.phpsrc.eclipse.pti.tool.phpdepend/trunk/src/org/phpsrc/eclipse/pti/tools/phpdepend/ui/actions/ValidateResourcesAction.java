/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.ui.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.phpsrc.eclipse.pti.tools.phpdepend.IPHPDependConstants;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.PHPDepend;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.PHPDependOptions;
import org.phpsrc.eclipse.pti.ui.actions.ResourceAction;

public class ValidateResourcesAction extends ResourceAction implements IWorkbenchWindowPulldownDelegate {
	private final static String OPTION_KEY = "OPTION_KEY";
	private SelectionListener optionsListener;

	public ValidateResourcesAction() {
		optionsListener = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				MenuItem item = (MenuItem) e.widget;
				PHPDepend.getInstance().getOptions().put((String) item.getData(OPTION_KEY),
						new Boolean(item.getSelection()));
			}
		};
	}

	public void run(IAction arg0) {
		final IResource[] resources = getSelectedResources();
		if (resources.length > 0) {

			Job job = new Job("PHP Depend") {
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("Validation", resources.length * 2);

					PHPDepend pdepend = PHPDepend.getInstance();

					int completed = 0;
					for (IResource resource : resources) {
						if (monitor.isCanceled())
							return Status.CANCEL_STATUS;

						try {
							resource.deleteMarkers(IPHPDependConstants.VALIDATOR_PHP_DEPEND_MARKER, false,
									IResource.DEPTH_INFINITE);
						} catch (CoreException e) {
						}

						monitor.worked(++completed);
						// createFileMarker(pdepend.validateResource(resource));
						pdepend.validateResource(resource);

						monitor.worked(++completed);
					}

					return Status.OK_STATUS;
				}
			};
			job.setUser(false);
			job.schedule();

		}
	}

	public Menu getMenu(Control parent) {
		Menu menu = new Menu(parent);
		PHPDependOptions options = PHPDepend.getInstance().getOptions();
		for (String key : options.keySet()) {
			MenuItem item = new MenuItem(menu, SWT.CHECK);
			item.setSelection(options.get(key).booleanValue());
			item.setText(key);
			item.setData(OPTION_KEY, key);
			item.addSelectionListener(optionsListener);
		}

		return menu;
	}
}
