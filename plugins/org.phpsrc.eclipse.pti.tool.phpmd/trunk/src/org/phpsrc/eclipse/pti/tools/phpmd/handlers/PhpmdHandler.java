package org.phpsrc.eclipse.pti.tools.phpmd.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class PhpmdHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection currentSelection = HandlerUtil.getCurrentSelection(event);

		if (null == currentSelection) {
			return null;
		}

		if (currentSelection instanceof ITextSelection) {
			System.out.println("Current selection is an ITextSelection");
		} else if (currentSelection instanceof IStructuredSelection) {
			System.out.println("Current selection is an IStructuredSelection");
		}

		return null;
	}
}