package org.phpsrc.eclipse.pti.tools.codesniffer.ui.correction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.graphics.Image;

public class AddingMissingCommentResolution extends AbstractResolution {

	@Override
	public String getDescription() {
		return "Add expected comment";
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public String getLabel() {
		return "Add expected comment";
	}

	@Override
	public void run(IMarker marker) {
		try {
			String msg = (String) marker.getAttribute(IMarker.MESSAGE);
			Integer lineNumber = (Integer) marker.getAttribute(IMarker.LINE_NUMBER);

			if (lineNumber != null && lineNumber.intValue() > 0) {
				// Expected //end class
				Pattern p = Pattern.compile("Expected //(.*)");
				Matcher m = p.matcher(msg);
				if (m.matches()) {
					String expected = prepareString(m.group(1));

					IDocument doc = this.getDocument(marker);
					IRegion line = doc.getLineInformation(lineNumber.intValue() - 1);

					// replace existing comments
					int startExistingComment = line.getLength();
					for (int i = 0; i < line.getLength(); i++) {
						if (doc.getChar(line.getOffset() + i) == '/' && doc.getChar(line.getOffset() + i + 1) == '/') {
							startExistingComment = i;
							break;
						}
					}

					doc.replace(line.getOffset() + startExistingComment, line.getLength() - startExistingComment, "//"
							+ expected);
				}
			}

		} catch (CoreException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
