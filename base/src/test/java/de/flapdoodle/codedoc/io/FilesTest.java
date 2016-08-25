/**
 * Copyright (C) 2016
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
