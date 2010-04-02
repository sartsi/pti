package org.phpsrc.eclipse.pti.tools.phpdepend.core.listener;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ISourceModule;
import org.phpsrc.eclipse.pti.core.PHPToolkitUtil;
import org.phpsrc.eclipse.pti.core.php.source.PHPSourceFile;
import org.phpsrc.eclipse.pti.tools.phpdepend.IPHPDependConstants;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.IMetricElement;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.IMetricRunSessionListener;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricFunction;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricMethod;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricResult;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricRunSession;
import org.phpsrc.eclipse.pti.ui.Logger;

public class PHPDependProblemMarkerListener implements IMetricRunSessionListener {

	@Override
	public void sessionAdded(MetricRunSession metricRunSession) {
		if (metricRunSession != null) {
			try {
				createProblemMarkerFromElement(metricRunSession.getSummaryRoot());
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void sessionRemoved(MetricRunSession metricRunSession) {
	}

	private void createProblemMarkerFromElement(IMetricElement element) throws CoreException {
		if (element.getStatus().isErrorOrWarning()) {
			IResource resource = element.getResource();
			if (resource != null && resource instanceof IFile) {
				IFile file = (IFile) resource;
				int lineNumber = 1;

				if (element instanceof MetricMethod || element instanceof MetricFunction) {
					try {
						PHPSourceFile sourceFile = new PHPSourceFile(file);
						ISourceModule module = PHPToolkitUtil.getSourceModule(file);
						if (module != null) {

						}
					} catch (IOException e) {
						Logger.logException(e);
					}
				}

				for (MetricResult result : element.getResults()) {
					String msg = result.metric != null ? result.metric.name : result.id;
					if (result.hasError()) {
						createFileMarker(file, IMarker.SEVERITY_ERROR, lineNumber, msg);
					} else if (result.hasWarning()) {
						createFileMarker(file, IMarker.SEVERITY_WARNING, lineNumber, msg);
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

	private void createFileMarker(IFile file, int serverity, int lineNumber, String msg)
			throws CoreException {
		IMarker marker = file.createMarker(IPHPDependConstants.VALIDATOR_PHP_DEPEND_MARKER);
		marker.setAttribute(IMarker.PROBLEM, true);
		marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		marker.setAttribute(IMarker.SEVERITY, serverity);
		// marker.setAttribute(IMarker.CHAR_START, problem.getSourceStart());
		// marker.setAttribute(IMarker.CHAR_END, problem.getSourceEnd());
		marker.setAttribute(IMarker.MESSAGE, msg);
	}
}
