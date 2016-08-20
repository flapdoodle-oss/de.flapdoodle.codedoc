package de.flapdoodle.codedoc.exceptions;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class ExceptionMappingFunctionTest {

	@Test
	public void throwRuntimeException() {
		ExceptionMappingFunction<String, Integer> function = ExceptionMappingFunction.of(new ThrowingFunction<String, Integer, IOException>() {

			@Override
			public Integer apply(String input) throws IOException {
				throw new IOException("foo");
			}
		});
		try {
			function.apply("dummy value");
			fail("should not reach this");
		} catch (RuntimeException rx) {
			assertEquals("with dummy value",rx.getMessage());
		}
	}
	
	@Test
	public void catchAndMapResult() {
		ExceptionMappingFunction<String, Integer> function = ExceptionMappingFunction.of(new ThrowingFunction<String, Integer, IllegalArgumentException>() {

			@Override
			public Integer apply(String input) throws IllegalArgumentException {
				return Integer.valueOf(input);
			}
		});
		
		ExceptionMappingFunction<String, Integer> toTest = function.onException(new OnException<String, Integer>() {
			
			@Override
			public Integer apply(String context, Exception exception) {
				return 42;
			}
		});
		
		assertEquals(12,toTest.apply("12").intValue());
		assertEquals(42,toTest.apply("a12").intValue());
	}
}
