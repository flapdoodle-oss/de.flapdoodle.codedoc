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
package de.flapdoodle.codedoc.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.flapdoodle.codedoc.java.Reference;

public class ReferenceTest {
	
	@Test
	public void noPackageReference() {
		String asString="Sample";
		Reference result = Reference.parse(asString).get();
		assertFalse(result.packageName().isPresent());
		assertEquals("Sample",result.className());
		assertEquals(asString,result.packageAndClassname());
	}
	
	@Test
	public void classReference() {
		String asString="de.flapdoodle.codedoc.Sample";
		Reference result = Reference.parse(asString).get();
		assertEquals("de.flapdoodle.codedoc",result.packageName().get());
		assertEquals("Sample",result.className());
		assertEquals(asString,result.packageAndClassname());
		assertFalse(result.constructor().isPresent());
		assertFalse(result.method().isPresent());
	}
	
	@Test
	public void constructorReference() {
		String asString="de.flapdoodle.codedoc.Sample.Sample(boolean)";
		Reference result = Reference.parse(asString).get();
		assertEquals("de.flapdoodle.codedoc",result.packageName().get());
		assertEquals("Sample",result.className());
		assertTrue(result.constructor().isPresent());
		assertEquals("[boolean]",result.constructor().get().arguments().toString());
		assertFalse(result.method().isPresent());
	}
	
	@Test
	public void methodReference() {
		String asString="de.flapdoodle.codedoc.Sample.twoArg(String, int)";
		Reference result = Reference.parse(asString).get();
		assertEquals("de.flapdoodle.codedoc",result.packageName().get());
		assertEquals("Sample",result.className());
		assertFalse(result.constructor().isPresent());
		assertTrue(result.method().isPresent());
		assertEquals("[String, int]",result.method().get().arguments().toString());
	}
	
	@Test
	public void genericMethodReference() {
		String asString="de.flapdoodle.codedoc.Sample.generic(T)";
		Reference result = Reference.parse(asString).get();
		assertEquals("de.flapdoodle.codedoc",result.packageName().get());
		assertEquals("Sample",result.className());
		assertFalse(result.constructor().isPresent());
		assertTrue(result.method().isPresent());
		assertEquals("[T]",result.method().get().arguments().toString());
	}
	
}
