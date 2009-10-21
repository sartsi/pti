/*******************************************************************************
 * Copyright (c) 2009, Sven Kiera
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
package org.phpsrc.eclipse.pti.core.launching;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.php.debug.core.debugger.parameters.IDebugParametersKeys;
import org.eclipse.php.internal.debug.core.IPHPDebugConstants;
import org.eclipse.php.internal.debug.core.debugger.AbstractDebuggerConfiguration;
import org.eclipse.php.internal.debug.core.phpIni.PHPINIUtil;
import org.eclipse.php.internal.debug.core.preferences.PHPDebugCorePreferenceNames;
import org.eclipse.php.internal.debug.core.preferences.PHPDebuggersRegistry;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;
import org.eclipse.php.internal.debug.ui.PHPDebugUIPlugin;
import org.eclipse.swt.widgets.Display;
import org.phpsrc.eclipse.pti.core.IPHPCoreConstants;
import org.phpsrc.eclipse.pti.core.PHPToolCorePlugin;
import org.phpsrc.eclipse.pti.core.PHPToolkitUtil;
import org.phpsrc.eclipse.pti.core.php.inifile.INIFileEntry;
import org.phpsrc.eclipse.pti.core.php.inifile.INIFileModifier;
import org.phpsrc.eclipse.pti.ui.Logger;

public class PHPToolLauncher {

	public final static String COMMANDLINE_PLACEHOLDER_FILE = "%file%"; //$NON-NLS-1$

	private final PHPexeItem phpExe;
	private final IPath phpScript;
	private final String commandLineArgs;
	private final INIFileEntry[] iniEntries;

	private boolean printOutput = false;

	public PHPToolLauncher(PHPexeItem phpExe, IPath phpScript) {
		this(phpExe, phpScript, "");
	}

	public PHPToolLauncher(PHPexeItem phpExe, IPath phpScript, String commandLineArgs) {
		this(phpExe, phpScript, commandLineArgs, new INIFileEntry[0]);
	}

	public PHPToolLauncher(PHPexeItem phpExe, IPath phpScript, INIFileEntry[] iniEntries) {
		this(phpExe, phpScript, "", iniEntries);
	}

	public PHPToolLauncher(PHPexeItem phpExe, IPath phpScript, String commandLineArgs, INIFileEntry[] iniEntries) {
		this.phpExe = phpExe;
		this.phpScript = phpScript;
		this.commandLineArgs = commandLineArgs;
		this.iniEntries = iniEntries;
	}

	public String launch(IFile file) {
		String phpFileLocation = null;
		if (PHPToolkitUtil.isPhpFile(file)) {
			IPath location = file.getLocation();
			if (location != null) {
				phpFileLocation = location.toOSString();
			} else {
				phpFileLocation = file.getFullPath().toString();
			}
		}

		return launch(file.getProject(), phpFileLocation);
	}

	public String launch(IProject project) {
		return launch(project, "");
	}

	protected String launch(IProject project, String phpFileLocation) {
		try {
			if (phpFileLocation == null) {
				// Could not find target to launch
				throw new CoreException(new Status(IStatus.ERROR, PHPDebugUIPlugin.ID, IStatus.OK,
						"Launch target not found", null));
			}

			ILaunchConfiguration config = findLaunchConfiguration(project, phpScript.toOSString(), phpScript
					.toOSString(), phpExe, ILaunchManager.RUN_MODE, getPHPExeLaunchConfigType());

			if (config != null) {
				ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
				String phpFileName = OperatingSystem.escapeShellFileArg(phpFileLocation);

				String arguments = commandLineArgs.replaceAll(COMMANDLINE_PLACEHOLDER_FILE, phpFileName);
				wc.setAttribute(IDebugParametersKeys.EXE_CONFIG_PROGRAM_ARGUMENTS, arguments);
				config = wc.doSave();

				if (printOutput)
					Logger.logToConsole(phpExe.getExecutable().toString() + " " + phpScript.toOSString() + " "
							+ arguments);

				PHPToolExecutableLauncher php = new PHPToolExecutableLauncher();
				IProcess process = php.launch(config);
				IStreamsProxy proxy = process.getStreamsProxy();
				String output = proxy.getOutputStreamMonitor().getContents();

				if (printOutput)
					Logger.logToConsole(output);

				return output;
			} else {
				// Could not find launch configuration
				throw new CoreException(new Status(IStatus.ERROR, PHPDebugUIPlugin.ID, IStatus.OK,
						"Launch configuration could not be created for the selected file.", null));
			}
		} catch (CoreException ce) {
			if (printOutput)
				Logger.logToConsole(ce.getMessage());

			final IStatus stat = ce.getStatus();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					ErrorDialog.openError(PHPToolCorePlugin.getActiveWorkbenchShell(), "Error",
							"Unable to execute file", stat);
				}
			});
		}

		return null;
	}

	protected static ILaunchConfigurationType getPHPExeLaunchConfigType() {
		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		return lm.getLaunchConfigurationType(IPHPCoreConstants.LaunchType);
	}

	/**
	 * Locate a configuration to relaunch for the given type. If one cannot be
	 * found, create one.
	 * 
	 * @return a re-useable config or <code>null</code> if none
	 */
	protected ILaunchConfiguration findLaunchConfiguration(IProject phpProject, String phpPathString,
			String phpFileFullLocation, PHPexeItem defaultEXE, String mode, ILaunchConfigurationType configType) {

		ILaunchConfiguration config = null;
		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(
					configType);

			int numConfigs = configs == null ? 0 : configs.length;

			for (int i = 0; i < numConfigs; i++) {
				String fileName = configs[i].getAttribute(IPHPDebugConstants.ATTR_FILE, (String) null);
				String exeName = configs[i].getAttribute(IPHPDebugConstants.ATTR_EXECUTABLE_LOCATION, (String) null);
				boolean isPti = configs[i].getAttribute(PHPToolCorePlugin.PLUGIN_ID, false);

				if (isPti && phpPathString.equals(fileName) && defaultEXE.getExecutable().toString().equals(exeName)) {
					String iniLocation = configs[i].getAttribute(IPHPDebugConstants.ATTR_INI_LOCATION, (String) null);
					if (iniLocation == null || !(new File(iniLocation).exists())) {
						configs[i].delete();
					} else {
						config = configs[i];
					}
					break;
				}
			}

			if (config == null) {
				String iniFile = null;
				File PHPINIFile = createCustomPHPINIFile(config, defaultEXE, iniEntries);
				if (PHPINIFile != null)
					iniFile = PHPINIFile.getAbsolutePath().toString();

				config = createConfiguration(phpProject, phpPathString, phpFileFullLocation, defaultEXE, configType,
						iniFile);
			}
		} catch (CoreException ce) {
			Logger.logException(ce);
		}

		return config;
	}

	public static void deleteAllConfigs(String phpPathString) {
		if (phpPathString == null)
			return;

		ILaunchConfigurationType configType = getPHPExeLaunchConfigType();

		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(
					configType);

			int numConfigs = configs == null ? 0 : configs.length;

			for (int i = 0; i < numConfigs; i++) {
				String fileName = configs[i].getAttribute(IPHPDebugConstants.ATTR_FILE, (String) null);
				boolean isPti = configs[i].getAttribute(PHPToolCorePlugin.PLUGIN_ID, false);

				if (isPti && phpPathString.equals(fileName)) {
					configs[i].delete();
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	protected File createCustomPHPINIFile(ILaunchConfiguration config, PHPexeItem defaultEXE, INIFileEntry[] fileEntries) {
		File oldPHPINIFile = PHPINIUtil.findPHPIni(defaultEXE.getExecutable().toString());

		if (oldPHPINIFile == null) {
			try {
				String iniLocation = config != null ? config.getAttribute(IPHPDebugConstants.ATTR_INI_LOCATION,
						(String) null) : null;
				oldPHPINIFile = iniLocation != null ? new File(iniLocation) : null;
			} catch (CoreException e) {
				Logger.logException(e);
			}
		}

		File tmpPHPINIFile;
		if (oldPHPINIFile != null)
			tmpPHPINIFile = PHPINIUtil.createTemporaryPHPINIFile(oldPHPINIFile);
		else
			tmpPHPINIFile = PHPINIUtil.createTemporaryPHPINIFile();

		if (fileEntries != null && fileEntries.length > 0) {
			try {
				INIFileModifier modifier = new INIFileModifier(tmpPHPINIFile);
				for (INIFileEntry entry : fileEntries) {
					String newValue = entry.getValue();
					if (entry.isAdditional()) {
						String oldValue = modifier.getEntry(entry.getSection(), entry.getName());
						if (oldValue != null)
							newValue = oldValue + ";" + newValue;
					}

					modifier.addEntry(entry.getSection(), entry.getName(), newValue, true, null);
				}
				modifier.close();
			} catch (IOException e) {
				Logger.logException(e);
			}
		}

		return tmpPHPINIFile;
	}

	/**
	 * Create & return a new configuration
	 */
	protected ILaunchConfiguration createConfiguration(IProject phpProject, String phpPathString,
			String phpFileFullLocation, PHPexeItem defaultEXE, ILaunchConfigurationType configType, String iniPath)
			throws CoreException {
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, getNewConfigurationName(phpPathString));

		// Set the delegate class according to selected executable.
		wc.setAttribute(PHPDebugCorePreferenceNames.PHP_DEBUGGER_ID, defaultEXE.getDebuggerID());
		AbstractDebuggerConfiguration debuggerConfiguration = PHPDebuggersRegistry.getDebuggerConfiguration(defaultEXE
				.getDebuggerID());
		wc.setAttribute(PHPDebugCorePreferenceNames.CONFIGURATION_DELEGATE_CLASS, debuggerConfiguration
				.getScriptLaunchDelegateClass());
		wc.setAttribute(IPHPDebugConstants.ATTR_FILE, phpPathString);
		wc.setAttribute(IPHPDebugConstants.ATTR_FILE_FULL_PATH, phpFileFullLocation);
		wc.setAttribute(IPHPDebugConstants.ATTR_EXECUTABLE_LOCATION, defaultEXE.getExecutable().getAbsolutePath()
				.toString());
		if (iniPath == null)
			iniPath = defaultEXE.getINILocation() != null ? defaultEXE.getINILocation().toString() : null;
		wc.setAttribute(IPHPDebugConstants.ATTR_INI_LOCATION, iniPath);
		wc.setAttribute(IPHPDebugConstants.RUN_WITH_DEBUG_INFO, false);
		wc.setAttribute(IDebugParametersKeys.FIRST_LINE_BREAKPOINT, false);
		wc.setAttribute(IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND, false);
		wc.setAttribute(IDebugUIConstants.ATTR_CAPTURE_IN_CONSOLE, true);
		wc.setAttribute(PHPToolCorePlugin.PLUGIN_ID, true);

		config = wc.doSave();

		return config;
	}

	/**
	 * Returns a name for a newly created launch configuration according to the
	 * given file name. In case the name generation fails, return the
	 * "New_configuration" string.
	 * 
	 * @param fileName
	 *            The original file name that this shortcut shoul execute.
	 * @return The new configuration name, or "New_configuration" in case it
	 *         fails for some reason.
	 */
	protected static String getNewConfigurationName(String fileName) {
		String configurationName = "New_configuration";
		try {
			IPath path = Path.fromOSString(fileName);
			String fileExtention = path.getFileExtension();
			String lastSegment = path.lastSegment();
			if (lastSegment != null) {
				if (fileExtention != null) {
					lastSegment = lastSegment.replaceFirst("." + fileExtention, "");
				}
				configurationName = lastSegment;
			}
		} catch (Exception e) {
			Logger.log(Logger.WARNING, "Could not generate configuration name for " + fileName
					+ ".\nThe default name will be used.", e);
		}

		configurationName = "pti_" + configurationName;

		return DebugPlugin.getDefault().getLaunchManager().generateUniqueLaunchConfigurationNameFrom(configurationName);
	}

	public void setPrintOuput(boolean printOutput) {
		this.printOutput = printOutput;
	}
}
