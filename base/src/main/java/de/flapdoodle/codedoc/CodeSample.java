package de.flapdoodle.codedoc;

import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

@Value.Immutable
public abstract class CodeSample {
	@Parameter
	public abstract String type();
	@Parameter
	public abstract String code();

	public static CodeSample of(String type, String code) {
		return ImmutableCodeSample.of(type, code);
	}
}
