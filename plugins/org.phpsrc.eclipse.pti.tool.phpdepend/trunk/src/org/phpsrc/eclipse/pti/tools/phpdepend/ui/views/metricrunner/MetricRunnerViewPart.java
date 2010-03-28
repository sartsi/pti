/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Julien Ruaux: jruaux@octo.com see bug 25324 Ability to know when tests are finished [junit]
 *     Vincent Massol: vmassol@octo.com 25324 Ability to know when tests are finished [junit]
 *     Sebastian Davids: sdavids@gmx.de 35762 JUnit View wasting a lot of screen space [JUnit]
 *     Brock Janiczak (brockj@tpg.com.au)
 *         - https://bugs.eclipse.org/bugs/show_bug.cgi?id=102236: [JUnit] display execution time next to each test
 *******************************************************************************/
package org.phpsrc.eclipse.pti.tools.phpdepend.ui.views.metricrunner;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.ElementChangedEvent;
import org.eclipse.dltk.core.IElementChangedListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.php.internal.ui.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.part.PageSwitcher;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.ui.progress.UIJob;
import org.phpsrc.eclipse.pti.core.Messages;
import org.phpsrc.eclipse.pti.tools.phpdepend.PHPDependPlugin;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.IMetricElement;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.IMetricRunSessionListener;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricRunSession;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.PHPDependModel;
import org.phpsrc.eclipse.pti.ui.viewsupport.BasicElementLabels;
import org.phpsrc.eclipse.pti.ui.viewsupport.ViewHistory;

/**
 * A ViewPart that shows the results of a test run.
 */
public class MetricRunnerViewPart extends ViewPart {

	public static final String NAME = "org.phpsrc.eclipse.pti.tools.phpdepend.ui.views.metricrunner"; //$NON-NLS-1$

	static final int REFRESH_INTERVAL = 200;

	static final int LAYOUT_FLAT = 0;
	static final int LAYOUT_HIERARCHICAL = 1;

	/**
	 * The current orientation; either <code>VIEW_ORIENTATION_HORIZONTAL</code>
	 * <code>VIEW_ORIENTATION_VERTICAL</code>, or
	 * <code>VIEW_ORIENTATION_AUTOMATIC</code>.
	 */
	private int fOrientation = VIEW_ORIENTATION_AUTOMATIC;
	/**
	 * The current orientation; either <code>VIEW_ORIENTATION_HORIZONTAL</code>
	 * <code>VIEW_ORIENTATION_VERTICAL</code>.
	 */
	private int fCurrentOrientation;
	/**
	 * The current layout mode (LAYOUT_FLAT or LAYOUT_HIERARCHICAL).
	 */
	private int fLayout = LAYOUT_HIERARCHICAL;

	// private boolean fTestIsRunning= false;

	protected Image fViewImage;
	protected boolean fShowOnErrorOnly = false;
	protected Clipboard fClipboard;
	protected volatile String fInfoMessage;

	private MetricTrace fMetricList;

	private MetricViewer fTestViewer;
	/**
	 * Is the UI disposed?
	 */
	private boolean fIsDisposed = false;

	/**
	 * Actions
	 */
	private Action fNextAction;
	private Action fPreviousAction;

	private Action fFailuresOnlyFilterAction;
	private ToggleOrientationAction[] fToggleOrientationActions;
	private ShowTestHierarchyAction fShowTestHierarchyAction;
	private ActivateOnErrorAction fActivateOnErrorAction;
	private IMenuListener fViewMenuListener;

	private MetricRunSession fMetricRunSession;

	private RunnerViewHistory fViewHistory;
	private MetricRunSessionListener fMetricRunSessionListener;

	final Image fStackViewIcon;

	final List fImagesToDispose;

	// Persistence tags.
	static final String TAG_PAGE = "page"; //$NON-NLS-1$
	static final String TAG_RATIO = "ratio"; //$NON-NLS-1$
	static final String TAG_TRACEFILTER = "tracefilter"; //$NON-NLS-1$
	static final String TAG_ORIENTATION = "orientation"; //$NON-NLS-1$
	static final String TAG_SCROLL = "scroll"; //$NON-NLS-1$
	static final String TAG_LAYOUT = "layout"; //$NON-NLS-1$
	static final String TAG_FAILURES_ONLY = "failuresOnly"; //$NON-NLS-1$

	static final String PREF_LAST_PATH = "lastImportExportPath"; //$NON-NLS-1$

	// orientations
	static final int VIEW_ORIENTATION_VERTICAL = 0;
	static final int VIEW_ORIENTATION_HORIZONTAL = 1;
	static final int VIEW_ORIENTATION_AUTOMATIC = 2;

	private IMemento fMemento;

	Image fOriginalViewImage;
	IElementChangedListener fDirtyListener;

	// private CTabFolder fTabFolder;
	private SashForm fSashForm;

	private Composite fParent;

	/**
	 * A Job that periodically updates view description, counters, and progress
	 * bar.
	 */
	private UpdateUIJob fUpdateJob;

	public static final Object FAMILY_JUNIT_RUN = new Object();

	private IPartListener2 fPartListener = new IPartListener2() {
		public void partActivated(IWorkbenchPartReference ref) {
		}

		public void partBroughtToTop(IWorkbenchPartReference ref) {
		}

		public void partInputChanged(IWorkbenchPartReference ref) {
		}

		public void partClosed(IWorkbenchPartReference ref) {
		}

		public void partDeactivated(IWorkbenchPartReference ref) {
		}

		public void partOpened(IWorkbenchPartReference ref) {
		}

		public void partVisible(IWorkbenchPartReference ref) {
			if (getSite().getId().equals(ref.getId())) {
				fPartIsVisible = true;
			}
		}

		public void partHidden(IWorkbenchPartReference ref) {
			if (getSite().getId().equals(ref.getId())) {
				fPartIsVisible = false;
			}
		}
	};

	protected boolean fPartIsVisible = false;

	private class RunnerViewHistory extends ViewHistory {

		public void configureHistoryListAction(IAction action) {
			action.setText(PHPDependMessages.TestRunnerViewPart_history);
		}

		public void configureHistoryDropDownAction(IAction action) {
			action.setToolTipText(PHPDependMessages.TestRunnerViewPart_test_run_history);
			PHPDependPlugin.setLocalImageDescriptors(action, "history_list.gif"); //$NON-NLS-1$
		}

		public Action getClearAction() {
			return new ClearAction();
		}

		public String getHistoryListDialogTitle() {
			return PHPDependMessages.TestRunnerViewPart_test_runs;
		}

		public String getHistoryListDialogMessage() {
			return PHPDependMessages.TestRunnerViewPart_select_test_run;
		}

		public Shell getShell() {
			return fParent.getShell();
		}

		public List getHistoryEntries() {
			return PHPDependPlugin.getModel().getMetricRunSessions();
		}

		public Object getCurrentEntry() {
			return fMetricRunSession;
		}

		public void setActiveEntry(Object entry) {
			MetricRunSession deactivatedSession = setActiveMetricRunSession((MetricRunSession) entry);
			if (deactivatedSession != null)
				deactivatedSession.swapOut();
		}

		public void setHistoryEntries(List remainingEntries, Object activeEntry) {
			setActiveMetricRunSession((MetricRunSession) activeEntry);

			List metricRunSessions = PHPDependPlugin.getModel().getMetricRunSessions();
			metricRunSessions.removeAll(remainingEntries);
			for (Iterator iter = metricRunSessions.iterator(); iter.hasNext();) {
				PHPDependPlugin.getModel().removeMetricRunSession((MetricRunSession) iter.next());
			}
			for (Iterator iter = remainingEntries.iterator(); iter.hasNext();) {
				MetricRunSession remaining = (MetricRunSession) iter.next();
				remaining.swapOut();
			}
		}

		public ImageDescriptor getImageDescriptor(Object element) {
			MetricRunSession session = (MetricRunSession) element;
			return ImageDescriptor.createFromImage(session.getImage());
		}

		public String getText(Object element) {
			MetricRunSession session = (MetricRunSession) element;
			String testRunLabel = BasicElementLabels.getPHPElementName(session.getName());

			String startTime = DateFormat.getDateTimeInstance().format(session.getGenerated());
			return Messages.format(PHPDependMessages.TestRunnerViewPart_testName_startTime, new Object[] {
					testRunLabel, startTime });

		}

		public void addMenuEntries(MenuManager manager) {
			manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new ImportMetricRunSessionAction(fParent
					.getShell()));
			if (fMetricRunSession != null)
				manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new ExportMetricRunSessionAction(fParent
						.getShell(), fMetricRunSession));
		}

		public String getMaxEntriesMessage() {
			return PHPDependMessages.TestRunnerViewPart_max_remembered;
		}

		public int getMaxEntries() {
			IPreferenceStore store = PHPDependPlugin.getDefault().getPreferenceStore();
			return store.getInt(PHPDependPreferencesConstants.MAX_TEST_RUNS);
		}

		public void setMaxEntries(int maxEntries) {
			IPreferenceStore store = PHPDependPlugin.getDefault().getPreferenceStore();
			store.setValue(PHPDependPreferencesConstants.MAX_TEST_RUNS, maxEntries);
		}
	}

	private static class ImportMetricRunSessionAction extends Action {
		private final Shell fShell;

		public ImportMetricRunSessionAction(Shell shell) {
			super(PHPDependMessages.TestRunnerViewPart_ImportTestRunSessionAction_name);
			fShell = shell;
		}

		public void run() {
			FileDialog importDialog = new FileDialog(fShell, SWT.OPEN);
			importDialog.setText(PHPDependMessages.TestRunnerViewPart_ImportTestRunSessionAction_title);
			IDialogSettings dialogSettings = PHPDependPlugin.getDefault().getDialogSettings();
			String lastPath = dialogSettings.get(PREF_LAST_PATH);
			if (lastPath != null) {
				importDialog.setFilterPath(lastPath);
			}
			importDialog.setFilterExtensions(new String[] { "*.xml", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
			String path = importDialog.open();
			if (path == null)
				return;

			// TODO: MULTI: getFileNames()
			File file = new File(path);

			try {
				PHPDependModel.importMetricRunSession(file);
			} catch (CoreException e) {
				Logger.logException(e);
				ErrorDialog.openError(fShell,
						PHPDependMessages.TestRunnerViewPart_ImportTestRunSessionAction_error_title, e.getStatus()
								.getMessage(), e.getStatus());
			}
		}
	}

	private static class ExportMetricRunSessionAction extends Action {
		private final MetricRunSession fMetricRunSession;
		private final Shell fShell;

		public ExportMetricRunSessionAction(Shell shell, MetricRunSession metricRunSession) {
			super(PHPDependMessages.TestRunnerViewPart_ExportTestRunSessionAction_name);
			fShell = shell;
			fMetricRunSession = metricRunSession;
		}

		public void run() {
			FileDialog exportDialog = new FileDialog(fShell, SWT.SAVE);
			exportDialog.setText(PHPDependMessages.TestRunnerViewPart_ExportTestRunSessionAction_title);
			IDialogSettings dialogSettings = PHPDependPlugin.getDefault().getDialogSettings();
			String lastPath = dialogSettings.get(PREF_LAST_PATH);
			if (lastPath != null) {
				exportDialog.setFilterPath(lastPath);
			}
			exportDialog.setFileName(getFileName());
			exportDialog.setFilterExtensions(new String[] { "*.xml", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
			String path = exportDialog.open();
			if (path == null)
				return;

			// TODO: MULTI: getFileNames()
			File file = new File(path);

			try {
				PHPDependModel.exportMetricRunSession(fMetricRunSession, file);
			} catch (CoreException e) {
				Logger.logException(e);
				ErrorDialog.openError(fShell,
						PHPDependMessages.TestRunnerViewPart_ExportTestRunSessionAction_error_title, e.getStatus()
								.getMessage(), e.getStatus());
			}
		}

		private String getFileName() {
			String testRunName = fMetricRunSession.getName();
			String isoTime = new SimpleDateFormat("yyyyMMdd-HHmmss").format(fMetricRunSession.getGenerated()); //$NON-NLS-1$
			return testRunName + " " + isoTime + ".xml"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private class MetricRunSessionListener implements IMetricRunSessionListener {
		public void sessionAdded(MetricRunSession metricRunSession) {
			MetricRunSession deactivatedSession = setActiveMetricRunSession(metricRunSession);
			if (deactivatedSession != null)
				deactivatedSession.swapOut();
		}

		public void sessionRemoved(MetricRunSession metricRunSession) {
			if (metricRunSession.equals(fMetricRunSession)) {
				List<MetricRunSession> metricRunSessions = PHPDependPlugin.getModel().getMetricRunSessions();
				MetricRunSession deactivatedSession;
				if (!metricRunSessions.isEmpty()) {
					deactivatedSession = setActiveMetricRunSession(metricRunSessions.get(0));
				} else {
					deactivatedSession = setActiveMetricRunSession(null);
				}
				if (deactivatedSession != null)
					deactivatedSession.swapOut();
			}
		}
	}

	private class UpdateUIJob extends UIJob {
		private boolean fRunning = true;

		public UpdateUIJob(String name) {
			super(name);
			setSystem(true);
		}

		public IStatus runInUIThread(IProgressMonitor monitor) {
			if (!isDisposed()) {
				processChangesInUI();
			}
			schedule(REFRESH_INTERVAL);
			return Status.OK_STATUS;
		}

		public void stop() {
			fRunning = false;
		}

		public boolean shouldSchedule() {
			return fRunning;
		}
	}

	private class ClearAction extends Action {
		public ClearAction() {
			setText(PHPDependMessages.TestRunnerViewPart_clear_history_label);

			List metricRunSessions = PHPDependPlugin.getModel().getMetricRunSessions();
			setEnabled(metricRunSessions.size() > 0);
		}

		public void run() {
			List metricRunSessions = PHPDependPlugin.getModel().getMetricRunSessions();
			Object first = metricRunSessions.isEmpty() ? null : metricRunSessions.get(0);
			fViewHistory.setHistoryEntries(metricRunSessions, first);
		}
	}

	private class ToggleOrientationAction extends Action {
		private final int fActionOrientation;

		public ToggleOrientationAction(int orientation) {
			super("", AS_RADIO_BUTTON); //$NON-NLS-1$
			if (orientation == MetricRunnerViewPart.VIEW_ORIENTATION_HORIZONTAL) {
				setText(PHPDependMessages.TestRunnerViewPart_toggle_horizontal_label);
				setImageDescriptor(PHPDependPlugin.getImageDescriptor("elcl16/th_horizontal.gif")); //$NON-NLS-1$
			} else if (orientation == MetricRunnerViewPart.VIEW_ORIENTATION_VERTICAL) {
				setText(PHPDependMessages.TestRunnerViewPart_toggle_vertical_label);
				setImageDescriptor(PHPDependPlugin.getImageDescriptor("elcl16/th_vertical.gif")); //$NON-NLS-1$
			} else if (orientation == MetricRunnerViewPart.VIEW_ORIENTATION_AUTOMATIC) {
				setText(PHPDependMessages.TestRunnerViewPart_toggle_automatic_label);
				setImageDescriptor(PHPDependPlugin.getImageDescriptor("elcl16/th_automatic.gif")); //$NON-NLS-1$
			}
			fActionOrientation = orientation;
			PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
					IPHPDependHelpContextIds.RESULTS_VIEW_TOGGLE_ORIENTATION_ACTION);
		}

		public int getOrientation() {
			return fActionOrientation;
		}

		public void run() {
			if (isChecked()) {
				fOrientation = fActionOrientation;
				computeOrientation();
			}
		}
	}

	/**
	 * Listen for for modifications to Java elements
	 */
	private class DirtyListener implements IElementChangedListener {
		public void elementChanged(ElementChangedEvent event) {

		}
	}

	private class FailuresOnlyFilterAction extends Action {
		public FailuresOnlyFilterAction() {
			super(PHPDependMessages.TestRunnerViewPart_show_failures_only, AS_CHECK_BOX);
			setToolTipText(PHPDependMessages.TestRunnerViewPart_show_failures_only);
			setImageDescriptor(PHPDependPlugin.getImageDescriptor("obj16/failures.gif")); //$NON-NLS-1$
		}

		public void run() {
			setShowFailuresOnly(isChecked());
		}
	}

	private class ShowTestHierarchyAction extends Action {

		public ShowTestHierarchyAction() {
			super(PHPDependMessages.TestRunnerViewPart_hierarchical_layout, IAction.AS_CHECK_BOX);
			setImageDescriptor(PHPDependPlugin.getImageDescriptor("elcl16/hierarchicalLayout.gif")); //$NON-NLS-1$
		}

		public void run() {
			int mode = isChecked() ? LAYOUT_HIERARCHICAL : LAYOUT_FLAT;
			setLayoutMode(mode);
		}
	}

	private class ActivateOnErrorAction extends Action {
		public ActivateOnErrorAction() {
			super(PHPDependMessages.TestRunnerViewPart_activate_on_failure_only, IAction.AS_CHECK_BOX);
			//setImageDescriptor(PHPDependPlugin.getImageDescriptor("obj16/failures.gif")); //$NON-NLS-1$
			update();
		}

		public void update() {
			setChecked(getShowOnErrorOnly());
		}

		public void run() {
			boolean checked = isChecked();
			fShowOnErrorOnly = checked;
			IPreferenceStore store = PHPDependPlugin.getDefault().getPreferenceStore();
			store.setValue(PHPDependPreferencesConstants.SHOW_ON_ERROR_ONLY, checked);
		}
	}

	public MetricRunnerViewPart() {
		fImagesToDispose = new ArrayList();

		fStackViewIcon = createManagedPHPDependImage("eview16/stackframe.gif");//$NON-NLS-1$
	}

	private Image createManagedImage(String path) {
		return createManagedImage(PHPDependPlugin.getImageDescriptor(path));
	}

	private Image createManagedPHPDependImage(String path) {
		return createManagedImage(PHPDependPlugin.getImageDescriptor(path));
	}

	private Image createManagedImage(ImageDescriptor descriptor) {
		Image image = descriptor.createImage();
		if (image == null) {
			image = ImageDescriptor.getMissingImageDescriptor().createImage();
		}
		fImagesToDispose.add(image);
		return image;
	}

	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		fMemento = memento;
		IWorkbenchSiteProgressService progressService = getProgressService();
		if (progressService != null)
			progressService.showBusyForFamily(MetricRunnerViewPart.FAMILY_JUNIT_RUN);
	}

	private IWorkbenchSiteProgressService getProgressService() {
		Object siteService = getSite().getAdapter(IWorkbenchSiteProgressService.class);
		if (siteService != null)
			return (IWorkbenchSiteProgressService) siteService;
		return null;
	}

	public void saveState(IMemento memento) {
		if (fSashForm == null) {
			// part has not been created
			if (fMemento != null) // Keep the old state;
				memento.putMemento(fMemento);
			return;
		}

		// int activePage= fTabFolder.getSelectionIndex();
		// memento.putInteger(TAG_PAGE, activePage);
		int weigths[] = fSashForm.getWeights();
		int ratio = (weigths[0] * 1000) / (weigths[0] + weigths[1]);
		memento.putInteger(TAG_RATIO, ratio);
		memento.putInteger(TAG_ORIENTATION, fOrientation);

		memento.putString(TAG_FAILURES_ONLY, fFailuresOnlyFilterAction.isChecked() ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
		memento.putInteger(TAG_LAYOUT, fLayout);
	}

	private void restoreLayoutState(IMemento memento) {
		Integer ratio = memento.getInteger(TAG_RATIO);
		if (ratio != null)
			fSashForm.setWeights(new int[] { ratio.intValue(), 1000 - ratio.intValue() });
		Integer orientation = memento.getInteger(TAG_ORIENTATION);
		if (orientation != null)
			fOrientation = orientation.intValue();
		computeOrientation();

		Integer layout = memento.getInteger(TAG_LAYOUT);
		int layoutValue = LAYOUT_HIERARCHICAL;
		if (layout != null)
			layoutValue = layout.intValue();

		String failuresOnly = memento.getString(TAG_FAILURES_ONLY);
		boolean showFailuresOnly = false;
		if (failuresOnly != null)
			showFailuresOnly = failuresOnly.equals("true"); //$NON-NLS-1$

		setFilterAndLayout(showFailuresOnly, layoutValue);
	}

	private void startUpdateJobs() {
		postSyncProcessChanges();

		if (fUpdateJob != null) {
			return;
		}

		fUpdateJob = new UpdateUIJob(PHPDependMessages.TestRunnerViewPart_jobName);
		fUpdateJob.schedule(REFRESH_INTERVAL);
	}

	private void stopUpdateJobs() {
		if (fUpdateJob != null) {
			fUpdateJob.stop();
			fUpdateJob = null;
		}
		postSyncProcessChanges();
	}

	private boolean hasErrorsPlusWarnings() {
		if (fMetricRunSession == null)
			return false;
		else
			return fMetricRunSession.hasErrors() || fMetricRunSession.hasWarnings();
	}

	private void processChangesInUI() {
		if (fSashForm.isDisposed())
			return;

		doShowInfoMessage();

		boolean hasErrorsOrFailures = hasErrorsPlusWarnings();
		fNextAction.setEnabled(hasErrorsOrFailures);
		fPreviousAction.setEnabled(hasErrorsOrFailures);

		fTestViewer.processChangesInUI();
	}

	public void selectNextFailure() {
		fTestViewer.selectFailure(true);
	}

	public void selectPreviousFailure() {
		fTestViewer.selectFailure(false);
	}

	protected void selectFirstFailure() {
		fTestViewer.selectFirstFailure();
	}

	/**
	 * @param metricRunSession
	 *            new active test run session
	 * @return deactivated session, or <code>null</code> iff no session got
	 *         deactivated
	 */
	private MetricRunSession setActiveMetricRunSession(MetricRunSession metricRunSession) {
		/*
		 * - State: fMetricRunSession fTestSessionListener Jobs
		 * fTestViewer.processChangesInUI(); - UI: fCounterPanel fProgressBar
		 * setContentDescription / fInfoMessage setTitleToolTip view icons
		 * statusLine fFailureTrace
		 * 
		 * action enablement
		 */
		if (fMetricRunSession == metricRunSession)
			return null;

		MetricRunSession deactivatedSession = fMetricRunSession;

		fMetricRunSession = metricRunSession;
		fTestViewer.registerActiveSession(metricRunSession);

		if (fSashForm.isDisposed()) {
			stopUpdateJobs();
			return deactivatedSession;
		}

		if (metricRunSession == null) {
			setTitleToolTip(null);
			clearStatus();
			fMetricList.clear();

			registerInfoMessage(" "); //$NON-NLS-1$
			stopUpdateJobs();

		} else {
			setTitleToolTip();

			clearStatus();
			fMetricList.clear();
			registerInfoMessage(BasicElementLabels.getPHPElementName(fMetricRunSession.getName()));

			if (fMetricRunSession.isRunning()) {
				startUpdateJobs();
			} else /* old or fresh session: don't want jobs at this stage */{
				stopUpdateJobs();
				fTestViewer.expandFirstLevel();
			}
		}
		return deactivatedSession;
	}

	private void setTitleToolTip() {

		String testRunLabel = BasicElementLabels.getPHPElementName(fMetricRunSession.getName());
		setTitleToolTip(testRunLabel);
	}

	public synchronized void dispose() {
		fIsDisposed = true;
		if (fMetricRunSessionListener != null)
			PHPDependPlugin.getModel().removeMetricRunSessionListener(fMetricRunSessionListener);

		setActiveMetricRunSession(null);

		getViewSite().getPage().removePartListener(fPartListener);

		disposeImages();
		if (fClipboard != null)
			fClipboard.dispose();
		if (fViewMenuListener != null) {
			getViewSite().getActionBars().getMenuManager().removeMenuListener(fViewMenuListener);
		}
		if (fDirtyListener != null) {
			// JavaCore.removeElementChangedListener(fDirtyListener);
			fDirtyListener = null;
		}
	}

	private void disposeImages() {
		for (int i = 0; i < fImagesToDispose.size(); i++) {
			((Image) fImagesToDispose.get(i)).dispose();
		}
	}

	private void postSyncRunnable(Runnable r) {
		if (!isDisposed())
			getDisplay().syncExec(r);
	}

	protected void postShowTestResultsView() {
		postSyncRunnable(new Runnable() {
			public void run() {
				if (isDisposed())
					return;
				showTestResultsView();
			}
		});
	}

	public static void showTestResultsView() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			MetricRunnerViewPart testRunner = null;

			if (page != null) {
				try { // show the result view
					testRunner = (MetricRunnerViewPart) page.findView(MetricRunnerViewPart.NAME);
					if (testRunner == null) {
						IWorkbenchPart activePart = page.getActivePart();
						testRunner = (MetricRunnerViewPart) page.showView(MetricRunnerViewPart.NAME);
						// restore focus
						page.activate(activePart);
					} else {
						page.bringToTop(testRunner);
					}
				} catch (PartInitException pie) {
					Logger.logException(pie);
				}
			}
		}
	}

	protected void doShowInfoMessage() {
		if (fInfoMessage != null) {
			setContentDescription(fInfoMessage);
			fInfoMessage = null;
		}
	}

	protected void registerInfoMessage(String message) {
		fInfoMessage = message;
	}

	private SashForm createSashForm(Composite parent) {
		fSashForm = new SashForm(parent, SWT.VERTICAL);

		ViewForm top = new ViewForm(fSashForm, SWT.NONE);

		Composite empty = new Composite(top, SWT.NONE);
		empty.setLayout(new Layout() {
			protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
				return new Point(1, 1); // (0, 0) does not work with
				// super-intelligent ViewForm
			}

			protected void layout(Composite composite, boolean flushCache) {
			}
		});
		top.setTopLeft(empty); // makes ViewForm draw the horizontal separator
		// line ...
		fTestViewer = new MetricViewer(top, fClipboard, this);
		top.setContent(fTestViewer.getTestViewerControl());

		ViewForm bottom = new ViewForm(fSashForm, SWT.NONE);

		CLabel label = new CLabel(bottom, SWT.NONE);
		label.setText(PHPDependMessages.TestRunnerViewPart_label_failure);
		label.setImage(fStackViewIcon);
		bottom.setTopLeft(label);
		ToolBar failureToolBar = new ToolBar(bottom, SWT.FLAT | SWT.WRAP);
		bottom.setTopCenter(failureToolBar);
		fMetricList = new MetricTrace(bottom, fClipboard, this, failureToolBar);
		bottom.setContent(fMetricList.getComposite());

		fSashForm.setWeights(new int[] { 50, 50 });
		return fSashForm;
	}

	private void clearStatus() {
		getStatusLine().setMessage(null);
		getStatusLine().setErrorMessage(null);
	}

	public void setFocus() {
		if (fTestViewer != null)
			fTestViewer.getTestViewerControl().setFocus();
	}

	public void createPartControl(Composite parent) {
		fParent = parent;
		addResizeListener(parent);
		fClipboard = new Clipboard(parent.getDisplay());

		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		parent.setLayout(gridLayout);

		fViewHistory = new RunnerViewHistory();
		configureToolBar();

		SashForm sashForm = createSashForm(parent);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		initPageSwitcher();

		fOriginalViewImage = getTitleImage();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IPHPDependHelpContextIds.RESULTS_VIEW);

		getViewSite().getPage().addPartListener(fPartListener);

		setFilterAndLayout(false, LAYOUT_HIERARCHICAL);
		if (fMemento != null) {
			restoreLayoutState(fMemento);
		}
		fMemento = null;

		fMetricRunSessionListener = new MetricRunSessionListener();
		PHPDependPlugin.getModel().addMetricRunSessionListener(fMetricRunSessionListener);
		List<MetricRunSession> sessions = PHPDependPlugin.getModel().getMetricRunSessions();
		if (sessions.size() > 0) {
			Object lastSession = sessions.get(0);
			if (lastSession instanceof MetricRunSession)
				fMetricRunSessionListener.sessionAdded((MetricRunSession) lastSession);
		}
	}

	private void initPageSwitcher() {
		new PageSwitcher(this) {
			public Object[] getPages() {
				return fViewHistory.getHistoryEntries().toArray();
			}

			public String getName(Object page) {
				return fViewHistory.getText(page);
			}

			public ImageDescriptor getImageDescriptor(Object page) {
				return fViewHistory.getImageDescriptor(page);
			}

			public void activatePage(Object page) {
				fViewHistory.setActiveEntry(page);
			}

			public int getCurrentPageIndex() {
				return fViewHistory.getHistoryEntries().indexOf(fViewHistory.getCurrentEntry());
			}
		};
	}

	private void addResizeListener(Composite parent) {
		parent.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
			}

			public void controlResized(ControlEvent e) {
				computeOrientation();
			}
		});
	}

	void computeOrientation() {
		if (fOrientation != VIEW_ORIENTATION_AUTOMATIC) {
			fCurrentOrientation = fOrientation;
			setOrientation(fCurrentOrientation);
		} else {
			Point size = fParent.getSize();
			if (size.x != 0 && size.y != 0) {
				if (size.x > size.y)
					setOrientation(VIEW_ORIENTATION_HORIZONTAL);
				else
					setOrientation(VIEW_ORIENTATION_VERTICAL);
			}
		}
	}

	private void configureToolBar() {
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolBar = actionBars.getToolBarManager();
		IMenuManager viewMenu = actionBars.getMenuManager();

		fNextAction = new ShowNextFailureAction(this);
		fNextAction.setEnabled(false);
		actionBars.setGlobalActionHandler(ActionFactory.NEXT.getId(), fNextAction);

		fPreviousAction = new ShowPreviousFailureAction(this);
		fPreviousAction.setEnabled(false);
		actionBars.setGlobalActionHandler(ActionFactory.PREVIOUS.getId(), fPreviousAction);

		fFailuresOnlyFilterAction = new FailuresOnlyFilterAction();

		fToggleOrientationActions = new ToggleOrientationAction[] {
				new ToggleOrientationAction(VIEW_ORIENTATION_VERTICAL),
				new ToggleOrientationAction(VIEW_ORIENTATION_HORIZONTAL),
				new ToggleOrientationAction(VIEW_ORIENTATION_AUTOMATIC) };

		fShowTestHierarchyAction = new ShowTestHierarchyAction();

		toolBar.add(fNextAction);
		toolBar.add(fPreviousAction);
		toolBar.add(fFailuresOnlyFilterAction);
		toolBar.add(new Separator());
		toolBar.add(fViewHistory.createHistoryDropDownAction());

		viewMenu.add(fShowTestHierarchyAction);
		viewMenu.add(new Separator());

		MenuManager layoutSubMenu = new MenuManager(PHPDependMessages.TestRunnerViewPart_layout_menu);
		for (int i = 0; i < fToggleOrientationActions.length; ++i) {
			layoutSubMenu.add(fToggleOrientationActions[i]);
		}
		viewMenu.add(layoutSubMenu);
		viewMenu.add(new Separator());

		viewMenu.add(fFailuresOnlyFilterAction);

		fActivateOnErrorAction = new ActivateOnErrorAction();
		viewMenu.add(fActivateOnErrorAction);
		fViewMenuListener = new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fActivateOnErrorAction.update();
			}
		};

		viewMenu.addMenuListener(fViewMenuListener);

		actionBars.updateActionBars();
	}

	private IStatusLineManager getStatusLine() {
		// we want to show messages globally hence we
		// have to go through the active part
		IViewSite site = getViewSite();
		IWorkbenchPage page = site.getPage();
		IWorkbenchPart activePart = page.getActivePart();

		if (activePart instanceof IViewPart) {
			IViewPart activeViewPart = (IViewPart) activePart;
			IViewSite activeViewSite = activeViewPart.getViewSite();
			return activeViewSite.getActionBars().getStatusLineManager();
		}

		if (activePart instanceof IEditorPart) {
			IEditorPart activeEditorPart = (IEditorPart) activePart;
			IEditorActionBarContributor contributor = activeEditorPart.getEditorSite().getActionBarContributor();
			if (contributor instanceof EditorActionBarContributor)
				return ((EditorActionBarContributor) contributor).getActionBars().getStatusLineManager();
		}
		// no active part
		return getViewSite().getActionBars().getStatusLineManager();
	}

	public void handleElementSelected(IMetricElement element) {
		showElement(element);
		// fCopyAction.handleTestSelected(element);
	}

	private void showElement(final IMetricElement element) {
		postSyncRunnable(new Runnable() {
			public void run() {
				if (!isDisposed())
					fMetricList.showElement(element);
			}
		});
	}

	/**
	 * @return the Java project, or <code>null</code>
	 */
	public IProject getLaunchedProject() {
		// return fMetricRunSession == null ? null :
		// fMetricRunSession.getLaunchedProject();
		return null;
	}

	private boolean isDisposed() {
		return fIsDisposed;
	}

	private Display getDisplay() {
		return getViewSite().getShell().getDisplay();
	}

	/*
	 * @see IWorkbenchPart#getTitleImage()
	 */
	public Image getTitleImage() {
		if (fOriginalViewImage == null)
			fOriginalViewImage = super.getTitleImage();

		if (fViewImage == null)
			return super.getTitleImage();
		return fViewImage;
	}

	void codeHasChanged() {
		if (fDirtyListener != null) {
			// JavaCore.removeElementChangedListener(fDirtyListener);
			fDirtyListener = null;
		}

		Runnable r = new Runnable() {
			public void run() {
				if (isDisposed())
					return;
				firePropertyChange(IWorkbenchPart.PROP_TITLE);
			}
		};
		if (!isDisposed())
			getDisplay().asyncExec(r);
	}

	public boolean isCreated() {
		return fTestViewer != null;
	}

	public void rerunTest(String testId, String className, String testName, String launchMode) {
	}

	private void postSyncProcessChanges() {
		postSyncRunnable(new Runnable() {
			public void run() {
				processChangesInUI();
			}
		});
	}

	public void warnOfContentChange() {
		IWorkbenchSiteProgressService service = getProgressService();
		if (service != null)
			service.warnOfContentChange();
	}

	private void setOrientation(int orientation) {
		if ((fSashForm == null) || fSashForm.isDisposed())
			return;
		boolean horizontal = orientation == VIEW_ORIENTATION_HORIZONTAL;
		fSashForm.setOrientation(horizontal ? SWT.HORIZONTAL : SWT.VERTICAL);
		for (int i = 0; i < fToggleOrientationActions.length; ++i)
			fToggleOrientationActions[i].setChecked(fOrientation == fToggleOrientationActions[i].getOrientation());
		fCurrentOrientation = orientation;
		fParent.layout();
	}

	private static boolean getShowOnErrorOnly() {
		IPreferenceStore store = PHPDependPlugin.getDefault().getPreferenceStore();
		return store.getBoolean(PHPDependPreferencesConstants.SHOW_ON_ERROR_ONLY);
	}

	public MetricTrace getFailureTrace() {
		return fMetricList;
	}

	void setShowFailuresOnly(boolean failuresOnly) {
		setFilterAndLayout(failuresOnly, fLayout);
	}

	private void setLayoutMode(int mode) {
		setFilterAndLayout(fFailuresOnlyFilterAction.isChecked(), mode);
	}

	private void setFilterAndLayout(boolean failuresOnly, int layoutMode) {
		fShowTestHierarchyAction.setChecked(layoutMode == LAYOUT_HIERARCHICAL);
		fLayout = layoutMode;
		fFailuresOnlyFilterAction.setChecked(failuresOnly);
		fTestViewer.setShowFailuresOnly(failuresOnly, layoutMode);
	}
}
