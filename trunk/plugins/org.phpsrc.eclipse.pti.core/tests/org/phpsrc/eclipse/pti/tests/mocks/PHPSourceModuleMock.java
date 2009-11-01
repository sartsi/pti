/**
 *
 */
package org.phpsrc.eclipse.pti.tests.mocks;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.IBuffer;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.IOpenable;
import org.eclipse.dltk.core.IPackageDeclaration;
import org.eclipse.dltk.core.IProblemRequestor;
import org.eclipse.dltk.core.IScriptModel;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.WorkingCopyOwner;

/**
 * @author mario
 *
 */
public class PHPSourceModuleMock implements ISourceModule {

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#becomeWorkingCopy(org.eclipse.dltk.core.IProblemRequestor, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void becomeWorkingCopy(IProblemRequestor arg0, IProgressMonitor arg1)
			throws ModelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#commitWorkingCopy(boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void commitWorkingCopy(boolean arg0, IProgressMonitor arg1)
			throws ModelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#discardWorkingCopy()
	 */
	@Override
	public void discardWorkingCopy() throws ModelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#getAllTypes()
	 */
	@Override
	public IType[] getAllTypes() throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#getElementAt(int)
	 */
	@Override
	public IModelElement getElementAt(int arg0) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#getField(java.lang.String)
	 */
	@Override
	public IField getField(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#getFields()
	 */
	@Override
	public IField[] getFields() throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#getMethod(java.lang.String)
	 */
	@Override
	public IMethod getMethod(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#getOwner()
	 */
	@Override
	public WorkingCopyOwner getOwner() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#getPackageDeclaration(java.lang.String)
	 */
	@Override
	public IPackageDeclaration getPackageDeclaration(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#getPackageDeclarations()
	 */
	@Override
	public IPackageDeclaration[] getPackageDeclarations() throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#getPrimary()
	 */
	@Override
	public ISourceModule getPrimary() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#getSource()
	 */
	@Override
	public String getSource() throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#getSourceAsCharArray()
	 */
	@Override
	public char[] getSourceAsCharArray() throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#getType(java.lang.String)
	 */
	@Override
	public IType getType(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#getTypes()
	 */
	@Override
	public IType[] getTypes() throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#getWorkingCopy(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public ISourceModule getWorkingCopy(IProgressMonitor arg0)
			throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#getWorkingCopy(org.eclipse.dltk.core.WorkingCopyOwner, org.eclipse.dltk.core.IProblemRequestor, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public ISourceModule getWorkingCopy(WorkingCopyOwner arg0,
			IProblemRequestor arg1, IProgressMonitor arg2)
			throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#isBinary()
	 */
	@Override
	public boolean isBinary() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#isBuiltin()
	 */
	@Override
	public boolean isBuiltin() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#isPrimary()
	 */
	@Override
	public boolean isPrimary() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#isWorkingCopy()
	 */
	@Override
	public boolean isWorkingCopy() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceModule#reconcile(boolean, org.eclipse.dltk.core.WorkingCopyOwner, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void reconcile(boolean arg0, WorkingCopyOwner arg1,
			IProgressMonitor arg2) throws ModelException {
		// TODO Auto-generated method stub

	}

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
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IModelElement#getAncestor(int)
	 */
	@Override
	public IModelElement getAncestor(int arg0) {
		// TODO Auto-generated method stub
		return null;
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

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IParent#getChildren()
	 */
	@Override
	public IModelElement[] getChildren() throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IParent#hasChildren()
	 */
	@Override
	public boolean hasChildren() throws ModelException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IOpenable#close()
	 */
	@Override
	public void close() throws ModelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IOpenable#getBuffer()
	 */
	@Override
	public IBuffer getBuffer() throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IOpenable#hasUnsavedChanges()
	 */
	@Override
	public boolean hasUnsavedChanges() throws ModelException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IOpenable#isConsistent()
	 */
	@Override
	public boolean isConsistent() throws ModelException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IOpenable#isOpen()
	 */
	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IOpenable#makeConsistent(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void makeConsistent(IProgressMonitor arg0) throws ModelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IOpenable#open(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void open(IProgressMonitor arg0) throws ModelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.IOpenable#save(org.eclipse.core.runtime.IProgressMonitor, boolean)
	 */
	@Override
	public void save(IProgressMonitor arg0, boolean arg1) throws ModelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceReference#getSourceRange()
	 */
	@Override
	public ISourceRange getSourceRange() throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceManipulation#copy(org.eclipse.dltk.core.IModelElement, org.eclipse.dltk.core.IModelElement, java.lang.String, boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void copy(IModelElement arg0, IModelElement arg1, String arg2,
			boolean arg3, IProgressMonitor arg4) throws ModelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceManipulation#delete(boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void delete(boolean arg0, IProgressMonitor arg1)
			throws ModelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceManipulation#move(org.eclipse.dltk.core.IModelElement, org.eclipse.dltk.core.IModelElement, java.lang.String, boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void move(IModelElement arg0, IModelElement arg1, String arg2,
			boolean arg3, IProgressMonitor arg4) throws ModelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ISourceManipulation#rename(java.lang.String, boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void rename(String arg0, boolean arg1, IProgressMonitor arg2)
			throws ModelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ICodeAssist#codeComplete(int, org.eclipse.dltk.core.CompletionRequestor)
	 */
	@Override
	public void codeComplete(int arg0, CompletionRequestor arg1)
			throws ModelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ICodeAssist#codeComplete(int, org.eclipse.dltk.core.CompletionRequestor, long)
	 */
	@Override
	public void codeComplete(int arg0, CompletionRequestor arg1, long arg2)
			throws ModelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ICodeAssist#codeComplete(int, org.eclipse.dltk.core.CompletionRequestor, org.eclipse.dltk.core.WorkingCopyOwner)
	 */
	@Override
	public void codeComplete(int arg0, CompletionRequestor arg1,
			WorkingCopyOwner arg2) throws ModelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ICodeAssist#codeComplete(int, org.eclipse.dltk.core.CompletionRequestor, org.eclipse.dltk.core.WorkingCopyOwner, long)
	 */
	@Override
	public void codeComplete(int arg0, CompletionRequestor arg1,
			WorkingCopyOwner arg2, long arg3) throws ModelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ICodeAssist#codeSelect(int, int)
	 */
	@Override
	public IModelElement[] codeSelect(int arg0, int arg1) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.core.ICodeAssist#codeSelect(int, int, org.eclipse.dltk.core.WorkingCopyOwner)
	 */
	@Override
	public IModelElement[] codeSelect(int arg0, int arg1, WorkingCopyOwner arg2)
			throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

}
