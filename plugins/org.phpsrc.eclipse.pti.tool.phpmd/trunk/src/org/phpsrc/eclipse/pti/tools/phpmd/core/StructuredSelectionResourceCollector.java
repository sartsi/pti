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
					addResourceToList(resourceList, (IResource) entry);
				} else if (entry instanceof ISourceModule) {
					if (((ISourceModule) entry).exists()) {
						IFile file = (IFile) ((ISourceModule) entry).getCorrespondingResource();
						if (PHPToolkitUtil.isPhpFile(file)) {
							addResourceToList(resourceList, file);
						}
					}
				} else if (entry instanceof IOpenable) {
					if (((IOpenable) entry).exists()) {
						addResourceToList(resourceList, ((IOpenable) entry).getCorrespondingResource());
					}
				} else if (entry instanceof IMember) {
					if (((IMember) entry).exists()) {
						addResourceToList(resourceList, ((IMember) entry).getResource());
					}
				} else if (entry instanceof IFileEditorInput) {
					if (((IFileEditorInput) entry).exists()) {
						addResourceToList(resourceList, ((IFileEditorInput) entry).getFile());
					}
				} else if (entry instanceof IScriptFolder) {
					if (((IScriptFolder) entry).exists()) {
						addResourceToList(resourceList, ((IScriptFolder) entry).getResource());
					}
				}
			} catch (ModelException e) {
				Logger.logException(e);
			}
		}
	}
}
