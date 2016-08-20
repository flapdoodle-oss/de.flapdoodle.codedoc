package de.flapdoodle.codedoc.exceptions;

public interface OnException<F, T> {
	T apply(F context, Exception exception);
}
