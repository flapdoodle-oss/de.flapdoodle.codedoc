package de.flapdoodle.codedoc.exceptions;

public interface ThrowingFunction<F,T> {

	T apply(F input) throws Exception;
}
