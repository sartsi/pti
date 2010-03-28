/*******************************************************************************
 * Copyright (c) 2010, Sven Kiera
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * - Neither the name of the Organisation nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.core.model;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.phpsrc.eclipse.pti.tools.phpdepend.PHPDependPlugin;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.Metric;

public class MetricRunSession extends MetricElement {

	private final static Image IMAGE = PHPDependPlugin.getDefault().getImageRegistry().get(
			PHPDependPlugin.IMG_PHP_DEPEND);

	volatile boolean fIsRunning;
	private final IResource fDependentResource;
	private final Image fImage;

	public MetricRunSession() {
		this(null);
	}

	public MetricRunSession(IResource dependentResource) {
		super(null, "PHP Depend", new MetricResult[0]);
		fDependentResource = dependentResource;
		if (dependentResource != null) {
			name = dependentResource.getFullPath().toPortableString();
			if (dependentResource instanceof IProject) {
				fImage = PlatformUI.getWorkbench().getSharedImages().getImage(
						org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJ_PROJECT);
			} else if (dependentResource instanceof IFolder) {
				fImage = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
			} else {
				fImage = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
			}
		} else {
			fImage = IMAGE;
		}
	}

	public Image getImage() {
		return fImage;
	}

	public IResource getResource() {
		return null;
	}

	public IMarker getFileMarker() {
		return null;
	}

	protected void addChild(IMetricElement child) {
		Assert.isNotNull(child);
		if (child instanceof MetricSummary && !hasChildren()) {
			super.addChild(child);
		}
	}

	/**
	 * @return <code>true</code> if this session has been started, but not ended
	 *         nor stopped nor terminated
	 */
	public boolean isRunning() {
		return fIsRunning;
	}

	public MetricSummary getSummaryRoot() {
		return (MetricSummary) getFirstChild();
	}

	public Date getGenerated() {
		MetricSummary summary = getSummaryRoot();
		if (summary != null)
			return summary.getGenerated();
		else
			return null;
	}

	public void swapIn() {
		if (getSummaryRoot() != null)
			return;

		// try {
		// PHPDependModel.importIntoTestRunSession(getSwapFile(), this);
		// } catch (IllegalStateException e) {
		// Logger.logException(e);
		// } catch (CoreException e) {
		// Logger.logException(e);
		// }
	}

	public void swapOut() {
		// if (getSummaryRoot() == null)
		// return;
		//
		// try {
		// File swapFile = getSwapFile();
		//
		// PHPDependModel.exportMetricRunSession(this, swapFile);
		// reset();
		// } catch (IllegalStateException e) {
		// Logger.logException(e);
		// } catch (CoreException e) {
		// Logger.logException(e);
		// }
	}

	public void removeSwapFile() {
		File swapFile = getSwapFile();
		if (swapFile.exists())
			swapFile.delete();
	}

	private File getSwapFile() throws IllegalStateException {
		File historyDir = PHPDependPlugin.getHistoryDirectory();
		String isoTime = new SimpleDateFormat("yyyyMMdd-HHmmss.SSS").format(getGenerated()); //$NON-NLS-1$
		String swapFileName = isoTime + ".xml"; //$NON-NLS-1$
		return new File(historyDir, swapFileName);
	}

	public void reset() {
		metrics = new Metric[0];
		members.clear();
		results = new MetricResult[0];
		warnings = false;
		errors = false;
	}
}
