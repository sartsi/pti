/**
 *
 */
package org.phpsrc.eclipse.pti.tests.mocks;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.IOpenable;
import org.eclipse.dltk.core.IScriptModel;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

/**
 * @author mario
 *
 */
public class PHPModelElementMock implements IModelElement {

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IModelElement#accept(org.eclipse.dltk.core.IModelElementVisitor)
	 */
	@Override
	public void accept(IModelElementVisitor arg0) throws ModelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IModelElement#exists()
	 */
	@Override
	public boolean exists() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IModelElement#getAncestor(int)
	 */
	@Override
	public IModelElement getAncestor(int arg0) {
		if (IModelElement.SOURCE_MODULE == arg0)
			return new PHPSourceModuleMock();
		else return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IModelElement#getCorrespondingResource()
	 */
	@Override
	public IResource getCorrespondingResource() throws ModelException {
		// TODO Auto-generated method stub
		return new PHPFileMock();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IModelElement#getElementName()
	 */
	@Override
	public String getElementName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IModelElement#getElementType()
	 */
	@Override
	public int getElementType() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IModelElement#getHandleIdentifier()
	 */
	@Override
	public String getHandleIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IModelElement#getModel()
	 */
	@Override
	public IScriptModel getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IModelElement#getOpenable()
	 */
	@Override
	public IOpenable getOpenable() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IModelElement#getParent()
	 */
	@Override
	public IModelElement getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IModelElement#getPath()
	 */
	@Override
	public IPath getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IModelElement#getPrimaryElement()
	 */
	@Override
	public IModelElement getPrimaryElement() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IModelElement#getResource()
	 */
	@Override
	public IResource getResource() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IModelElement#getScriptProject()
	 */
	@Override
	public IScriptProject getScriptProject() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IModelElement#getUnderlyingResource()
	 */
	@Override
	public IResource getUnderlyingResource() throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IModelElement#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IModelElement#isStructureKnown()
	 */
	@Override
	public boolean isStructureKnown() throws ModelException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

}
