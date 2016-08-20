package de.flapdoodle.codedoc.exceptions;

public abstract class Exceptions {

	private Exceptions() {
		// no instance
	}
	
	public static <F, T> ExceptionMappingFunction<F, T> call(ThrowingFunction<F, T> call) {
		return ExceptionMappingFunction.of(call);
	}
	
	public static <F, T> ExceptionMappingFunction<F, T> call(ThrowingFunction<F, T> call, OnException<F, T> onException) {
		return ExceptionMappingFunction.of(call,onException);
	}
}
