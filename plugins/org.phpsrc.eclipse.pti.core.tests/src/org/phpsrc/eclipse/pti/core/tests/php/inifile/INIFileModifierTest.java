package org.phpsrc.eclipse.pti.core.tests.php.inifile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;
import org.phpsrc.eclipse.pti.core.php.inifile.INIFileModifier;

public class INIFileModifierTest {
	@Test
	public void testWrongSectionIncludePath() throws IOException,
			URISyntaxException {

		URL iniFile = getClass().getResource("wrong-section-php.ini");
		assertNotNull(iniFile);

		INIFileModifier modifier = new INIFileModifier(iniFile.getFile());
		String oldIncludePath = modifier.getEntry("PHP", "include_path");
		assertEquals(
				".:/Applications/XAMPP/xamppfiles/lib/php:/Applications/XAMPP/xamppfiles/lib/php/pear",
				oldIncludePath);

		String newIncludePath = oldIncludePath + ";" + "/test/path";
		modifier.addEntry("PHP", "include_path", newIncludePath, true, null);

		assertEquals(newIncludePath, modifier.getEntry("PHP", "include_path"));
	}

	@Test
	public void testDoubleIncludePath() throws IOException, URISyntaxException {

		URL iniFile = getClass().getResource("double-include_path-php.ini");
		assertNotNull(iniFile);

		INIFileModifier modifier = new INIFileModifier(iniFile.getFile());
		String oldIncludePath = modifier.getEntry("PHP", "include_path");
		assertEquals(
				"c:\\Programme\\PHP\\PEAR;c:\\Programme\\PHP\\PHPLIB\\FluentDOM;.",
				oldIncludePath);

		String newIncludePath = oldIncludePath + ";" + "/test/path";
		modifier.addEntry("PHP", "include_path", newIncludePath, true, null);

		assertEquals(newIncludePath, modifier.getEntry("PHP", "include_path"));
	}
}
