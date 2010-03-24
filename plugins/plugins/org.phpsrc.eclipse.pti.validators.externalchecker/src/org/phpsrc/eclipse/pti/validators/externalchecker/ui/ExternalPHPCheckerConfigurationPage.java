package org.phpsrc.eclipse.pti.validators.externalchecker.ui;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.dltk.ui.environment.EnvironmentPathBlock;
import org.eclipse.dltk.ui.environment.IEnvironmentPathBlockListener;
import org.eclipse.dltk.validators.ui.ValidatorConfigurationPage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.phpsrc.eclipse.pti.ui.preferences.dialogfields.PHPExecutableDialogField;
import org.phpsrc.eclipse.pti.validators.externalchecker.core.ExternalPHPChecker;
import org.phpsrc.eclipse.pti.validators.externalchecker.core.ExternalPHPCheckerPlugin;
import org.phpsrc.eclipse.pti.validators.externalchecker.core.Rule;

public class ExternalPHPCheckerConfigurationPage extends ValidatorConfigurationPage {

	private PHPExecutableDialogField fPhpExecutable;
	private StringDialogField fArguments;
	private EnvironmentPathBlock fPath;
	private StringDialogField fExtensions;
	private SelectionButtonDialogField fDebugPrintOutput;

	private Table fTable;
	private TableViewer tableViewer;
	private Button addRule;
	private Button delRule;
	private RulesList rulesList = new RulesList();

	private String message = ""; //$NON-NLS-1$
	private int messageType = IStatus.OK;

	private final String TYPES = "TYPES"; //$NON-NLS-1$

	public RulesList getRulesList() {
		return rulesList;
	}

	private String[] columnNames = new String[] { "RULES", "TYPES" }; //$NON-NLS-1$ //$NON-NLS-2$

	public ExternalPHPCheckerConfigurationPage() {
	}

	public IStatus getStatus() {
		return new Status(messageType, ExternalPHPCheckerPlugin.PLUGIN_ID, message);
	}

	private void resetMessage() {
		this.message = ""; //$NON-NLS-1$
		this.messageType = IStatus.OK;
	}

	private void setMessage(IEnvironment env, String message, int type) {
		String pattern = "{0}: {1}";
		message = NLS.bind(pattern, new String[] { env.getName(), message });
		setMessage(message, type);
	}

	private void setMessage(String message, int type) {
		if (type > messageType) {
			this.message = message;
			this.messageType = type;
		}
	}

	protected void validateTclCheckerPath() {
		Map envs = fPath.getPaths();
		for (Iterator it = envs.keySet().iterator(); it.hasNext();) {
			IEnvironment env = (IEnvironment) it.next();
			String txtPath = envs.get(env).toString();
			txtPath = txtPath.trim();

			if ("".equals(txtPath)) { //$NON-NLS-1$
				/*
				 * setMessage(env,
				 * ValidatorMessages.ValidatorMessages_path_isempty,
				 * IStatus.INFO);
				 */
				continue;
			}

			IPath path = Path.fromPortableString(txtPath);
			IFileHandle file = env.getFile(path);

			if (file == null) {
				setMessage(env, "Entered path is invalid", IStatus.ERROR);
				continue;
			} else if (!file.isFile()) {
				setMessage(env, "Entered path doesn't exist", IStatus.ERROR);
				continue;
			} else if (!file.exists()) {
				setMessage(env, "Entered path doesn't exist", IStatus.ERROR);
				continue;
			}
		}
	}

	protected void validate() {
		resetMessage();
		validateTclCheckerPath();
		updateStatus();
	}

	public void applyChanges() {
		ExternalPHPChecker externalChecker = getExternalChecker();
		externalChecker.setArguments(this.fArguments.getText());
		externalChecker.setCommand(this.fPath.getPaths());
		externalChecker.setRules(rulesList.getRules());
		externalChecker.setExtensions(this.fExtensions.getText());
		externalChecker.setPhpExecutable(this.fPhpExecutable.getText());
		externalChecker.setPrintOutput(fDebugPrintOutput.isSelected());
	}

	private void createPathBrowse(final Composite parent, int columns) {
		this.fPath = new EnvironmentPathBlock();
		this.fPath.createControl(parent, columns);
		fPath.addListener(new IEnvironmentPathBlockListener() {
			public void valueChanged(Map paths) {
				validate();
			}
		});
	}

	public void createControl(final Composite ancestor, int columns) {
		createFields();

		this.createPathBrowse(ancestor, columns);

		this.fPhpExecutable.doFillIntoGrid(ancestor, columns);
		this.fArguments.doFillIntoGrid(ancestor, columns);

		Label label = new Label(ancestor, SWT.WRAP);
		label.setText("You can use %f for source file path and %d for folder path.");
		GridData data = new GridData(SWT.FILL, SWT.FILL, false, false);
		data.horizontalSpan = columns;
		data.minimumWidth = 100;
		data.widthHint = 100;
		label.setLayoutData(data);

		this.fExtensions.doFillIntoGrid(ancestor, columns);

		label = new Label(ancestor, SWT.WRAP);
		label.setText("Comma separated list of extensions");
		data = new GridData(SWT.FILL, SWT.FILL, false, false);
		data.horizontalSpan = columns;
		data.minimumWidth = 100;
		data.widthHint = 100;
		label.setLayoutData(data);
		this.rulesList.getRules().clear();

		this.fDebugPrintOutput.doFillIntoGrid(ancestor, columns);

		// GridLayout layout = (GridLayout)ancestor.getLayout();

		Group group = new Group(ancestor, SWT.NONE);
		group.setText("Pattern rules");
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = columns;
		group.setLayoutData(data);
		GridLayout layout = new GridLayout(2, false);
		group.setLayout(layout);

		label = new Label(ancestor, SWT.WRAP);
		label
				.setText("Pattern is regular expression.\nYou must specify %f for filename, %n for line number and %m for message.\n");
		data = new GridData(SWT.FILL, SWT.FILL, false, false);
		data.horizontalSpan = columns;
		data.minimumWidth = 100;
		data.widthHint = 100;
		label.setLayoutData(data);
		// label.
		// label.setSize(label.computeSize(100, SWT.DEFAULT));

		fTable = new Table(group, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
				| SWT.HIDE_SELECTION);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.widthHint = 300;
		data.heightHint = 100;
		fTable.setLayoutData(data);

		// fTable.setLayout(layout);
		fTable.setLinesVisible(true);
		fTable.setHeaderVisible(true);
		// fTable.setSize(500, 500);

		TableColumn col1 = new TableColumn(fTable, SWT.LEFT, 0);
		col1.setWidth(200);
		col1.setText("Output rule");

		TableColumn col2 = new TableColumn(fTable, SWT.LEFT, 1);
		col2.setWidth(100);
		col2.setText("Type");

		tableViewer = new TableViewer(fTable);
		tableViewer.setColumnProperties(columnNames);
		CellEditor[] editors = new CellEditor[columnNames.length];

		TextCellEditor textEditor = new TextCellEditor(fTable);
		((Text) textEditor.getControl()).setTextLimit(60);
		editors[0] = textEditor;

		ComboBoxCellEditor comboEditor = new ComboBoxCellEditor(fTable, rulesList.getTypes(), SWT.READ_ONLY);
		editors[1] = comboEditor;

		tableViewer.setCellEditors(editors);

		tableViewer.setCellModifier(new RuleCelllModifier(this));

		tableViewer.setContentProvider(new RulesContentProvider());
		tableViewer.setLabelProvider(new RulesLabelProvider());
		tableViewer.setInput(rulesList);

		Composite buttons = new Composite(group, SWT.NONE);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);
		data = new GridData(SWT.FILL, SWT.NONE, false, false);
		data.verticalAlignment = SWT.TOP;
		addRule = new Button(buttons, SWT.PUSH);
		addRule.setLayoutData(data);
		addRule.setText("Add Rule");
		addRule.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent ev) {
				rulesList.addRule();
			}
		});

		delRule = new Button(buttons, SWT.PUSH);
		delRule.setLayoutData(data);
		delRule.setText("Delete Rule");
		delRule.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent ev) {
				Rule rule = (Rule) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
				if (rule != null)
					rulesList.removeRule(rule);
			}
		});

		updateValuesFrom();
	}

	private ExternalPHPChecker getExternalChecker() {
		return (ExternalPHPChecker) getValidator();

	}

	private void updateValuesFrom() {
		ExternalPHPChecker externalChecker = getExternalChecker();
		this.fArguments.setText(externalChecker.getArguments());

		this.fPath.setPaths(externalChecker.getCommand());
		this.fExtensions.setText(externalChecker.getExtensions());

		this.rulesList.getRules().clear();
		for (int i = 0; i < externalChecker.getNRules(); i++) {
			Rule r = externalChecker.getRule(i);
			rulesList.addRule(r);
		}

		this.fPhpExecutable.setText(externalChecker.getPhpExecutable());
		this.fDebugPrintOutput.setSelection(externalChecker.getPrintOutput());
	}

	private void createFields() {
		this.fPhpExecutable = new PHPExecutableDialogField();
		this.fPhpExecutable.setLabelText("PHP Executable:");
		this.fArguments = new StringDialogField();
		this.fArguments.setLabelText("Checker arguments:");
		this.fExtensions = new StringDialogField();
		this.fExtensions.setLabelText("Filename extensions:");
		this.fDebugPrintOutput = new SelectionButtonDialogField(SWT.CHECK);
		this.fDebugPrintOutput.setLabelText("print PHP output to console");
	}

	public class RulesContentProvider implements IStructuredContentProvider, IRulesListViewer {

		public Object[] getElements(Object inputElement) {
			return rulesList.getRules().toArray();
		}

		public void dispose() {
			rulesList.removeChangeListener(this);
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput != null)
				((RulesList) newInput).addChangeListener(this);
			if (oldInput != null)
				((RulesList) oldInput).removeChangeListener(this);
		}

		public void addRule(Rule r) {
			tableViewer.add(r);
			tableViewer.editElement(r, 0);
		}

		public void removeRule(Rule r) {
			tableViewer.remove(r);
		}

		public void updateRule(Rule r) {
			tableViewer.update(r, null);
		}

	}

	public List getColumnNames() {
		return Arrays.asList(columnNames);
	}

	public String[] getChoices(String property) {
		if (TYPES.equals(property)) {
			return rulesList.getTypes();
		}
		return new String[0];
	}
}
