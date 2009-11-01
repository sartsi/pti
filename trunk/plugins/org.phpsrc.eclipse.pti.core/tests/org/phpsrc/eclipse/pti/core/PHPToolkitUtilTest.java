/**
 *
 */
package org.phpsrc.eclipse.pti.core;

import static org.junit.Assert.*;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.junit.Before;
import org.junit.Test;
import org.phpsrc.eclipse.pti.tests.mocks.*;

/**
 * @author mario
 *
 */
public class PHPToolkitUtilTest extends TestCase{

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testHasPhpExtentionByString()
	{
		assertTrue(PHPToolkitUtil.hasPhpExtention("pti.php"));
		assertFalse(PHPToolkitUtil.hasPhpExtention("pti.py"));
	} // function

	@Test
	public void testHasPhpExtensionByIFile()
	{
		PHPFileMock phpFile = new PHPFileMock();
		TextFileMock txtFile = new TextFileMock();
		assertTrue(PHPToolkitUtil.hasPhpExtention(phpFile));
		assertFalse(PHPToolkitUtil.hasPhpExtention(txtFile));
	} // function

	@Test
	public void testIsPhpProject() throws CoreException
	{
		PHPProjectMock project = new PHPProjectMock();
		assertTrue(PHPToolkitUtil.isPhpProject(project));
	} // function

	@Test
	public void testCreateTempFile()
	{
		File file = PHPToolkitUtil.createTempFile("pri.tmp");
		assertTrue(file instanceof File);
		assertTrue(file.canRead());
		assertTrue(file.canWrite());

		// For security reasons
		assertFalse(file.canExecute());
	} // function

	@Test
	public void testIsPhpElementByElement()
	{
		assertTrue(PHPToolkitUtil.isPhpElement(new PHPModelElementMock()));
		assertFalse(PHPToolkitUtil.isPhpElement(new TextModelElementMock()));
	} // function

	@Test
	public void testIsPhpElementBySourceModule()
	{
		assertTrue(PHPToolkitUtil.isPhpElement(new PHPSourceModuleMock()));
		assertFalse(PHPToolkitUtil.isPhpElement(new TextSourceModuleMock()));
	} // function
}
