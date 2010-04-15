package org.phpsrc.eclipse.pti.tools.phpmd.core;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

public class TextSelectionResourceCollector extends AbstractResourceCollector {
	public void collect() {
		IEditorInput input = null;
		try {
			input = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
					.getEditorInput();

			if (input != null && input instanceof IFileEditorInput) {
				addResource(((IFileEditorInput) input).getFile());
			}
		} catch (NullPointerException e) {
		}
	}
}
