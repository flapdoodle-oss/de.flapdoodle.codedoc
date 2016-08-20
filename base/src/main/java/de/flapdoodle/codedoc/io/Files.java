package de.flapdoodle.codedoc.io;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import de.flapdoodle.codedoc.exceptions.ExceptionMappingFunction;
import de.flapdoodle.codedoc.exceptions.Exceptions;
import de.flapdoodle.codedoc.exceptions.ThrowingFunction;

public abstract class Files {
	private Files() {
		// no instance
	}
	
	public static ExceptionMappingFunction<File, String> readFile() {
		return Exceptions.call(new ThrowingFunction<File, String, IOException>() {

			@Override
			public String apply(File file) throws IOException {
				return com.google.common.io.Files.asCharSource(file, Charset.defaultCharset()).read();
			}
		});
	}
}
