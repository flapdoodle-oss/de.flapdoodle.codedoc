package de.flapdoodle.codedoc.common;

import com.google.common.base.Optional;

public abstract class Optionals {

	private Optionals() {
		// no instance
	}
	
	public static <T> Optional<T> flatmap(Optional<Optional<T>> src) {
		return src.isPresent() ? src.get() : Optional.<T>absent();
	}
}
