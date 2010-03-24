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
package org.phpsrc.eclipse.pti.library.pear;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.BundleContext;
import org.phpsrc.eclipse.pti.core.AbstractPHPToolPlugin;
import org.phpsrc.eclipse.pti.library.pear.core.preferences.PEARPreferences;
import org.phpsrc.eclipse.pti.library.pear.core.preferences.PEARPreferencesFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class PHPLibraryPEARPlugin extends AbstractPHPToolPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.phpsrc.eclipse.pti.library.pear";

	// The shared instance
	private static PHPLibraryPEARPlugin plugin;

	/**
	 * The constructor
	 */
	public PHPLibraryPEARPlugin() {
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
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static PHPLibraryPEARPlugin getDefault() {
		return plugin;
	}

	
	public IPath[] getPluginIncludePaths(IProject project) {
		return getPluginIncludePaths(PEARPreferencesFactory.factory(project));
	}

	public IPath[] getPluginIncludePaths(String libraryName) {
		return getPluginIncludePaths(PEARPreferencesFactory.factoryByName(libraryName));
	}

	public IPath[] getPluginIncludePaths(PEARPreferences pref) {
		ArrayList<IPath> paths = new ArrayList<IPath>(2);

		if (pref != null && !" ".equals(pref.getLibraryPath())) {
			IPath path = Path.fromOSString(pref.getLibraryPath());
			if (path != null) {
				paths.add(path);
			}
			path = Path.fromOSString(pref.getLibraryPath() + Path.SEPARATOR + "PEAR");
			if (path != null) {
				paths.add(path);
			}
		}

		paths.add(resolvePluginResource("/php/library"));
		paths.add(resolvePluginResource("/php/library/PEAR"));

		return paths.toArray(new IPath[0]);
	}
}