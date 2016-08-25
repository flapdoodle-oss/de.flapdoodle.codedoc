/**
 * Copyright (C) 2013
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
package de.flapdoodle.codedoc.resolver.java;

import static org.junit.Assert.*;

import org.junit.Test;

import de.flapdoodle.codedoc.CodeSample;
import de.flapdoodle.codedoc.common.Either;
import de.flapdoodle.codedoc.common.Error;

public class JavaSourceReferenceResolverImplTest {

	private final String code="\n"
			+ "\n"
			+ "/* some comment */\n"
			+ "import foo;\n"
			+ "\n"
			+ "public class Foo {/*comment*/}\n"
			+ "\n"
			+ "\n";

	@Test
	public void resolveClassWithDefaultScope() {
		String match="import foo;\n"
				+ "\n"
				+ "public class Foo {/*comment*/}\n"
				+ "\n"
				+ "\n";
		
		Reference ref=Reference.parse("foo.Foo").get();
		Either<CodeSample, Error> result = new JavaSourceReferenceResolverImpl().resolve(ref, code);
		
		assertTrue(result.isLeft());
		assertEquals(match, result.left().code());
		assertEquals("java", result.left().type());
	}

	@Test
	public void resolveClassWithScopeBody() {
		String match="/*comment*/";
		
		Reference ref=Reference.parse("foo.Foo body").get();
		Either<CodeSample, Error> result = new JavaSourceReferenceResolverImpl().resolve(ref, code);
		
		assertTrue(result.isLeft());
		assertEquals(match, result.left().code());
		assertEquals("java", result.left().type());
	}
	
	@Test
	public void resolveClassWithScopeAll() {
		String match=code;
		
		Reference ref=Reference.parse("foo.Foo all").get();
		Either<CodeSample, Error> result = new JavaSourceReferenceResolverImpl().resolve(ref, code);
		
		assertTrue(result.isLeft());
		assertEquals(match, result.left().code());
		assertEquals("java", result.left().type());
	}
}
