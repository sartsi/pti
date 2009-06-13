package org.phpsrc.eclipse.pti.validators.externalchecker.ui;

import org.phpsrc.eclipse.pti.validators.externalchecker.core.Rule;

public interface IRulesListViewer {

	public void addRule(Rule r);

	public void removeRule(Rule r);

	public void updateRule(Rule r);

}
