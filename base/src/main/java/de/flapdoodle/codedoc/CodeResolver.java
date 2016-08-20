package de.flapdoodle.codedoc;

import java.nio.file.Path;

import com.google.common.base.Optional;

public interface CodeResolver {
	Optional<CodeSample> resolve(Path currentDirectory, ResourceLocator resourceLocator);
}
