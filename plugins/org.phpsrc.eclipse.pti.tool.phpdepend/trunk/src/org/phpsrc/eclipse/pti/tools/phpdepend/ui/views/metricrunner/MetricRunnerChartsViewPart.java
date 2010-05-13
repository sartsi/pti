package org.phpsrc.eclipse.pti.tools.phpdepend.ui.views.metricrunner;

import java.io.File;
import java.net.MalformedURLException;

import org.eclipse.gmf.runtime.draw2d.ui.render.RenderedImage;
import org.eclipse.gmf.runtime.draw2d.ui.render.factory.RenderedImageFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricRunSession;
import org.phpsrc.eclipse.pti.ui.widgets.ImageCanvas;

public class MetricRunnerChartsViewPart extends ViewPart {

	public static final String NAME = "org.phpsrc.eclipse.pti.tools.phpdepend.ui.views.metricrunnercharts"; //$NON-NLS-1$

	private MetricRunSession metricRunSession;
	private ImageCanvas fSummaryPyramid;

	public void createPartControl(Composite parent) {
		fSummaryPyramid = new ImageCanvas(parent, SWT.BORDER);
	}

	public void setFocus() {
		fSummaryPyramid.setFocus();
	}

	public void dispose() {
		fSummaryPyramid.dispose();
		super.dispose();
	}

	public void setActiveMetricRunSession(MetricRunSession metricRunSession) {
		if (metricRunSession != null) {
			this.metricRunSession = metricRunSession;

			File summaryPyramid = metricRunSession.getSummaryPyramidFile();
			if (summaryPyramid != null && summaryPyramid.exists()) {
				try {
					RenderedImage image = RenderedImageFactory.getInstance(summaryPyramid.toURI().toURL());
					fSummaryPyramid.setImage(image.getSWTImage());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
