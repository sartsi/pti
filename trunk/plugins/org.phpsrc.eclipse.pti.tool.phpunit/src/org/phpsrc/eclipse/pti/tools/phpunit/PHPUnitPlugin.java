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

package org.phpsrc.eclipse.pti.tools.phpunit;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.phpsrc.eclipse.pti.core.AbstractPHPToolPlugin;
import org.phpsrc.eclipse.pti.library.pear.PHPLibraryPEARPlugin;
import org.phpsrc.eclipse.pti.tools.phpunit.core.model.PHPUnitModel;
import org.phpsrc.eclipse.pti.tools.phpunit.core.preferences.PHPUnitPreferences;
import org.phpsrc.eclipse.pti.tools.phpunit.core.preferences.PHPUnitPreferencesFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class PHPUnitPlugin extends AbstractPHPToolPlugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "org.phpsrc.eclipse.pti.tools.phpunit";

	public static final String IMG_PHPUNIT = "IMG_PHPUNIT"; //$NON-NLS-1$
	public static final String IMG_PHPUNIT_TEST = "IMG_PHPUNIT_TEST"; //$NON-NLS-1$
	public static final String IMG_PHPUNIT_TEST_ERROR = "IMG_PHPUNIT_TEST_ERROR"; //$NON-NLS-1$
	public static final String IMG_PHPUNIT_TEST_FAIL = "IMG_PHPUNIT_TEST_FAIL"; //$NON-NLS-1$
	public static final String IMG_PHPUNIT_TEST_IGNORED = "IMG_PHPUNIT_TEST_IGNORED"; //$NON-NLS-1$
	public static final String IMG_PHPUNIT_TEST_OK = "IMG_PHPUNIT_TEST_OK"; //$NON-NLS-1$
	public static final String IMG_PHPUNIT_TEST_SUITE = "IMG_PHPUNIT_TEST_SUITE"; //$NON-NLS-1$
	public static final String IMG_PHPUNIT_TEST_SUITE_ERROR = "IMG_PHPUNIT_TEST_SUITE_ERROR"; //$NON-NLS-1$
	public static final String IMG_PHPUNIT_TEST_SUITE_FAIL = "IMG_PHPUNIT_TEST_SUITE_FAIL"; //$NON-NLS-1$
	public static final String IMG_PHPUNIT_TEST_SUITE_OK = "IMG_PHPUNIT_TEST_SUITE_OK"; //$NON-NLS-1$

	private static final IPath ICONS_PATH = new Path("$nl$/icons/full"); //$NON-NLS-1$

	// The shared instance
	private static PHPUnitPlugin plugin;

	private final PHPUnitModel fJUnitModel = new PHPUnitModel();

	/**
	 * The constructor
	 */
	public PHPUnitPlugin() {
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
		fJUnitModel.start();
	}

	protected void initializeImageRegistry(ImageRegistry registry) {
		registry.put(IMG_PHPUNIT, ImageDescriptor
				.createFromURL(resolvePluginResourceURL("icons/full/obj16/phpunit.gif")));
		registry.put(IMG_PHPUNIT_TEST, ImageDescriptor
				.createFromURL(resolvePluginResourceURL("icons/full/obj16/test.gif")));
		registry.put(IMG_PHPUNIT_TEST_ERROR, ImageDescriptor
				.createFromURL(resolvePluginResourceURL("icons/full/obj16/testerr.gif")));
		registry.put(IMG_PHPUNIT_TEST_FAIL, ImageDescriptor
				.createFromURL(resolvePluginResourceURL("icons/full/obj16/testfail.gif")));
		registry.put(IMG_PHPUNIT_TEST_IGNORED, ImageDescriptor
				.createFromURL(resolvePluginResourceURL("icons/full/obj16/testignored.gif")));
		registry.put(IMG_PHPUNIT_TEST_OK, ImageDescriptor
				.createFromURL(resolvePluginResourceURL("icons/full/obj16/testok.gif")));
		registry.put(IMG_PHPUNIT_TEST_SUITE, ImageDescriptor
				.createFromURL(resolvePluginResourceURL("icons/full/obj16/tsuite.gif")));
		registry.put(IMG_PHPUNIT_TEST_SUITE_ERROR, ImageDescriptor
				.createFromURL(resolvePluginResourceURL("icons/full/obj16/tsuiteerror.gif")));
		registry.put(IMG_PHPUNIT_TEST_SUITE_FAIL, ImageDescriptor
				.createFromURL(resolvePluginResourceURL("icons/full/obj16/tsuitefail.gif")));
		registry.put(IMG_PHPUNIT_TEST_SUITE_OK, ImageDescriptor
				.createFromURL(resolvePluginResourceURL("icons/full/obj16/tsuiteok.gif")));
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
		try {
			fJUnitModel.stop();
		} finally {
			super.stop(context);
		}
	}

	public static PHPUnitModel getModel() {
		return getDefault().fJUnitModel;
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static PHPUnitPlugin getDefault() {
		return plugin;
	}

	public IPath[] getPluginIncludePaths(IProject project) {

		PHPUnitPreferences prefs = PHPUnitPreferencesFactory.factory(project);

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
}
