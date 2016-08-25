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
