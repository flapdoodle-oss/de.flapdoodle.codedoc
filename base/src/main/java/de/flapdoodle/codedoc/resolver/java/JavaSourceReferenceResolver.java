package de.flapdoodle.codedoc.resolver.java;

import de.flapdoodle.codedoc.CodeSample;
import de.flapdoodle.codedoc.common.Either;
import de.flapdoodle.codedoc.common.Error;

public interface JavaSourceReferenceResolver {

	Either<CodeSample, Error> resolve(Reference ref, String code);
	
}
