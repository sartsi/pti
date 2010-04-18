package org.phpsrc.eclipse.pti.tools.phpdepend.core.listener;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.SourceRange;
import org.phpsrc.eclipse.pti.core.PHPToolkitUtil;
import org.phpsrc.eclipse.pti.core.php.source.PHPSourceFile;
import org.phpsrc.eclipse.pti.tools.phpdepend.IPHPDependConstants;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.IMetricElement;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.IMetricRunSessionListener;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricClass;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricFunction;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricMethod;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricResult;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricRunSession;
import org.phpsrc.eclipse.pti.ui.Logger;

public class PHPDependProblemMarkerListener implements IMetricRunSessionListener {

	private static int DEFAULT_LINE_NUMBER = 1;
	private static ISourceRange DEFAULT_SOURCE_RANGE = new SourceRange(0, 0);

	public void sessionAdded(MetricRunSession metricRunSession) {
		if (metricRunSession != null && metricRunSession.getDependentResource() != null) {
			try {
				createProblemMarkerFromElement(metricRunSession.getSummaryRoot());
			} catch (CoreException e) {
				Logger.logException(e);
			} catch (IOException e) {
				Logger.logException(e);
			}
		}
	}

	public void sessionRemoved(MetricRunSession metricRunSession) {
	}

	private void createProblemMarkerFromElement(IMetricElement element) throws CoreException, IOException {
		if (element.getStatus().isErrorOrWarning()) {
			IResource resource = element.getResource();
			if (resource != null && resource instanceof IFile) {
				IFile file = (IFile) resource;
				PHPSourceFile sourceFile = new PHPSourceFile(file);
				ISourceRange range = findSourceRange(sourceFile, element);
				int lineNumber = sourceFile.findLineNumberForOffset(range.getOffset());

				for (MetricResult result : element.getResults()) {
					String msg = result.value + " for Metric \"";
					if (result.metric != null)
						msg += result.metric.name + " (" + result.id + ")";
					else
						msg += result.id;
					msg += "\" is out of range";

					if (result.hasError()) {
						createFileMarker(file, IMarker.SEVERITY_ERROR, lineNumber, range, msg);
					} else if (result.hasWarning()) {
						createFileMarker(file, IMarker.SEVERITY_WARNING, lineNumber, range, msg);
					}
				}
			}
		}

		for (IMetricElement child : element.getChildren()) {
			if (child.hasWarnings() || child.hasErrors()) {
				createProblemMarkerFromElement(child);
			}
		}
	}

	private ISourceRange findSourceRange(PHPSourceFile sourceFile, MetricMethod metricMethod) {
		try {
			ISourceModule module = sourceFile.getSourceModule();
			if (module != null) {
				MetricClass metricClass = (MetricClass) metricMethod.getParent();

				IMethod method = PHPToolkitUtil.getClassMethod(module, metricClass.getName(), metricMethod.getName());
				if (method != null && method.exists()) {
					return method.getNameRange();
				}
			}
		} catch (CoreException e) {
			Logger.logException(e);
		}

		return DEFAULT_SOURCE_RANGE;
	}

	private ISourceRange findSourceRange(PHPSourceFile sourceFile, MetricFunction metricFunction) {
		try {
			ISourceModule module = sourceFile.getSourceModule();
			if (module != null) {
				IMethod func = PHPToolkitUtil.getFunction(module, metricFunction.getName());
				if (func != null & func.exists()) {
					return func.getNameRange();
				}
			}
		} catch (ModelException e) {
			Logger.logException(e);
		}

		return DEFAULT_SOURCE_RANGE;
	}

	private ISourceRange findSourceRange(PHPSourceFile sourceFile, MetricClass metricClass) {
		try {
			ISourceModule module = sourceFile.getSourceModule();
			if (module != null) {
				IType clazz = PHPToolkitUtil.getClassType(module, metricClass.getName());

				if (clazz != null && clazz.exists()) {
					return clazz.getNameRange();
				}
			}
		} catch (CoreException e) {
			Logger.logException(e);
		}

		return DEFAULT_SOURCE_RANGE;
	}

	private ISourceRange findSourceRange(PHPSourceFile sourceFile, IMetricElement element) {
		if (element instanceof MetricClass) {
			return findSourceRange(sourceFile, (MetricClass) element);
		} else if (element instanceof MetricMethod) {
			return findSourceRange(sourceFile, (MetricMethod) element);
		} else if (element instanceof MetricFunction) {
			return findSourceRange(sourceFile, (MetricFunction) element);
		}

		return DEFAULT_SOURCE_RANGE;
	}

	private void createFileMarker(IFile file, int serverity, int lineNumber, ISourceRange range, String msg)
			throws CoreException {
		IMarker marker = file.createMarker(IPHPDependConstants.VALIDATOR_PHP_DEPEND_MARKER);
		marker.setAttribute(IMarker.PROBLEM, true);
		marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		marker.setAttribute(IMarker.SEVERITY, serverity);
		if (range != null && range.getOffset() > 0) {
			marker.setAttribute(IMarker.CHAR_START, range.getOffset());
			marker.setAttribute(IMarker.CHAR_END, range.getOffset() + range.getLength());
		}
		marker.setAttribute(IMarker.MESSAGE, msg);
	}
}
