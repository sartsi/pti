package org.phpsrc.eclipse.pti.tool.phpunit.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class CreatePHPUnitTestCaseWizard extends Wizard implements INewWizard {

	private IWorkbench workbench;
	private IStructuredSelection selection;
	private PHPUnitTestCaseCreationWizardPage sourceClassPage;

	public CreatePHPUnitTestCaseWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	public boolean performFinish() {
		IProject project = sourceClassPage.getProject();
		System.out.println(project);
		return false;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}

	public void addPages() {
		sourceClassPage = new PHPUnitTestCaseCreationWizardPage(selection);
		addPage(sourceClassPage);
	}

	public boolean setSourceClassName(String className) {
		return sourceClassPage.setSourceClassName(className);
	}
}
