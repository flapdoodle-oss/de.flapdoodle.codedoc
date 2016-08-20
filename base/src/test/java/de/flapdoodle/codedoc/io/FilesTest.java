package de.flapdoodle.codedoc.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.Test;

import com.google.common.io.Resources;

public class FilesTest {

	@Test
	public void readExistingFileMustGiveContent() throws URISyntaxException {
		File resourceAsFile = Paths.get(Resources.getResource(getClass(), "sample.txt").toURI()).toFile();
		String content = Files.readFile().apply(resourceAsFile);
		assertEquals("dummy text file\n", content);
	}
	
	@Test(expected=RuntimeException.class)
	public void throwRuntimeExceptionIfMissing() {
		Files.readFile().apply(new File("shouldNotExist.blob"));
	}

}
