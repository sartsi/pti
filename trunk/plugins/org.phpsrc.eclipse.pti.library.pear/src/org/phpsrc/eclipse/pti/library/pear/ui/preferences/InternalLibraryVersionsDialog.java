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
package org.phpsrc.eclipse.pti.library.pear.ui.preferences;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.phpsrc.eclipse.pti.library.pear.PHPLibraryPEARPlugin;
import org.phpsrc.eclipse.pti.ui.Logger;

/**
 * Dialog to enter a na new task tag
 */
public class InternalLibraryVersionsDialog extends MessageDialog {

	private static final Pattern CHANNEL_PATTERN = Pattern.compile("INSTALLED PACKAGES, CHANNEL (.+):$"); //$NON-NLS-1$
	private static final Pattern PACKAGE_PATTERN = Pattern.compile("(.+) +([0-9.]+) +([a-z]+)$"); //$NON-NLS-1$

	private Tree fTree;

	public InternalLibraryVersionsDialog(Shell parent) {
		super(parent, "PEAR Versions", null, "", 0, new String[] { "OK" }, 0);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		fTree = new Tree(parent, SWT.BORDER | SWT.FULL_SELECTION);
		fTree.setLinesVisible(true);
		fTree.setHeaderVisible(true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 400;
		data.widthHint = 400;
		fTree.setLayoutData(data);

		String[] titles = { "Package", "Version", "State" };

		for (int i = 0; i < titles.length; i++) {
			TreeColumn column = new TreeColumn(fTree, SWT.NONE);
			column.setText(titles[i]);
		}

		IPath versionFile = PHPLibraryPEARPlugin.getDefault().resolvePluginResource("/php/library/PEAR/versions.txt");
		if (versionFile != null) {
			BufferedReader r;
			try {
				r = new BufferedReader(new FileReader(versionFile.toPortableString()));

				TreeItem channel = null;
				String lastChannelName = null;

				String line;
				while ((line = r.readLine()) != null) {
					Matcher m = CHANNEL_PATTERN.matcher(line);
					if (m.matches()) {
						lastChannelName = m.group(1).trim();
					} else {
						m = PACKAGE_PATTERN.matcher(line);
						if (m.matches()) {
							if (lastChannelName != null) {
								channel = new TreeItem(fTree, SWT.NONE);
								channel.setText(lastChannelName);

								Font font = channel.getFont();
								FontData fd = font.getFontData()[0];
								fd.setStyle(SWT.BOLD);
								channel.setFont(new Font(font.getDevice(), fd));

								lastChannelName = null;
							}

							if (channel != null) {
								TreeItem item = new TreeItem(channel, SWT.NONE);
								item.setText(0, m.group(1).trim());
								item.setText(1, m.group(2).trim());
								item.setText(2, m.group(3).trim());
								fTree.showItem(item);
							}
						}
					}
				}

				int width = data.widthHint;
				for (int i = 1; i < titles.length; i++) {
					fTree.getColumn(i).pack();
					width -= fTree.getColumn(i).getWidth();
				}
				fTree.getColumn(0).setWidth(width);

			} catch (FileNotFoundException e) {
				Logger.logException(e);
			} catch (IOException e) {
				Logger.logException(e);
			}
		}

		return fTree;
	}

	/*
	 * @see org.eclipse.jface.window.Window#configureShell(Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// TODO - Add the Help contex id
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell,
		// IPHPHelpContextIds.TODO_TASK_INPUT_DIALOG);
	}
}
