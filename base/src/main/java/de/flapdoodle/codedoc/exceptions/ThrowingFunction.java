package de.flapdoodle.codedoc.exceptions;

public interface ThrowingFunction<F,T,E extends Exception> {

	T apply(F input) throws E;
}
