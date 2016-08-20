package de.flapdoodle.codedoc.exceptions;

import com.google.common.base.Function;

import de.flapdoodle.codedoc.common.Either;

public class ExceptionMappingFunction<F,T> implements Function<F, T> {

	public static final class OnExceptionThrowRuntimeException<F, T> implements OnException<F, T> {
		@Override
		public T apply(F context, Exception exception) {
			throw new RuntimeException("with "+context,exception);
		}
	}

	private ThrowingFunction<F, T> throwingFunction;
	private OnException<F, T> onException;

	private ExceptionMappingFunction(ThrowingFunction<F, T> throwingFunction, OnException<F, T> onException) {
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
	
	public <X> ExceptionMappingFunction<F, Either<T, X>> or(final OnException<F, X> onException) {
		final ThrowingFunction<F, T> org = this.throwingFunction;
		
		return of(new ThrowingFunction<F, Either<T,X>>() {

			@Override
			public Either<T, X> apply(F input) throws Exception {
				return Either.left(org.apply(input));
			}
		}, new OnException<F, Either<T,X>>() {
			@Override
			public Either<T, X> apply(F context, Exception exception) {
				return Either.right(onException.apply(context, exception));
			}
		});
	}

	public static <F,T> ExceptionMappingFunction<F, T> of(ThrowingFunction<F, T> call, OnException<F, T> onException) {
		return new ExceptionMappingFunction<>(call, onException);
	}
	
	public static <F,T> ExceptionMappingFunction<F, T> of(ThrowingFunction<F, T> call) {
		return new ExceptionMappingFunction<>(call, new OnExceptionThrowRuntimeException<F, T>());
	}
}
