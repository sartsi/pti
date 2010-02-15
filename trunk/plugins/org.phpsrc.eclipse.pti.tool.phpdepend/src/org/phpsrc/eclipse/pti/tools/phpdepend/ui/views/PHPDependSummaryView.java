/*******************************************************************************
 * Copyright (c) 2010, Sven Kiera
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * - Neither the name of the Organisation nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.ui.views;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.phpsrc.eclipse.pti.tools.phpdepend.PHPDependPlugin;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.metrics.elements.IElement;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.metrics.elements.MetricResult;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.metrics.elements.MetricSummary;

public class PHPDependSummaryView extends ViewPart {

	public static final String VIEW_ID = "org.phpsrc.eclipse.pti.tools.phpdepend.ui.views.summary";

	protected Tree tree;
	protected ArrayList<MetricSummary> summaries = new ArrayList<MetricSummary>();
	protected int showIndex = -1;

	public void createPartControl(Composite parent) {
		tree = new Tree(parent, SWT.VIRTUAL | SWT.BORDER | SWT.FULL_SELECTION);
		tree.setHeaderVisible(false);

		TreeColumn element = new TreeColumn(tree, SWT.LEFT);
		element.setText("Element");
		element.setWidth(400);

		TreeColumn infos = new TreeColumn(tree, SWT.LEFT);
		infos.setText("Metrics");
		infos.setWidth(400);

		tree.addListener(SWT.SetData, new Listener() {
			public void handleEvent(Event event) {
				TreeItem item = (TreeItem) event.item;
				TreeItem parentItem = item.getParentItem();

				IElement element = null;
				if (parentItem == null) {
					element = summaries.get(showIndex);
				} else {
					IElement parentElement = (IElement) parentItem.getData("element");
					IElement[] members = parentElement.members();
					element = members[parentItem.indexOf(item)];
				}

				StringBuffer m = new StringBuffer();
				for (MetricResult r : element.getResults()) {
					if (m.length() > 0)
						m.append(", ");
					m.append(r.id + ": " + r.value);
				}

				item.setData("element", element);
				item.setText(new String[] { element.getName(), m.toString() });
				item.setImage(element.getImage());
				int length = element.members().length;
				item.setItemCount(length);

				for (TreeColumn c : tree.getColumns()) {
					c.pack();
				}
			}
		});

		createToolbar();
	}

	private void createToolbar() {
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		Action history = new Action("History", Action.AS_DROP_DOWN_MENU) {
			public void runWithEvent(Event e) {
				IMenuCreator mc = getMenuCreator();
				if (mc != null) {
					ToolItem ti = (ToolItem) e.widget;
					Menu m = mc.getMenu(ti.getParent());
					if (m != null) {
						Point point = ti.getParent().toDisplay(new Point(e.x, e.y + 21));
						m.setLocation(point.x, point.y);
						m.setVisible(true);
					}
				}
			}
		};
		history.setImageDescriptor(PHPDependPlugin.getDefault().getImageRegistry().getDescriptor(
				PHPDependPlugin.IMG_PHP_DEPEND));
		history.setMenuCreator(new IMenuCreator() {
			private Menu listMenu;

			public Menu getMenu(Menu parent) {
				return null;
			}

			public Menu getMenu(Control parent) {
				if (listMenu != null)
					listMenu.dispose();

				listMenu = new Menu(parent);

				int length = summaries.size();
				if (length == 0) {
					MenuItem m = new MenuItem(listMenu, SWT.CHECK);
					m.setText("Empty");
					m.setEnabled(false);
				} else {
					for (int i = length - 1; i >= 0; i--) {
						MetricSummary s = summaries.get(i);
						MenuItem m = new MenuItem(listMenu, SWT.CHECK);
						m.setText(s.getGenerated().toString());
						m.setSelection(i == showIndex);
						m.setEnabled(i != showIndex);
						m.setData("index", new Integer(i));
						m.addSelectionListener(new SelectionListener() {
							public void widgetSelected(SelectionEvent e) {
								MenuItem item = (MenuItem) e.getSource();
								Integer index = (Integer) item.getData("index");
								showIndex = index.intValue();
								tree.removeAll();
								tree.setItemCount(1);
							}

							public void widgetDefaultSelected(SelectionEvent e) {
							}
						});
					}
				}

				return listMenu;
			}

			public void dispose() {
				if (listMenu != null)
					listMenu.dispose();
			}
		});

		mgr.add(new ActionContributionItem(history));
	}

	public void setFocus() {

	}

	public static void showSummary(MetricSummary summary) {
		Assert.isNotNull(summary);

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				PHPDependSummaryView view = (PHPDependSummaryView) page.findView(VIEW_ID);
				if (view == null) {
					try {
						view = (PHPDependSummaryView) page.showView(VIEW_ID);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				} else {
					page.bringToTop(view);
				}

				if (view != null) {
					view.summaries.add(summary);
					view.showIndex = view.summaries.size() - 1;
					if (view.tree.getItemCount() > 0)
						view.tree.removeAll();
					view.tree.setItemCount(1);
				}
			}
		}
	}
}
