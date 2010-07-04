package org.phpsrc.eclipse.pti.tools.phpmd.core;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IOpenable;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IFileEditorInput;
import org.phpsrc.eclipse.pti.core.PHPToolkitUtil;
import org.phpsrc.eclipse.pti.ui.Logger;

public class StructuredSelectionResourceCollector extends AbstractResourceCollector {
	public void collect() {
		IStructuredSelection structuredSelection = (IStructuredSelection) getSelection();
		resourceList = new ArrayList<IResource>(structuredSelection.size());

		Iterator<?> iterator = structuredSelection.iterator();

		while (iterator.hasNext()) {
			Object entry = iterator.next();
			try {
				handleEntry(entry);
			} catch (ModelException e) {
				Logger.logException(e);
			}
		}
	}

	private void handleEntry(Object entry) throws ModelException {
		if (entry instanceof IResource) {
			addResource((IResource) entry);
		} else if (entry instanceof ISourceModule) {
			addResource((ISourceModule) entry);
		} else if (entry instanceof IOpenable) {
			addResource((IOpenable) entry);
		} else if (entry instanceof IMember) {
			addResource((IMember) entry);
		} else if (entry instanceof IFileEditorInput) {
			addResource((IFileEditorInput) entry);
		} else if (entry instanceof IScriptFolder) {
			addResource((IScriptFolder) entry);
		}
	}

	private void addResource(ISourceModule entry) throws ModelException {
		if (entry.exists()) {
			IFile file = (IFile) entry.getCorrespondingResource();
			if (PHPToolkitUtil.isPhpFile(file)) {
				addResource(file);
			}
		}
	}

	private void addResource(IOpenable entry) throws ModelException {
		if (entry.exists()) {
			addResource(entry.getCorrespondingResource());
		}
	}

	private void addResource(IMember entry) throws ModelException {
		if (entry.exists()) {
			addResource(entry.getCorrespondingResource());
		}
	}

	private void addResource(IFileEditorInput entry) {
		if (entry.exists()) {
			addResource(entry.getFile());
		}
	}

	private void addResource(IScriptFolder entry) {
		if (entry.exists()) {
			addResource(entry.getResource());
		}
	}
}
