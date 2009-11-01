/**
 *
 */
package org.phpsrc.eclipse.pti.tests.mocks;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;

/**
 * @author mario
 *
 */
public class TextFileContentDescriptionMock implements IContentDescription {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentDescription#getCharset()
	 */
	@Override
	public String getCharset() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentDescription#getContentType()
	 */
	@Override
	public IContentType getContentType() {
		return new TextFileContentTypeMock();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentDescription#getProperty(org.eclipse.core.runtime.QualifiedName)
	 */
	@Override
	public Object getProperty(QualifiedName key) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentDescription#isRequested(org.eclipse.core.runtime.QualifiedName)
	 */
	@Override
	public boolean isRequested(QualifiedName key) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentDescription#setProperty(org.eclipse.core.runtime.QualifiedName, java.lang.Object)
	 */
	@Override
	public void setProperty(QualifiedName key, Object value) {
		// TODO Auto-generated method stub

	}

}
