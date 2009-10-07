package org.phpsrc.eclipse.pti.tools.codesniffer.ui.correction;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.graphics.Image;

public class RemoveWhitespaceAtEndOfLineResolution extends AbstractResolution {

	@Override
	public String getDescription() {
		return "Remove whitespaces at end of line.";
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public String getLabel() {
		return "Remove whitespaces at end of line";
	}

	@Override
	public void run(IMarker marker) {
		try {
			String msg = (String) marker.getAttribute(IMarker.MESSAGE);
			Integer lineNumber = (Integer) marker.getAttribute(IMarker.LINE_NUMBER);

			if (lineNumber != null && lineNumber.intValue() > 0) {
				IDocument doc = this.getDocument(marker);
				IRegion line = doc.getLineInformation(lineNumber.intValue() - 1);

				int endOfLine = line.getOffset() + line.getLength() - 1;
				int whiteSpaceCount = 0;

				while (doc.getChar(endOfLine - whiteSpaceCount) == ' '
						|| doc.getChar(endOfLine - whiteSpaceCount) == '\t') {
					whiteSpaceCount++;
				}

				doc.replace(endOfLine - whiteSpaceCount + 1, whiteSpaceCount, "");

				marker.delete();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
