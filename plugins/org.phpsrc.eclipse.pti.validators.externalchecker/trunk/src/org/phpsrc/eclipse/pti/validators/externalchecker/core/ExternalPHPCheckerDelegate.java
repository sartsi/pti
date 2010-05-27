package org.phpsrc.eclipse.pti.validators.externalchecker.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IExecutionEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.validators.core.IValidatorOutput;
import org.eclipse.dltk.validators.core.IValidatorProblem;
import org.eclipse.dltk.validators.core.IValidatorReporter;
import org.eclipse.dltk.validators.core.ValidatorReporter;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;
import org.eclipse.php.internal.debug.core.preferences.PHPexes;
import org.phpsrc.eclipse.pti.core.launching.PHPToolLauncher;

/**
 * Delegate implementation of execution of external validators.
 */
@SuppressWarnings("restriction")
class ExternalPHPCheckerDelegate {

	public static final String MARKER_ID = ExternalPHPCheckerPlugin.PLUGIN_ID
			+ ".externalphpcheckerproblem"; // $NON-NLS-1$

	private final String arguments;
	private final String command;
	private final PHPToolLauncher launcher;

	private final IEnvironment environment;
	private final IExecutionEnvironment execEnvironment;
	private final String[] extensions;
	private final List<Rule> rules = new ArrayList<Rule>();

	static interface IExternalReporterDelegate {
		void report(IValidatorProblem problem) throws CoreException;
	}

	public ExternalPHPCheckerDelegate(IEnvironment environment, ExternalPHPChecker externalChecker) {
		this.environment = environment;
		this.execEnvironment = (IExecutionEnvironment) environment
				.getAdapter(IExecutionEnvironment.class);

		for (int i = 0; i < externalChecker.getNRules(); ++i) {
			rules.add(externalChecker.getRule(i));
		}

		this.arguments = externalChecker.getArguments();
		this.extensions = prepareExtensions(externalChecker.getExtensions());
		this.command = prepareCommand(externalChecker.getCommand(), environment);

		this.launcher = new PHPToolLauncher(getQualifiedName(environment.getId()),
				getPHPExecutable(externalChecker.getPhpExecutable()), Path
						.fromOSString(this.command), this.arguments.replaceFirst("%f",
						PHPToolLauncher.COMMANDLINE_PLACEHOLDER_FILE).replaceFirst("%d",
						PHPToolLauncher.COMMANDLINE_PLACEHOLDER_FOLDER));
		this.launcher.setPrintOuput(externalChecker.getPrintOutput());
	}

	public IValidatorReporter createValidatorReporter() {
		return new ValidatorReporter(getMarkerId(), false);
	}

	public String getMarkerId() {
		return MARKER_ID;
	}

	public boolean isValidatorConfigured() {
		if ((command == null) || (command.trim().length() == 0)) {
			return false;
		}

		return true;
	}

	public boolean isValidExtension(String extension) {
		if (extensions.length == 0) {
			return true;
		}

		for (int i = 0; i < extensions.length; ++i) {
			if (extension != null && extension.endsWith(extensions[i])) {
				return true;
			}
		}

		return false;
	}

	public void runValidator(IResource resource, IValidatorOutput console,
			IExternalReporterDelegate delegate) throws CoreException {
		if (resource instanceof IFile) {

			String output = this.launcher.launch((IFile) resource);

			String[] lines = output.split("\n");
			for (String line : lines) {
				IValidatorProblem problem = parseProblem(line.trim());
				delegate.report(problem);
			}
		}
	}

	private String getResourcePath(IResource resource) {
		if (resource.getLocation() != null) {
			return resource.getLocation().makeAbsolute().toOSString();
		}

		URI uri = resource.getLocationURI();
		IFileHandle file = environment.getFile(uri);
		return file.toOSString();
	}

	private IValidatorProblem parseProblem(String problem) {
		List<?> wlist = ExternalPHPCheckerWildcardManager.loadCustomWildcards();

		for (int i = 0; i < rules.size(); i++) {
			Rule rule = (Rule) this.rules.get(i);
			try {
				WildcardMatcher wmatcher = new WildcardMatcher(wlist);
				IValidatorProblem cproblem = wmatcher.match(rule, problem);
				if (cproblem != null) {
					return cproblem;
				}
			} catch (Exception x) {
				continue;
			}
		}

		return null;
	}

	private String prepareCommand(Map<IEnvironment, String> commands, IEnvironment environment) {
		String result = (String) commands.get(environment);
		if (result != null) {
			result = result.trim();
		}

		return result;
	}

	private String[] prepareExtensions(String extensions) {
		final String[] parts = extensions.split("[\\s;]+"); // $NON-NLS-1$
		for (int i = 0; i < parts.length; ++i) {
			if ("*".equals(parts[i])) // $NON-NLS-1$
			{
				return CharOperation.NO_STRINGS;
			}
		}

		return parts;
	}

	private PHPexeItem getPHPExecutable(String phpExecutableId) {
		PHPexeItem[] items = PHPexes.getInstance().getAllItems();
		for (PHPexeItem item : items) {
			if (item.getName().equals(phpExecutableId))
				return item;
		}

		return null;
	}

	public static QualifiedName getQualifiedName(String checkerId) {
		return new QualifiedName(ExternalPHPCheckerPlugin.PLUGIN_ID, "external_checker#"
				+ checkerId);
	}
}
