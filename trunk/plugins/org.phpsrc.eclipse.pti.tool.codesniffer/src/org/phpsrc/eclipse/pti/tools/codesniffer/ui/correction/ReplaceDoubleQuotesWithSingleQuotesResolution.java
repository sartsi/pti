/*******************************************************************************
 * Copyright (c) 2009, Sven Kiera
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

package org.phpsrc.eclipse.pti.tools.codesniffer.ui.correction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.graphics.Image;
import org.phpsrc.eclipse.pti.ui.Logger;

public class ReplaceDoubleQuotesWithSingleQuotesResolution extends AbstractResolution {

	@Override
	public String getDescription() {
		return "Replace double quotes with single quotes.";
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public String getLabel() {
		return "Replace double quotes with single quotes";
	}

	@Override
	public void run(IMarker marker) {
		try {
			String msg = (String) marker.getAttribute(IMarker.MESSAGE);
			Integer lineNumber = (Integer) marker.getAttribute(IMarker.LINE_NUMBER);

			if (lineNumber != null && lineNumber.intValue() > 0) {
				// String "..." does not require double quotes; use single
				// quotes instead
				Pattern p = Pattern.compile(
						"String \"(.*)\" does not require double quotes; use single quotes instead", Pattern.MULTILINE
								| Pattern.DOTALL);
				Matcher m = p.matcher(msg);
				if (m.matches()) {
					String found = prepareString(m.group(1));

					IDocument doc = this.getDocument(marker);
					IRegion line = doc.getLineInformation(lineNumber.intValue() - 1);

					FindReplaceDocumentAdapter findReplace = new FindReplaceDocumentAdapter(doc);

					// codesniffer converts tabs to spaces so check for any
					// whitespace
					String search = "\\Q" + found.replaceAll("[\\s]+", "\\\\E[\\\\s]+\\\\Q") + "\\E";
					IRegion region = findReplace
							.find(line.getOffset(), "\"(" + search + ")\"", true, true, false, true);
					if (region != null) {
						findReplace.replace("'$1'", true);
						marker.delete();
					}
				}
			}
		} catch (CoreException e) {
			Logger.logException(e);
		} catch (BadLocationException e) {
			Logger.logException(e);
		}
	}

}
