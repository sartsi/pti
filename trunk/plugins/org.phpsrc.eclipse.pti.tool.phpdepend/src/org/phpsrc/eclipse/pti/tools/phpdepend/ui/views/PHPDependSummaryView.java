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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.phpsrc.eclipse.pti.core.PHPToolCorePlugin;
import org.phpsrc.eclipse.pti.tools.phpdepend.PHPDependPlugin;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.metrics.elements.IElement;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.metrics.elements.MetricResult;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.metrics.elements.MetricSummary;
import org.phpsrc.eclipse.pti.ui.Logger;
import org.phpsrc.eclipse.pti.ui.images.OverlayImageIcon;

public class PHPDependSummaryView extends ViewPart {

	public static final String VIEW_ID = "org.phpsrc.eclipse.pti.tools.phpdepend.ui.views.summary";
	private static final String ELEMENT_DATA_KEY = "element";

	protected Tree tree;
	protected Table table;
	protected ArrayList<MetricSummary> summaries = new ArrayList<MetricSummary>();
	protected int showIndex = -1;

	protected static final class ElementDecorator implements IElement {
		protected static final ImageRegistry imageRegistry = new ImageRegistry();

		protected final IElement element;

		protected ElementDecorator(IElement element) {
			this.element = element;
		}

		public Image getImage() {
			String key = element.getClass().getName();
			if (element.hasErrors())
				key += "#error";
			else if (element.hasWarnings())
				key += "#warning";
			Image img = imageRegistry.get(key);
			if (img == null) {
				if (element.hasErrors())
					img = new OverlayImageIcon(element.getImage(), PHPToolCorePlugin.getDefault().getImageRegistry()
							.get(PHPToolCorePlugin.IMG_OVERLAY_ERROR), OverlayImageIcon.POS_BOTTOM_LEFT).getImage();
				else if (element.hasWarnings())
					img = new OverlayImageIcon(element.getImage(), PHPToolCorePlugin.getDefault().getImageRegistry()
							.get(PHPToolCorePlugin.IMG_OVERLAY_WARNING), OverlayImageIcon.POS_BOTTOM_LEFT).getImage();
				else
					img = element.getImage();
				imageRegistry.put(key, img);
			}

			return img;
		}

		public String getName() {
			return element.getName();
		}

		public IElement getParent() {
			return element.getParent();
		}

		public IResource getResource() {
			return element.getResource();
		}

		public MetricResult[] getResults() {
			return element.getResults();
		}

		public IElement[] members() {
			return element.members();
		}

		public boolean hasErrors() {
			return element.hasErrors();
		}

		public boolean hasWarnings() {
			return element.hasWarnings();
		}

		public IMarker getFileMarker() {
			return element.getFileMarker();
		}
	}

	public void createPartControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		comp.setLayout(layout);

		tree = new Tree(comp, SWT.VIRTUAL | SWT.BORDER | SWT.FULL_SELECTION);
		tree.setHeaderVisible(false);
		tree.setLinesVisible(false);

		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		tree.setLayoutData(data);

		TreeColumn element = new TreeColumn(tree, SWT.LEFT);
		element.setText("Element");
		element.setWidth(400);

		TreeColumn infos = new TreeColumn(tree, SWT.LEFT);
		infos.setText("Metrics");
		infos.setWidth(400);

		// final Menu treeMenu = new Menu(tree);
		// MenuItem item = new MenuItem(treeMenu, SWT.PUSH);
		// item.setText("open resource");

		tree.addListener(SWT.SetData, new Listener() {
			public void handleEvent(Event event) {
				TreeItem item = (TreeItem) event.item;
				TreeItem parentItem = item.getParentItem();

				IElement element = null;
				if (parentItem == null) {
					element = summaries.get(showIndex);
				} else {
					IElement parentElement = (IElement) parentItem.getData(ELEMENT_DATA_KEY);
					IElement[] members = parentElement.members();
					element = members[parentItem.indexOf(item)];
				}
				ElementDecorator decorator = new ElementDecorator(element);

				StringBuffer m = new StringBuffer();
				for (MetricResult r : decorator.getResults()) {
					if (m.length() > 0)
						m.append(", ");
					m.append(r.id + ": " + r.value);
				}

				item.setData(ELEMENT_DATA_KEY, decorator);
				item.setText(new String[] { decorator.getName(), m.toString() });
				item.setImage(decorator.getImage());
				int length = decorator.members().length;
				item.setItemCount(length);

				for (TreeColumn c : tree.getColumns()) {
					c.pack();
				}
			}
		});

		tree.addMouseListener(new MouseListener() {

			public void mouseUp(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseDoubleClick(MouseEvent e) {
				if (e.button == 1 && e.count == 2) {
					Tree t = (Tree) e.widget;
					TreeItem[] items = t.getSelection();
					if (items.length > 0) {
						IElement element = (IElement) items[0].getData(ELEMENT_DATA_KEY);
						if (element != null) {
							IMarker m = element.getFileMarker();
							if (m != null) {
								try {
									IDE.openEditor(
											PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), m);
								} catch (PartInitException e1) {
									Logger.logException(e1);
								}
								try {
									m.delete();
								} catch (CoreException e1) {
									Logger.logException(e1);
								}
							}
						}
					}
				}
			}
		});

		tree.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				TreeItem select = (TreeItem) e.item;
				table.removeAll();
				IElement element = (IElement) select.getData(ELEMENT_DATA_KEY);
				if (element != null) {
					for (MetricResult result : element.getResults()) {
						TableItem item = new TableItem(table, SWT.NULL);
						String name = "";
						Image img = null;
						if (result.metric != null) {
							name = result.metric.name;
							if (result.hasError()) {
								img = PlatformUI.getWorkbench().getSharedImages().getImage(
										ISharedImages.IMG_OBJS_ERROR_TSK);
								item.setForeground(new Color(item.getDisplay(), 255, 0, 0));
							} else if (result.hasWarning()) {
								img = PlatformUI.getWorkbench().getSharedImages().getImage(
										ISharedImages.IMG_OBJS_WARN_TSK);
							}
						}

						item.setText(new String[] { name, result.id, "" + result.value });
						item.setImage(img);
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		table = new Table(comp, SWT.BORDER | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		data.grabExcessVerticalSpace = true;
		data.minimumWidth = 320;
		table.setLayoutData(data);

		TableColumn names = new TableColumn(table, SWT.NONE);
		names.setText("Name");
		names.setWidth(200);

		TableColumn ids = new TableColumn(table, SWT.NONE);
		ids.setText("Id");
		ids.setWidth(50);

		TableColumn values = new TableColumn(table, SWT.NONE);
		values.setText("Value");
		values.setWidth(70);

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
						Point point = ti.getParent().toDisplay(new Point(e.x, e.y + ti.getBounds().height));
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

					new MenuItem(listMenu, SWT.SEPARATOR);
					MenuItem m = new MenuItem(listMenu, SWT.PUSH);
					m.setText("Clear History");
					m.addSelectionListener(new SelectionListener() {
						public void widgetSelected(SelectionEvent e) {
							tree.removeAll();
							table.removeAll();
							showIndex = -1;
							int size = summaries.size();
							for (int i = size - 1; i >= 0; i--) {
								summaries.remove(i);
							}
						}

						public void widgetDefaultSelected(SelectionEvent e) {
						}
					});

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
		tree.setFocus();
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
