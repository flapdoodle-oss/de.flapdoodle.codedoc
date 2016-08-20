package de.flapdoodle.codedoc;

import java.nio.file.Path;

import de.flapdoodle.codedoc.common.Either;
import de.flapdoodle.codedoc.common.Error;

public interface CodeResolver {
	Either<CodeSample, Error> resolve(Path currentDirectory, ResourceLocator resourceLocator);
}
