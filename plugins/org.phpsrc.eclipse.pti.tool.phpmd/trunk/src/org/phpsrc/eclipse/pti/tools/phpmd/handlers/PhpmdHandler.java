package org.phpsrc.eclipse.pti.tools.phpmd.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.phpsrc.eclipse.pti.tools.phpmd.core.IResourceCollector;
import org.phpsrc.eclipse.pti.tools.phpmd.core.ResourceCollectorFactory;

public class PhpmdHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
		IResourceCollector collector = ResourceCollectorFactory.factory(currentSelection);

		if (null == collector) {
			return null;
		}

		collector.collect();

		System.out.println(collector.getResources());

		return null;
	}
}