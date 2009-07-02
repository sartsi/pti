package org.phpsrc.eclipse.pti.tool.phpunit.ui.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.internal.core.SourceType;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.phpsrc.eclipse.pti.core.PHPCoreID;
import org.phpsrc.eclipse.pti.core.PHPToolCorePlugin;
import org.phpsrc.eclipse.pti.core.search.PHPSearchEngine;

public class PHPUnitTestCaseCreationWizardPage extends WizardPage {

	protected Text classText;
	protected IFile classFile;
	protected Text containerText;
	protected Text fileText;
	private IStructuredSelection selection;
	protected IProject project;

	protected static final String UTF_8 = "UTF 8"; //$NON-NLS-1$
	protected static final String NO_TEMPLATE = "-- none -- "; //$NON-NLS-1$
	protected Label targetResourceLabel;

	protected boolean testFileExists = false;

	public PHPUnitTestCaseCreationWizardPage(final IStructuredSelection selection) {
		super("wizardPage"); //$NON-NLS-1$
		setTitle("New PHPUnit Test Case"); //$NON-NLS-1$
		this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 10;
		container.setLayout(layout);

		final Group classGroup = new Group(container, SWT.RESIZE);
		classGroup.setText("Source");

		final GridLayout classLayout = new GridLayout();
		classGroup.setLayout(classLayout);
		classLayout.numColumns = 3;
		classLayout.verticalSpacing = 9;
		Label classLabel = new Label(classGroup, SWT.NULL);
		classLabel.setText("Class Name"); //$NON-NLS-1$

		classText = new Text(classGroup, SWT.BORDER | SWT.SINGLE);
		classText.setEditable(false);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 400;
		classText.setLayoutData(gd);
		classText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				dialogChanged();
			}
		});

		final Button sourceButton = new Button(classGroup, SWT.PUSH);
		sourceButton.setText("Browse..."); //$NON-NLS-1$
		sourceButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {

			}
		});

		final Group fileGroup = new Group(container, SWT.RESIZE);
		fileGroup.setText("Test");

		final GridLayout fileLayout = new GridLayout();
		fileGroup.setLayout(fileLayout);
		fileLayout.numColumns = 3;
		fileLayout.verticalSpacing = 9;
		Label fileLabel = new Label(fileGroup, SWT.NULL);
		fileLabel.setText("Source Folder"); //$NON-NLS-1$

		containerText = new Text(fileGroup, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 400;
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				dialogChanged();
			}
		});

		final Button containerButton = new Button(fileGroup, SWT.PUSH);
		containerButton.setText("Browse..."); //$NON-NLS-1$
		containerButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				handleBrowse();
			}
		});

		targetResourceLabel = new Label(fileGroup, SWT.NULL);
		targetResourceLabel.setText("File Name"); //$NON-NLS-1$

		fileText = new Text(fileGroup, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		// gd.widthHint = 300;
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				dialogChanged();
			}
		});

		initialize();
		dialogChanged();
		setControl(container);
	}

	private void setSourceClass(SearchMatch match) {
		SourceType type = (SourceType) match.getElement();

		classText.setText(type.getElementName());
		classFile = (IFile) match.getResource();

		String path = type.getPath().toOSString();
		if (path.indexOf("\\", 1) >= 0) {
			String containerPath = path.substring(0, path.indexOf("\\", 1));
			containerPath += "\\tests";
			containerPath += path.substring(path.indexOf("\\", 1), path.lastIndexOf("\\"));
			containerText.setText(containerPath);
		} else {
			containerText.setText(path + "\\tests");
		}

		String fileName = match.getResource().getName();
		int dotPos = fileName.indexOf(".");
		String testFileName = fileName.substring(0, dotPos) + "Test" + fileName.substring(dotPos);
		fileText.setText(testFileName);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */
	private void initialize() {
		if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1) {
				return;
			}

			Object obj = ssel.getFirstElement();
			if (obj instanceof IAdaptable) {
				obj = ((IAdaptable) obj).getAdapter(IResource.class);
			}

			IContainer container = null;
			if (obj instanceof IResource) {
				if (obj instanceof IContainer) {
					container = (IContainer) obj;
				} else {
					container = ((IResource) obj).getParent();
				}
			}

			if (container != null) {
				containerText.setText(container.getFullPath().toString());
				this.project = container.getProject();
			}
		}
	}

	protected void setInitialFileName(final String fileName) {
		fileText.setText(fileName);
		// fixed bug 157145 - highlight the newfile word in the file name input
		fileText.setSelection(0, 7);
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleBrowse() {
		final ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace()
				.getRoot(), false, "PHPFileCreationWizardPage.9"); //$NON-NLS-1$
		dialog.showClosedProjects(false);
		if (dialog.open() == Window.OK) {
			final Object[] result = dialog.getResult();
			if (result.length == 1)
				containerText.setText(((Path) result[0]).toOSString());
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */
	protected void dialogChanged() {
		testFileExists = false;

		final String container = getContainerName();
		final String fileName = getFileName();

		if (container.length() == 0) {
			updateStatus("PHPFileCreationWizardPage.10"); //$NON-NLS-1$
			return;
		}

		final IContainer containerFolder = getContainer(container);
		if (containerFolder == null || !containerFolder.exists()) {
			setMessage("Selected folder does not exist and will be created", WizardPage.INFORMATION);
		} else {
			if (!containerFolder.getProject().isOpen()) {
				updateStatus("PHPFileCreationWizardPage.12"); //$NON-NLS-1$
				return;
			}
			if (fileName != null && !fileName.equals("") && containerFolder.getFile(new Path(fileName)).exists()) { //$NON-NLS-1$
				setMessage("File exists and will be overwritten", WizardPage.WARNING);
				testFileExists = true;
			}
		}

		this.project = null;
		if (container != null && container.length() > 0 && container.indexOf("\\") >= 0) {
			IContainer projectContainer = getContainer(container.substring(0, container.indexOf("\\", 1)));
			if (projectContainer != null)
				this.project = projectContainer.getProject();
		}

		if (this.project == null) {
			updateStatus("Project does not exist"); //$NON-NLS-1$
			return;
		}

		int dotIndex = fileName.lastIndexOf('.');
		if (fileName.length() == 0 || dotIndex == 0) {
			updateStatus("PHPFileCreationWizardPage.15"); //$NON-NLS-1$
			return;
		}

		if (dotIndex != -1) {
			String fileNameWithoutExtention = fileName.substring(0, dotIndex);
			for (int i = 0; i < fileNameWithoutExtention.length(); i++) {
				char ch = fileNameWithoutExtention.charAt(i);
				if (!(Character.isJavaIdentifierPart(ch) || ch == '.' || ch == '-')) {
					updateStatus("PHPFileCreationWizardPage.16"); //$NON-NLS-1$
					return;
				}
			}
		}

		final IContentType contentType = Platform.getContentTypeManager().getContentType(PHPCoreID.ContentTypeID_PHP);
		if (!contentType.isAssociatedWith(fileName)) {
			// fixed bug 195274
			// get the extensions from content type
			final String[] fileExtensions = contentType.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
			StringBuffer buffer = new StringBuffer("PHPFileCreationWizardPage.17"); //$NON-NLS-1$
			buffer.append(fileExtensions[0]);
			for (String extension : fileExtensions) {
				buffer.append(", ").append(extension); //$NON-NLS-1$
			}
			buffer.append("]"); //$NON-NLS-1$
			updateStatus(buffer.toString());
			return;
		}

		updateStatus(null);
	}

	protected IContainer getContainer(final String text) {
		final Path path = new Path(text);

		final IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		return resource instanceof IContainer ? (IContainer) resource : null;

	}

	protected void updateStatus(final String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public boolean setSourceClassName(String className) {
		return setSourceClassName(className, PHPSearchEngine.createWorkspaceScope());
	}

	public boolean setSourceClassName(String className, IDLTKSearchScope scope) {
		SearchMatch[] matches = PHPSearchEngine.findClass(className, scope);
		if (matches.length > 0) {
			setSourceClass(matches[0]);
			return true;
		}

		return false;
	}

	public String getSourceClassName() {
		return classText.getText();
	}

	public IFile getSourceClassFile() {
		return classFile;
	}

	public String getContainerName() {
		return containerText.getText();
	}

	public String getFileName() {
		return fileText.getText();
	}

	public String getTestClassFilePath() {
		return getContainerName() + "\\" + getFileName();
	}

	public IProject getProject() {
		return project;
	}

	public boolean finish() {
		if (testFileExists) {
			return MessageDialog.openConfirm(PHPToolCorePlugin.getActiveWorkbenchShell(), "Test file exists",
					"Test file exists. Overwrite?");
		}
		return true;
	}
}