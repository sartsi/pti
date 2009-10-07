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
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

}
