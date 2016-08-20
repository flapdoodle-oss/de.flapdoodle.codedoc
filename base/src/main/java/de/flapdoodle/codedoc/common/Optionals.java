package de.flapdoodle.codedoc.common;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;

public abstract class Optionals {

	private Optionals() {
		// no instance
	}
	
	public static <T> Optional<T> flatmap(Optional<Optional<T>> src) {
		return src.isPresent() ? src.get() : Optional.<T>absent();
	}
	
	public static <L,R> Either<L, R> or(Optional<L> left, Supplier<R> right) {
		return left.isPresent() ? Either.<L,R>left(left.get()) : Either.<L,R>right(right.get());
	}
}
