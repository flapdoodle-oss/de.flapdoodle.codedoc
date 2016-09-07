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
package de.flapdoodle.codedoc.resolver.java;

import static org.junit.Assert.*;

import org.junit.Test;

import de.flapdoodle.codedoc.CodeSample;
import de.flapdoodle.codedoc.common.Either;
import de.flapdoodle.codedoc.common.Error;

public class JavaSourceReferenceResolverImplTest {

	public static class SimpleTestCase {
		
		private final String simpleCode="\n"
				+ "\n"
				+ "/* some comment */\n"
				+ "import foo;\n"
				+ "\n"
				+ "public class Foo {/*comment*/}\n"
				+ "\n"
				+ "\n";
	
		@Test
		public void resolveClassWithDefaultScope() {
			String match="public class Foo {/*comment*/}";
			
			Reference ref=Reference.parse("foo.Foo").get();
			Either<CodeSample, Error> result = new JavaSourceReferenceResolverImpl().resolve(ref, simpleCode);
			
			assertTrue(result.isLeft());
			assertEquals(match, result.left().code());
			assertEquals("java", result.left().type());
		}
	
		@Test
		public void resolveClassWithScopeBody() {
			String match="/*comment*/";
			
			Reference ref=Reference.parse("foo.Foo body").get();
			Either<CodeSample, Error> result = new JavaSourceReferenceResolverImpl().resolve(ref, simpleCode);
			
			assertTrue(result.isLeft());
			assertEquals(match, result.left().code());
			assertEquals("java", result.left().type());
		}
		
		@Test
		public void resolveClassWithScopeAll() {
			String match=simpleCode;
			
			Reference ref=Reference.parse("foo.Foo all").get();
			Either<CodeSample, Error> result = new JavaSourceReferenceResolverImpl().resolve(ref, simpleCode);
			
			assertTrue(result.isLeft());
			assertEquals(match, result.left().code());
			assertEquals("java", result.left().type());
		}
		
	}
	
	public static class ComplexCase {
		
		private final String code="public class ClassInMethod {\n" + 
				"\n" + 
				"	public void someMethod() {\n" + 
				"		class Embedded {\n" + 
				"			\n" + 
				"			public Embedded(String foo) {\n" + 
				"				\n" + 
				"			}\n" + 
				"			\n" + 
				"			public void methodInEmbedded() {\n" + 
				"				/*method in embedded*/\n" + 
				"			}\n" + 
				"		};\n" + 
				"	}\n" + 
				"	\n" + 
				"	class Anon {\n" + 
				"\n" + 
				"		public Anon(boolean bar) {\n" + 
				"			\n" + 
				"		}\n" + 
				"		\n" + 
				"		public void methodInAnon(int number) {\n" + 
				"			\n" + 
				"		}\n" + 
				"	}\n" + 
				"}";
		
		@Test
		public void resolveMethodInEmbeddedClass() {
			String match="/*method in embedded*/";
			
			Reference ref=Reference.parse("de.flapdoodle.codedoc.sample.ClassInMethod.someMethod().Embedded.methodInEmbedded()").get();
			Either<CodeSample, Error> result = new JavaSourceReferenceResolverImpl().resolve(ref, code);
			
			assertTrue(result.isLeft());
			assertEquals(match, result.left().code());
			assertEquals("java", result.left().type());

		}
	}
}
