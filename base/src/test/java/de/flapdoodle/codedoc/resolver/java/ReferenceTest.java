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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.flapdoodle.codedoc.resolver.java.Reference;
import de.flapdoodle.codedoc.resolver.java.Reference.Part;
import de.flapdoodle.codedoc.resolver.java.Reference.Scope;

public class ReferenceTest {
	
	@Test
	public void noPackageReference() {
		String asString="Sample";
		Reference result = Reference.parse(asString).get();
		assertFalse(result.packageName().isPresent());
		assertTrue(result.simpleReference());
		assertEquals("Sample",result.parts().get(0).className());
		assertEquals(asString,result.asString());
		assertEquals(Scope.Exact,result.scope());
	}
	
	@Test
	public void classReference() {
		String asString="de.flapdoodle.codedoc.Sample";
		Reference result = Reference.parse(asString).get();
		assertEquals("de.flapdoodle.codedoc",result.packageName().get());
		Part firstPart = result.parts().get(0);
		assertEquals("Sample",firstPart.className());
		assertEquals(asString,result.asString());
		assertFalse(firstPart.constructor().isPresent());
		assertFalse(firstPart.method().isPresent());
		assertEquals(Scope.Exact,result.scope());
	}
	
	@Test
	public void shortClassReference() {
		String asString="foo.Foo";
		Reference result = Reference.parse(asString).get();
		assertEquals("foo",result.packageName().get());
		Part firstPart = result.parts().get(0);
		assertEquals("Foo",firstPart.className());
		assertEquals(asString,result.asString());
		assertFalse(firstPart.constructor().isPresent());
		assertFalse(firstPart.method().isPresent());
		assertEquals(Scope.Exact,result.scope());
	}
	
	@Test
	public void constructorReference() {
		String asString="de.flapdoodle.codedoc.Sample.Sample(boolean)";
		Reference result = Reference.parse(asString).get();
		assertEquals("de.flapdoodle.codedoc",result.packageName().get());
		Part firstPart = result.parts().get(0);
		assertEquals("Sample",firstPart.className());
		assertTrue(firstPart.constructor().isPresent());
		assertEquals("[boolean]",firstPart.constructor().get().arguments().toString());
		assertFalse(firstPart.method().isPresent());
		assertEquals(Scope.Body,result.scope());
	}
	
	@Test
	public void methodReference() {
		String asString="de.flapdoodle.codedoc.Sample.twoArg(String, int)";
		Reference result = Reference.parse(asString).get();
		assertEquals("de.flapdoodle.codedoc",result.packageName().get());
		Part firstPart = result.parts().get(0);
		assertEquals("Sample",firstPart.className());
		assertFalse(firstPart.constructor().isPresent());
		assertTrue(firstPart.method().isPresent());
		assertEquals("[String, int]",firstPart.method().get().arguments().toString());
		assertEquals(Scope.Body,result.scope());
	}
	
	@Test
	public void genericMethodReference() {
		String asString="de.flapdoodle.codedoc.Sample.generic(T)";
		Reference result = Reference.parse(asString).get();
		assertEquals("de.flapdoodle.codedoc",result.packageName().get());
		Part firstPart = result.parts().get(0);
		assertEquals("Sample",firstPart.className());
		assertFalse(firstPart.constructor().isPresent());
		assertTrue(firstPart.method().isPresent());
		assertEquals("[T]",firstPart.method().get().arguments().toString());
		assertEquals(Scope.Body,result.scope());
	}
	
	@Test
	public void additionalScope() {
		String asString="de.flapdoodle.codedoc.Sample.twoArg(String, int) all";
		Reference result = Reference.parse(asString).get();
		assertEquals("de.flapdoodle.codedoc",result.packageName().get());
		Part firstPart = result.parts().get(0);
		assertEquals("Sample",firstPart.className());
		assertFalse(firstPart.constructor().isPresent());
		assertTrue(firstPart.method().isPresent());
		assertEquals("[String, int]",firstPart.method().get().arguments().toString());
		assertEquals(Scope.All,result.scope());
	}
	
	@Test
	public void methodInClassInMethod() {
		String asString="de.flapdoodle.codedoc.sample.ClassInMethod.someMethod().Embedded.methodInEmbedded()";
		Reference result = Reference.parse(asString).get();
		assertEquals("de.flapdoodle.codedoc.sample",result.packageName().get());
		assertEquals(2,result.parts().size());
		
		Part firstPart = result.parts().get(0);
		assertEquals("ClassInMethod",firstPart.className());
		assertFalse(firstPart.constructor().isPresent());
		assertTrue(firstPart.method().isPresent());
		assertEquals("someMethod",firstPart.method().get().name());
		assertEquals("[]",firstPart.method().get().arguments().toString());
		
		Part secondPart = result.parts().get(1);
		assertEquals("Embedded",secondPart.className());
		assertFalse(secondPart.constructor().isPresent());
		assertTrue(secondPart.method().isPresent());
		assertEquals("methodInEmbedded",secondPart.method().get().name());
		assertEquals("[]",secondPart.method().get().arguments().toString());
		
		assertEquals(Scope.Body,result.scope());
	}
	

	@Test
	public void constructorInClassInMethod() {
		String asString="de.flapdoodle.codedoc.sample.ClassInMethod.someMethod().Embedded.Embedded(ClassInMethod, String)";
		Reference result = Reference.parse(asString).get();
		assertEquals("de.flapdoodle.codedoc.sample",result.packageName().get());
		assertEquals(2,result.parts().size());
		
		Part firstPart = result.parts().get(0);
		assertEquals("ClassInMethod",firstPart.className());
		assertFalse(firstPart.constructor().isPresent());
		assertTrue(firstPart.method().isPresent());
		assertEquals("someMethod",firstPart.method().get().name());
		assertEquals("[]",firstPart.method().get().arguments().toString());
		
		Part secondPart = result.parts().get(1);
		assertEquals("Embedded",secondPart.className());
		assertTrue(secondPart.constructor().isPresent());
		assertFalse(secondPart.method().isPresent());
		assertEquals("[ClassInMethod, String]",secondPart.constructor().get().arguments().toString());
		
		assertEquals(Scope.Body,result.scope());
	}
}
