package de.flapdoodle.codedoc.io;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

import com.google.common.base.Optional;

import de.flapdoodle.codedoc.common.Either;
import de.flapdoodle.codedoc.common.Error;
import de.flapdoodle.codedoc.exceptions.ExceptionMappingFunction;
import de.flapdoodle.codedoc.exceptions.Exceptions;
import de.flapdoodle.codedoc.exceptions.OnException;
import de.flapdoodle.codedoc.exceptions.ThrowingFunction;

public abstract class Files {
	private Files() {
		// no instance
	}
	
	public static ExceptionMappingFunction<File, String> readFile() {
		return Exceptions.call(new ThrowingFunction<File, String>() {

			@Override
			public String apply(File file) throws IOException {
				return com.google.common.io.Files.asCharSource(file, Charset.defaultCharset()).read();
			}
		});
	}
	
	public static Either<String, Error> contentOf(Path file) {
		File resolvedFile = file.toFile();
		return readFile().or(new OnException<File, Error>() {

			@Override
			public Error apply(File context, Exception exception) {
				return Error.with("could not read "+context, exception);
			}
			
		}).apply(resolvedFile);
		
//		if (resolvedFile.exists() && resolvedFile.isFile() && resolvedFile.canRead()) {
//			return Optional.of(Files.readFile().apply(resolvedFile));
//		}
//		return Optional.absent();
	}
}
