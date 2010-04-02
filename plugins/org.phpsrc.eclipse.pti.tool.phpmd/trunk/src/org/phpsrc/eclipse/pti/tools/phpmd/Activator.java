package org.phpsrc.eclipse.pti.tools.phpmd;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.osgi.framework.BundleContext;
import org.phpsrc.eclipse.pti.core.AbstractPHPToolPlugin;
import org.phpsrc.eclipse.pti.library.pear.PHPLibraryPEARPlugin;
import org.phpsrc.eclipse.pti.tools.phpmd.core.Phpmd;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractPHPToolPlugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "Plugin_org.phpsrc.eclipse.pti.tools.phpmd";

	// The shared instance
	private static Activator plugin;

	private Phpmd phpmd;

	/**
	 * The constructor
	 */
	public Activator() {
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
	public static Activator getDefault() {
		return plugin;
	}

	public Phpmd getPhpmd() {
		if (null == phpmd) {
			phpmd = new Phpmd();
		}

		return phpmd;
	}

	/**
	 * Provides all include paths for the plugin
	 * 
	 * @return the include paths
	 */
	public IPath[] getPluginIncludePaths(IProject project) {
		IPath[] pearPaths = PHPLibraryPEARPlugin.getDefault()
				.getPluginIncludePaths(project);

		IPath[] includePaths = new IPath[pearPaths.length + 1];
		includePaths[0] = resolvePluginResource("/php/tools");
		System.arraycopy(pearPaths, 0, includePaths, 1, pearPaths.length);

		return includePaths;
	}
}
