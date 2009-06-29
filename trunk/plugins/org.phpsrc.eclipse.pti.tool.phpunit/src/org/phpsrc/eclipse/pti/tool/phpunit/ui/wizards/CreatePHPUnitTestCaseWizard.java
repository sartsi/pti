package org.phpsrc.eclipse.pti.tool.phpunit.ui.wizards;

import java.io.InvalidObjectException;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.phpsrc.eclipse.pti.tool.phpunit.core.PHPUnit;

public class CreatePHPUnitTestCaseWizard extends Wizard implements INewWizard {

	private IWorkbench workbench;
	private IStructuredSelection selection;
	private PHPUnitTestCaseCreationWizardPage sourceClassPage;

	public CreatePHPUnitTestCaseWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	public boolean performFinish() {
		PHPUnit phpunit = PHPUnit.getInstance();
		try {
			phpunit.createTestSkeleton(sourceClassPage.getSourceClassName(), sourceClassPage.getSourceClassFile(),
					sourceClassPage.getTestClassFilePath());
		} catch (InvalidObjectException e) {
			e.printStackTrace();
		}

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
