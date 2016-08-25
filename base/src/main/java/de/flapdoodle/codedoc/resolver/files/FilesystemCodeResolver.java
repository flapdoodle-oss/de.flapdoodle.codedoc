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
package de.flapdoodle.codedoc.resolver.files;

import java.io.File;
import java.nio.file.Path;

import com.google.common.base.Function;
import com.google.common.base.Optional;

import de.flapdoodle.codedoc.CodeResolver;
import de.flapdoodle.codedoc.CodeSample;
import de.flapdoodle.codedoc.ResourceLocator;
import de.flapdoodle.codedoc.common.Either;
import de.flapdoodle.codedoc.common.Error;
import de.flapdoodle.codedoc.io.Files;

public class FilesystemCodeResolver implements CodeResolver {

	private FileExtensionCodeTypeMapping codeTypeMapping;

	public FilesystemCodeResolver(FileExtensionCodeTypeMapping codeTypeMapping) {
		this.codeTypeMapping = codeTypeMapping;
	}
	
	@Override
	public Either<CodeSample, Error> resolve(Path currentDirectory, ResourceLocator resourceLocator) {
		final Path resolvedLocation = currentDirectory.resolve(resourceLocator.value());
		return Files.contentOf(resolvedLocation)
				.mapLeft(new Function<String, CodeSample>() {

					@Override
					public CodeSample apply(String code) {
						return CodeSample.of(codeType(resolvedLocation.getFileName().toString()), code);
					}
				});
	}

	private String codeType(String fileName) {
		String codeType = "?";
		int idx=fileName.lastIndexOf('.');
		if (idx!=-1) {
			codeType=codeTypeMapping.codeTypeOf(fileName.substring(idx+1));
		}
		return codeType;
	}


}
