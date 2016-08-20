package de.flapdoodle.codedoc.exceptions;

import com.google.common.base.Function;

public class ExceptionMappingFunction<F,T> implements Function<F, T> {

	public static final class OnExceptionThrowRuntimeException<F, T> implements OnException<F, T> {
		@Override
		public T apply(F context, Exception exception) {
			throw new RuntimeException("with "+context,exception);
		}
	}

	private ThrowingFunction<F, T, ? extends Exception> throwingFunction;
	private OnException<F, T> onException;

	private <E extends Exception> ExceptionMappingFunction(ThrowingFunction<F, T, E> throwingFunction, OnException<F, T> onException) {
		this.throwingFunction = throwingFunction;
		this.onException = onException;
	}
	
	@Override
	public T apply(F input) {
		try {
			return throwingFunction.apply(input);
		} catch (Exception e) {
			return onException.apply(input, e);
		}
	}
	
	public ExceptionMappingFunction<F, T> onException(OnException<F, T> onException) {
		return of(this.throwingFunction, onException);
	}

	public static <F,T,E extends Exception> ExceptionMappingFunction<F, T> of(ThrowingFunction<F, T, E> call, OnException<F, T> onException) {
		return new ExceptionMappingFunction<>(call, onException);
	}
	
	public static <F,T,E extends Exception> ExceptionMappingFunction<F, T> of(ThrowingFunction<F, T, E> call) {
		return new ExceptionMappingFunction<>(call, new OnExceptionThrowRuntimeException<F, T>());
	}
}
