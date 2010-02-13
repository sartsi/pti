package org.phpsrc.eclipse.pti.tools.phpdepend;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.BundleContext;
import org.phpsrc.eclipse.pti.core.AbstractPHPToolPlugin;
import org.phpsrc.eclipse.pti.library.pear.PHPLibraryPEARPlugin;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.PHPDependPreferences;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.PHPDependPreferencesFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class PHPDependPlugin extends AbstractPHPToolPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.phpsrc.eclipse.pti.tools.phpdepend";

	public static final String IMG_METRIC_TYPE_FILE = "IMG_METRIC_TYPE_FILE"; //$NON-NLS-1$
	public static final String IMG_METRIC_TYPE_FILE_HIERACHY = "IMG_METRIC_TYPE_FILE_HIERACHY"; //$NON-NLS-1$
	public static final String IMG_METRIC_TYPE_PACKAGE = "IMG_METRIC_TYPE_FOLDER"; //$NON-NLS-1$

	// The shared instance
	private static PHPDependPlugin plugin;

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
	}

	protected void initializeImageRegistry(ImageRegistry registry) {
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

	@Override
	public IPath[] getPluginIncludePaths(IProject project) {
		PHPDependPreferences prefs = PHPDependPreferencesFactory.factory(project);
		IPath[] pearPaths = PHPLibraryPEARPlugin.getDefault().getPluginIncludePaths(prefs.getPearLibraryName());

		IPath[] includePaths = new IPath[pearPaths.length + 1];
		includePaths[0] = resolvePluginResource("/php/tools");
		System.arraycopy(pearPaths, 0, includePaths, 1, pearPaths.length);

		return includePaths;
	}
}
