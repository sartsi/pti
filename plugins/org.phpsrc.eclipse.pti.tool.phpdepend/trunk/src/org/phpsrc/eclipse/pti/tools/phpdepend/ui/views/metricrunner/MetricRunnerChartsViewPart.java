package org.phpsrc.eclipse.pti.tools.phpdepend.ui.views.metricrunner;

import java.io.File;
import java.net.MalformedURLException;

import org.eclipse.gmf.runtime.draw2d.ui.render.RenderedImage;
import org.eclipse.gmf.runtime.draw2d.ui.render.factory.RenderedImageFactory;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.MetricRunSession;
import org.phpsrc.eclipse.pti.ui.Logger;
import org.phpsrc.eclipse.pti.ui.widgets.ImageCanvas;
import org.phpsrc.eclipse.pti.ui.widgets.ImageCanvasViewer;

public class MetricRunnerChartsViewPart extends ViewPart {

	public static final String NAME = "org.phpsrc.eclipse.pti.tools.phpdepend.ui.views.metricrunnercharts"; //$NON-NLS-1$

	private TabFolder fImageFolder;
	private ImageCanvasViewer fSummaryPyramid;
	private ImageCanvasViewer fJDependChart;

	public void createPartControl(Composite parent) {

		Composite comp = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(comp);

		fImageFolder = new TabFolder(comp, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(fImageFolder);

		fJDependChart = new ImageCanvasViewer(fImageFolder, SWT.NONE);
		createTabItem(fImageFolder, "JDepend Chart", fJDependChart);

		fSummaryPyramid = new ImageCanvasViewer(fImageFolder, SWT.NONE);
		createTabItem(fImageFolder, "Summary Pyramid", fSummaryPyramid);
	}

	private void createTabItem(TabFolder folder, String title, Control image) {
		TabItem item = new TabItem(folder, SWT.NONE);
		item.setText(title);
		item.setControl(image);
		image.setBackground(new Color(folder.getDisplay(), 255, 255, 255));
	}

	public void setFocus() {
		fSummaryPyramid.setFocus();
	}

	public void setActiveMetricRunSession(MetricRunSession metricRunSession) {
		if (metricRunSession != null) {
			showImage(metricRunSession.getJDependChartFile(), fJDependChart.getImageCanvas());
			showImage(metricRunSession.getSummaryPyramidFile(), fSummaryPyramid.getImageCanvas());
		}
	}

	private boolean showImage(File file, ImageCanvas imageCanvas) {
		if (file != null && file.exists()) {
			try {
				RenderedImage image = RenderedImageFactory.getInstance(file.toURI().toURL());
				Image swtImage = image.getSWTImage();
				imageCanvas.setImageData(swtImage.getImageData());
				return true;
			} catch (MalformedURLException e) {
				Logger.logException(e);
			}
		}

		return false;
	}
}
