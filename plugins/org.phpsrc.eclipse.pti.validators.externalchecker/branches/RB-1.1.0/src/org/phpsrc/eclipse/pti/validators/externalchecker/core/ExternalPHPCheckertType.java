package org.phpsrc.eclipse.pti.validators.externalchecker.core;

import org.eclipse.dltk.validators.core.AbstractValidatorType;
import org.eclipse.dltk.validators.core.ISourceModuleValidator;
import org.eclipse.dltk.validators.core.IValidator;
import org.phpsrc.eclipse.pti.core.IPHPCoreConstants;

public class ExternalPHPCheckertType extends AbstractValidatorType {

	public static final String ID = "org.phpsrc.eclipse.pti.validators.externalPHPChecker"; //$NON-NLS-1$

	public IValidator createValidator(String id) {
		return new ExternalPHPChecker(id, getName(), this);
	}

	public String getID() {
		return ID;
	}

	public String getName() {
		return "External PHP Script";
	}

	public String getNature() {
		return IPHPCoreConstants.PHPNatureID;
	}

	public boolean isBuiltin() {
		return false;
	}

	public boolean supports(Class validatorType) {
		return ISourceModuleValidator.class.equals(validatorType);
	}

}
