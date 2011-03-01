package org.phpsrc.eclipse.pti.tools.phpdepend.validator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;

public class PHPDependValidator extends AbstractValidator {
	public ValidationResult validate(IResource resource, int kind, ValidationState state,
			IProgressMonitor monitor) {

		ValidationResult result = new ValidationResult();
		return result;
	}
}
