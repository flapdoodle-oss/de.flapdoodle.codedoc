/**
 * Copyright (C) 2016
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.codedoc.exceptions;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class ExceptionMappingFunctionTest {

	@Test
	public void throwRuntimeException() {
		ExceptionMappingFunction<String, Integer> function = ExceptionMappingFunction.of(new ThrowingFunction<String, Integer>() {

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
		ExceptionMappingFunction<String, Integer> function = ExceptionMappingFunction.of(new ThrowingFunction<String, Integer>() {

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
