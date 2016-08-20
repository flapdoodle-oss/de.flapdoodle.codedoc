package de.flapdoodle.codedoc.common;

import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

import com.google.common.base.Optional;

@Value.Immutable
public abstract class Error {

	@Parameter
	public abstract String message();
	
	@Parameter
	public abstract Optional<Throwable> exception();
	
	public static Error with(String message) {
		return ImmutableError.of(message, Optional.<Throwable>absent());
	}
	
	public static Error with(String message, Throwable throwable) {
		return ImmutableError.of(message, Optional.of(throwable));
	}
}
