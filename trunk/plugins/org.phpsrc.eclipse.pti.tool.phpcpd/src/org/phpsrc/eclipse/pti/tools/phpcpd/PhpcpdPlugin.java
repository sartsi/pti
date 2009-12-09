package org.phpsrc.eclipse.pti.tools.phpcpd;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.osgi.framework.BundleContext;
import org.phpsrc.eclipse.pti.core.AbstractPHPToolPlugin;
import org.phpsrc.eclipse.pti.library.pear.PHPLibraryPEARPlugin;
import org.phpsrc.eclipse.pti.tools.phpcpd.core.preferences.PhpcpdPreferences;
import org.phpsrc.eclipse.pti.tools.phpcpd.core.preferences.PhpcpdPreferencesFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class PhpcpdPlugin extends AbstractPHPToolPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.phpsrc.eclipse.pti.tools.phpcpd";

	// The shared instance
	private static PhpcpdPlugin plugin;

	/**
	 * The constructor
	 */
	public PhpcpdPlugin() {
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
	public static PhpcpdPlugin getDefault() {
		return plugin;
	}

	@Override
	public IPath[] getPluginIncludePaths(IProject project) {
		PhpcpdPreferences prefs = PhpcpdPreferencesFactory.factory(project);
		IPath[] pearPaths = PHPLibraryPEARPlugin.getDefault().getPluginIncludePaths(prefs.getPearLibraryName());

		IPath[] includePaths = new IPath[pearPaths.length + 1];
		includePaths[0] = resolvePluginResource("/php/tools");
		System.arraycopy(pearPaths, 0, includePaths, 1, pearPaths.length);

		return includePaths;
	}
}
