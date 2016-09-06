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
package de.flapdoodle.codedoc.resolver.java;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;

import de.flapdoodle.codedoc.CodeResolver;
import de.flapdoodle.codedoc.CodeSample;
import de.flapdoodle.codedoc.ResourceLocator;
import de.flapdoodle.codedoc.common.Either;
import de.flapdoodle.codedoc.common.Error;
import de.flapdoodle.codedoc.common.Optionals;

public class JavaReferencenCodeResolver implements CodeResolver {

	private JavaSourceCodeResolver sourceCodeResolver;
	private JavaSourceReferenceResolver referenceResolver;

	public JavaReferencenCodeResolver(JavaSourceCodeResolver sourceCodeResolver, JavaSourceReferenceResolver referenceResolver) {
		this.sourceCodeResolver = sourceCodeResolver;
		this.referenceResolver = referenceResolver;
	}
	
	@Override
	public Either<CodeSample, Error> resolve(Path currentDirectory, final ResourceLocator resourceLocator) {
		Optional<? extends Reference> reference = Reference.parse(resourceLocator.value());
		Either<? extends Reference, Error> refOrError = Optionals.or(reference, new Supplier<Error>() {

			@Override
			public Error get() {
				return Error.with("could not parse reference "+resourceLocator.value());
			}
		});
		return refOrError.flatmapLeft(new Function<Reference, Either<CodeSample, Error>>() {

			@Override
			public Either<CodeSample, Error> apply(Reference input) {
				return resolve(input);
			}
		});
	}

	protected Either<CodeSample, Error> resolve(Reference ref) {
		return codeOf(ref, sourceCodeResolver.resolve(asPath(ref)));
	}

	private Either<CodeSample, Error> codeOf(final Reference ref, Either<String, Error> resolved) {
		return resolved.flatmapLeft(new Function<String, Either<CodeSample,Error>>() {

			@Override
			public Either<CodeSample, Error> apply(String input) {
				return referenceResolver.resolve(ref, input);
			}
		}); 
	}

	protected static Path asPath(Reference ref) {
		String[] packageAndClassname = (ref.packageName()+ref.parts().get(0).className()).split("\\.");
		return packageAndClassname.length==1 ? Paths.get(packageAndClassname[0]) : Paths.get(packageAndClassname[0], Arrays.copyOfRange(packageAndClassname, 1,packageAndClassname.length));
	}

}
