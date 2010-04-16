package org.phpsrc.eclipse.pti.tools.phpmd.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.phpsrc.eclipse.pti.core.AbstractPHPToolPlugin;
import org.phpsrc.eclipse.pti.core.launching.OperatingSystem;
import org.phpsrc.eclipse.pti.core.launching.PHPToolLauncher;
import org.phpsrc.eclipse.pti.core.php.inifile.INIFileEntry;
import org.phpsrc.eclipse.pti.core.php.inifile.INIFileUtil;
import org.phpsrc.eclipse.pti.core.tools.AbstractPHPTool;
import org.phpsrc.eclipse.pti.library.pear.PHPLibraryPEARPlugin;
import org.phpsrc.eclipse.pti.tools.phpmd.PhpmdPlugin;

@SuppressWarnings("restriction")
public class Phpmd extends AbstractPHPTool {
	public final static QualifiedName QUALIFIED_NAME = new QualifiedName(PhpmdPlugin.PLUGIN_ID, "phpmd");

	public enum RuleSet {
		CODESIZE("codesize.xml"), UNUSEDCODE("unusedcode.xml"), NAMING("naming.xml");

		private String ruleSetFilename;

		private String baseResoucePath = "/php/library/PEAR/data/PHP_PMD/rulesets"; //$NON-NLS-1$

		private AbstractPHPToolPlugin resourceResolver = null;

		RuleSet(String ruleSetFilename) {
			this.ruleSetFilename = ruleSetFilename;
		}

		public AbstractPHPToolPlugin getResourceResolver() {
			if (null == resourceResolver) {
				resourceResolver = PHPLibraryPEARPlugin.getDefault();
			}
			return resourceResolver;
		}

		public void setResourceResolver(AbstractPHPToolPlugin resourceResolver) {
			this.resourceResolver = resourceResolver;
		}

		public void resetResourceResolver() {
			resourceResolver = null;
		}

		public String getFullPathname() {
			IPath path = getResourceResolver().resolvePluginResource(getFilepath());
			return OperatingSystem.escapeShellFileArg(path.toOSString());
		}

		private String getFilepath() {
			return getBaseResoucePath() + "/" + getRuleSetFilename();
		}

		public String getBaseResoucePath() {
			return baseResoucePath;
		}

		public String getRuleSetFilename() {
			return ruleSetFilename;
		}

		public void setBaseResoucePath(final String baseResoucePath) {
			String theBaseResourcePath = baseResoucePath;
			if (theBaseResourcePath.lastIndexOf("/") == theBaseResourcePath.length()) {
				theBaseResourcePath = theBaseResourcePath.substring(0, theBaseResourcePath.length());
			}
			this.baseResoucePath = theBaseResourcePath;
		}
	}

	public void execute(IResource resource) {
		PHPexeItem phpExec = getDefaultPhpExecutable();
		String path = OperatingSystem.escapeShellFileArg(resource.getLocation().toOSString());

		if (null == phpExec) {
			displayNoExecutalbeFoundDialog();
			return;
		}

		String cmdLineArgs = String.format("%s xml %s", path, getRuleSetsForCmdLine());

		PHPToolLauncher launcher = new PHPToolLauncher(QUALIFIED_NAME, phpExec, getScriptFile(), cmdLineArgs,
				getPHPINIEntries(resource.getProject(), resource.getLocation()));
		launcher.setPrintOuput(true);
		launcher.launch(resource.getProject());
	}

	private void displayNoExecutalbeFoundDialog() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
				MessageDialog.openError(shell, "PHP Mess Detector", "No executable php found");
				System.err.println("No executable php found!");
			}
		});
	}

	private String getRuleSetsForCmdLine() {
		StringBuffer resultSets = new StringBuffer();
		resultSets.append(RuleSet.CODESIZE.getFullPathname());
		resultSets.append(",");
		resultSets.append(RuleSet.NAMING.getFullPathname());
		resultSets.append(",");
		resultSets.append(RuleSet.UNUSEDCODE.getFullPathname());
		return resultSets.toString();
	}

	// -------------------------

	public static IPath getScriptFile() {
		return PhpmdPlugin.getDefault().resolvePluginResource("/php/tools/phpmd.php");
	}

	private INIFileEntry[] getPHPINIEntries(IProject project, IPath fileIncludePath) {
		IPath[] pluginIncludePaths = PhpmdPlugin.getDefault().getPluginIncludePaths(project);

		IPath[] includePaths = new IPath[pluginIncludePaths.length + 1];
		System.arraycopy(pluginIncludePaths, 0, includePaths, 0, pluginIncludePaths.length);
		includePaths[includePaths.length - 1] = fileIncludePath;

		return getPHPINIEntries(includePaths);
	}

	private INIFileEntry[] getPHPINIEntries(IPath[] includePaths) {
		INIFileEntry[] entries;
		if (includePaths.length > 0) {
			entries = new INIFileEntry[] { INIFileUtil.createIncludePathEntry(includePaths) };
		} else {
			entries = new INIFileEntry[0];
		}
		return entries;
	}
}
