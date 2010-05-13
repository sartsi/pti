/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend;

import java.io.File;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.phpsrc.eclipse.pti.core.AbstractPHPToolPlugin;
import org.phpsrc.eclipse.pti.library.pear.PHPLibraryPEARPlugin;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.listener.PHPDependProblemMarkerListener;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.PHPDependModel;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.PHPDependPreferences;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.PHPDependPreferencesFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class PHPDependPlugin extends AbstractPHPToolPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.phpsrc.eclipse.pti.tools.phpdepend";

	public static final String IMG_PHP_DEPEND = "IMG_PHP_DEPEND"; //$NON-NLS-1$
	public static final String IMG_METRIC_TYPE_FILE = "IMG_METRIC_TYPE_FILE"; //$NON-NLS-1$
	public static final String IMG_METRIC_TYPE_FILE_HIERACHY = "IMG_METRIC_TYPE_FILE_HIERACHY"; //$NON-NLS-1$
	public static final String IMG_METRIC_TYPE_PACKAGE = "IMG_METRIC_TYPE_FOLDER"; //$NON-NLS-1$

	public static final String ID_EXTENSION_POINT_METRICRUN_LISTENERS = PLUGIN_ID + "." + "metricRunListeners"; //$NON-NLS-1$ //$NON-NLS-2$

	private static final IPath ICONS_PATH = new Path("$nl$/icons/full"); //$NON-NLS-1$
	private static final String HISTORY_DIR_NAME = "history"; //$NON-NLS-1$

	// The shared instance
	private static PHPDependPlugin plugin;

	private final PHPDependModel fPHPDependModel = new PHPDependModel();
	private final PHPDependProblemMarkerListener fProblemListener = new PHPDependProblemMarkerListener();

	private static boolean fIsStopped = false;

	/**
	 * The constructor
	 */
	public PHPDependPlugin() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		fIsStopped = false;
		fPHPDependModel.start();
		fPHPDependModel.addMetricRunSessionListener(fProblemListener);
	}

	protected void initializeImageRegistry(ImageRegistry registry) {
		registry.put(IMG_PHP_DEPEND, ImageDescriptor
				.createFromURL(resolvePluginResourceURL("icons/full/obj16/php_depend.gif")));
		registry.put(IMG_METRIC_TYPE_FILE, ImageDescriptor
				.createFromURL(resolvePluginResourceURL("icons/full/obj16/type_file.gif")));
		registry.put(IMG_METRIC_TYPE_FILE_HIERACHY, ImageDescriptor
				.createFromURL(resolvePluginResourceURL("icons/full/obj16/type_file_hierachy.gif")));
		registry.put(IMG_METRIC_TYPE_PACKAGE, ImageDescriptor
				.createFromURL(resolvePluginResourceURL("icons/full/obj16/type_folder.gif")));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		try {
			for (File file : getHistoryDirectory().listFiles()) {
				if (file.isFile())
					file.delete();
			}
		} catch (Exception e) {
		}

		fIsStopped = true;
		try {
			fPHPDependModel.removeMetricRunSessionListener(fProblemListener);
			fPHPDependModel.stop();
		} finally {
			plugin = null;
			super.stop(context);
		}
	}

	public static PHPDependModel getModel() {
		return getDefault().fPHPDependModel;
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static PHPDependPlugin getDefault() {
		return plugin;
	}

	public IPath[] getPluginIncludePaths(IProject project) {
		PHPDependPreferences prefs = PHPDependPreferencesFactory.factory(project);
		IPath[] pearPaths = PHPLibraryPEARPlugin.getDefault().getPluginIncludePaths(prefs.getPearLibraryName());

		IPath[] includePaths = new IPath[pearPaths.length + 1];
		includePaths[0] = resolvePluginResource("/php/tools");
		System.arraycopy(pearPaths, 0, includePaths, 1, pearPaths.length);

		return includePaths;
	}

	public static ImageDescriptor getImageDescriptor(String relativePath) {
		IPath path = ICONS_PATH.append(relativePath);
		return createImageDescriptor(getDefault().getBundle(), path, true);
	}

	public static Image createImage(String path) {
		return getImageDescriptor(path).createImage();
	}

	/**
	 * Sets the three image descriptors for enabled, disabled, and hovered to an
	 * action. The actions are retrieved from the *lcl16 folders.
	 * 
	 * @param action
	 *            the action
	 * @param iconName
	 *            the icon name
	 */
	public static void setLocalImageDescriptors(IAction action, String iconName) {
		setImageDescriptors(action, "lcl16", iconName); //$NON-NLS-1$
	}

	private static void setImageDescriptors(IAction action, String type, String relPath) {
		ImageDescriptor id = createImageDescriptor("d" + type, relPath, false); //$NON-NLS-1$
		if (id != null)
			action.setDisabledImageDescriptor(id);

		ImageDescriptor descriptor = createImageDescriptor("e" + type, relPath, true); //$NON-NLS-1$
		action.setHoverImageDescriptor(descriptor);
		action.setImageDescriptor(descriptor);
	}

	/*
	 * Creates an image descriptor for the given prefix and name in the JDT UI
	 * bundle. The path can contain variables like $NL$. If no image could be
	 * found, <code>useMissingImageDescriptor</code> decides if either the
	 * 'missing image descriptor' is returned or <code>null</code>. or
	 * <code>null</code>.
	 */
	private static ImageDescriptor createImageDescriptor(String pathPrefix, String imageName,
			boolean useMissingImageDescriptor) {
		IPath path = ICONS_PATH.append(pathPrefix).append(imageName);
		return createImageDescriptor(PHPDependPlugin.getDefault().getBundle(), path, useMissingImageDescriptor);
	}

	/**
	 * Creates an image descriptor for the given path in a bundle. The path can
	 * contain variables like $NL$. If no image could be found,
	 * <code>useMissingImageDescriptor</code> decides if either the 'missing
	 * image descriptor' is returned or <code>null</code>.
	 * 
	 * @param bundle
	 *            a bundle
	 * @param path
	 *            path in the bundle
	 * @param useMissingImageDescriptor
	 *            if <code>true</code>, returns the shared image descriptor for
	 *            a missing image. Otherwise, returns <code>null</code> if the
	 *            image could not be found
	 * @return an {@link ImageDescriptor}, or <code>null</code> iff there's no
	 *         image at the given location and
	 *         <code>useMissingImageDescriptor</code> is <code>true</code>
	 */
	private static ImageDescriptor createImageDescriptor(Bundle bundle, IPath path, boolean useMissingImageDescriptor) {
		URL url = FileLocator.find(bundle, path, null);
		if (url != null) {
			return ImageDescriptor.createFromURL(url);
		}
		if (useMissingImageDescriptor) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
		return null;
	}

	public static boolean isStopped() {
		return fIsStopped;
	}

	public IDialogSettings getDialogSettingsSection(String name) {
		IDialogSettings dialogSettings = getDialogSettings();
		IDialogSettings section = dialogSettings.getSection(name);
		if (section == null) {
			section = dialogSettings.addNewSection(name);
		}
		return section;
	}

	public static File getHistoryDirectory() throws IllegalStateException {
		File historyDir = getDefault().getStateLocation().append(HISTORY_DIR_NAME).toFile();
		if (!historyDir.isDirectory()) {
			historyDir.mkdir();
		}

		return historyDir;
	}

	/**
	 * Returns the active workbench window
	 * 
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		if (plugin == null)
			return null;
		IWorkbench workBench = plugin.getWorkbench();
		if (workBench == null)
			return null;
		return workBench.getActiveWorkbenchWindow();
	}

	public static IWorkbenchPage getActivePage() {
		IWorkbenchWindow activeWorkbenchWindow = getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null)
			return null;
		return activeWorkbenchWindow.getActivePage();
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, "Error", e)); //$NON-NLS-1$
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}
}
