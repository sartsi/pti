/**
 *
 */
package org.phpsrc.eclipse.pti.tests.mocks;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeSettings;
import org.eclipse.core.runtime.preferences.IScopeContext;

/**
 * @author mario
 *
 */
public class PHPFileContentTypeMock implements IContentType {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentType#getBaseType()
	 */
	@Override
	public IContentType getBaseType() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentType#getDefaultCharset()
	 */
	@Override
	public String getDefaultCharset() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentType#getDefaultDescription()
	 */
	@Override
	public IContentDescription getDefaultDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentType#getDescriptionFor(java.io.InputStream, org.eclipse.core.runtime.QualifiedName[])
	 */
	@Override
	public IContentDescription getDescriptionFor(InputStream contents,
			QualifiedName[] options) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentType#getDescriptionFor(java.io.Reader, org.eclipse.core.runtime.QualifiedName[])
	 */
	@Override
	public IContentDescription getDescriptionFor(Reader contents,
			QualifiedName[] options) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentType#getFileSpecs(int)
	 */
	@Override
	public String[] getFileSpecs(int type) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentType#getId()
	 */
	@Override
	public String getId() {
		return "org.eclipse.php.core.phpsource";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentType#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentType#getSettings(org.eclipse.core.runtime.preferences.IScopeContext)
	 */
	@Override
	public IContentTypeSettings getSettings(IScopeContext context)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentType#isAssociatedWith(java.lang.String)
	 */
	@Override
	public boolean isAssociatedWith(String fileName) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentType#isAssociatedWith(java.lang.String, org.eclipse.core.runtime.preferences.IScopeContext)
	 */
	@Override
	public boolean isAssociatedWith(String fileName, IScopeContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentType#isKindOf(org.eclipse.core.runtime.content.IContentType)
	 */
	@Override
	public boolean isKindOf(IContentType another) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentTypeSettings#addFileSpec(java.lang.String, int)
	 */
	@Override
	public void addFileSpec(String fileSpec, int type) throws CoreException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentTypeSettings#removeFileSpec(java.lang.String, int)
	 */
	@Override
	public void removeFileSpec(String fileSpec, int type) throws CoreException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.content.IContentTypeSettings#setDefaultCharset(java.lang.String)
	 */
	@Override
	public void setDefaultCharset(String userCharset) throws CoreException {
		// TODO Auto-generated method stub

	}

}
