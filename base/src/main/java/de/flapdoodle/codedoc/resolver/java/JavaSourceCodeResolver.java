package de.flapdoodle.codedoc.resolver.java;

import java.nio.file.Path;

import de.flapdoodle.codedoc.common.Either;
import de.flapdoodle.codedoc.common.Error;

public interface JavaSourceCodeResolver {
	public Either<String, Error> resolve(Path classAsPath);
}
