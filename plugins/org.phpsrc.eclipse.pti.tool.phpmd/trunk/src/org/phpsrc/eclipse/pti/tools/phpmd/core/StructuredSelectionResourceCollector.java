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
	@Override
	public void collect() {
		IStructuredSelection structuredSelection = (IStructuredSelection) getSelection();
		resourceList = new ArrayList<IResource>(structuredSelection.size());

		Iterator<?> iterator = structuredSelection.iterator();

		while (iterator.hasNext()) {
			Object entry = iterator.next();
			try {
				if (entry instanceof IResource) {
					addResourceToList((IResource) entry);
				} else if (entry instanceof ISourceModule) {
					ISourceModule concreteEntry = (ISourceModule) entry;
					if (concreteEntry.exists()) {
						IFile file = (IFile) concreteEntry.getCorrespondingResource();
						if (PHPToolkitUtil.isPhpFile(file)) {
							addResourceToList(file);
						}
					}
				} else if (entry instanceof IOpenable) {
					IOpenable concreteEntry = (IOpenable) entry;
					if (concreteEntry.exists()) {
						addResourceToList(concreteEntry.getCorrespondingResource());
					}
				} else if (entry instanceof IMember) {
					IMember concreteEntry = (IMember) entry;
					if (concreteEntry.exists()) {
						addResourceToList(concreteEntry.getResource());
					}
				} else if (entry instanceof IFileEditorInput) {
					IFileEditorInput concreteEntry = (IFileEditorInput) entry;
					if (concreteEntry.exists()) {
						addResourceToList(concreteEntry.getFile());
					}
				} else if (entry instanceof IScriptFolder) {
					IScriptFolder concreteEntry = (IScriptFolder) entry;
					if (concreteEntry.exists()) {
						addResourceToList(concreteEntry.getResource());
					}
				}
			} catch (ModelException e) {
				Logger.logException(e);
			}
		}
	}
}
