package org.phpsrc.eclipse.pti.tool.phpunit.ui.wizards;

import java.io.InvalidObjectException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
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
		if (sourceClassPage.finish()) {
			PHPUnit phpunit = PHPUnit.getInstance();
			try {
				return phpunit.createTestSkeleton(sourceClassPage.getSourceClassName(), sourceClassPage
						.getSourceClassFile(), sourceClassPage.getTestClassFilePath());
			} catch (InvalidObjectException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
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

	public boolean setSourceClassName(String className, IDLTKSearchScope scope) {
		return sourceClassPage.setSourceClassName(className, scope);
	}
}