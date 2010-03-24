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

package org.phpsrc.eclipse.pti.tools.phpdepend;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.BundleContext;
import org.phpsrc.eclipse.pti.core.AbstractPHPToolPlugin;
import org.phpsrc.eclipse.pti.core.listener.IResultListener;
import org.phpsrc.eclipse.pti.library.pear.PHPLibraryPEARPlugin;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.PHPDepend;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.metrics.elements.MetricSummary;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.PHPDependPreferences;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.PHPDependPreferencesFactory;
import org.phpsrc.eclipse.pti.tools.phpdepend.ui.views.PHPDependSummaryView;

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

	// The shared instance
	private static PHPDependPlugin plugin;

	private final IResultListener updateViewListener;

	/**
	 * The constructor
	 */
	public PHPDependPlugin() {
		updateViewListener = new IResultListener() {
			public void handleResult(Object result) {
				if (result != null && result instanceof MetricSummary)
					PHPDependSummaryView.showSummary((MetricSummary) result);
			}
		};
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
		PHPDepend.getInstance().addResultListener(updateViewListener);
	}

	protected void initializeImageRegistry(ImageRegistry registry) {
		registry.put(IMG_PHP_DEPEND, ImageDescriptor
				.createFromURL(resolvePluginResourceURL("icons/obj16/php_depend.gif")));
		registry.put(IMG_METRIC_TYPE_FILE, ImageDescriptor
				.createFromURL(resolvePluginResourceURL("icons/obj16/type_file.gif")));
		registry.put(IMG_METRIC_TYPE_FILE_HIERACHY, ImageDescriptor
				.createFromURL(resolvePluginResourceURL("icons/obj16/type_file_hierachy.gif")));
		registry.put(IMG_METRIC_TYPE_PACKAGE, ImageDescriptor
				.createFromURL(resolvePluginResourceURL("icons/obj16/type_folder.gif")));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		PHPDepend.getInstance().removeResultListener(updateViewListener);
		super.stop(context);
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
}
