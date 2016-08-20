package de.flapdoodle.codedoc.resolver.java;

import java.nio.file.Path;

import com.google.common.base.Optional;

public interface JavaSourceCodeResolver {
	public Optional<String> resolve(Path classAsPath);
}
