/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.validator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.phpsrc.eclipse.pti.tools.phpdepend.IPHPDependConstants;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.PHPDepend;
import org.phpsrc.eclipse.pti.ui.Logger;

public class PHPDependValidator extends AbstractValidator {
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		// process only PHP files
		if (resource.getType() != IResource.FILE) {
			return null;
		}

		ValidationResult result = new ValidationResult();
		validateFile(result, (IFile) resource, kind);
		return result;
	}

	protected void validateFile(ValidationResult result, IFile file, int kind) {
		// remove the markers currently existing for this resource
		// in case of project/folder, the markers are deleted recursively
		try {
			file.deleteMarkers(IPHPDependConstants.VALIDATOR_PHP_DEPEND_MARKER, false, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		PHPDepend pDepend = PHPDepend.getInstance();
		IProblem[] problems;
		try {
			problems = pDepend.validateResource(file);
			for (IProblem problem : problems) {
				IMarker marker = file.createMarker(IPHPDependConstants.VALIDATOR_PHP_DEPEND_MARKER);
				marker.setAttribute(IMarker.PROBLEM, true);
				marker.setAttribute(IMarker.LINE_NUMBER, problem.getSourceLineNumber());

				if (problem.isWarning()) {
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
					result.incrementWarning(1);
				} else {
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
					result.incrementError(1);
				}
				marker.setAttribute(IMarker.CHAR_START, problem.getSourceStart());
				marker.setAttribute(IMarker.CHAR_END, problem.getSourceEnd());
				marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
			}
		} catch (CoreException e) {
			Logger.logException(e);
		}
	}
}
