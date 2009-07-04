package org.phpsrc.eclipse.pti.tools.phpunit;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.QualifiedName;
import org.osgi.framework.BundleContext;
import org.phpsrc.eclipse.pti.core.AbstractPHPToolPlugin;
import org.phpsrc.eclipse.pti.library.pear.PHPLibraryPEARPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class PHPUnitPlugin extends AbstractPHPToolPlugin {

	public final static QualifiedName QUALIFIED_NAME = new QualifiedName(PHPUnitPlugin.PLUGIN_ID, "PHPUnitTool");

	// The plug-in ID
	public static final String PLUGIN_ID = "org.phpsrc.eclipse.pti.tools.phpunit";

	// The shared instance
	private static PHPUnitPlugin plugin;

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
	public static PHPUnitPlugin getDefault() {
		return plugin;
	}

	@Override
	public IPath[] getPluginIncludePaths() {
		return new IPath[] { PHPLibraryPEARPlugin.getDefault().resolvePluginResource("/php/library"),
				PHPLibraryPEARPlugin.getDefault().resolvePluginResource("/php/library/PEAR"),
				resolvePluginResource("/php/tools") };
	}
}
