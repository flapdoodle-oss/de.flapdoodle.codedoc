package de.flapdoodle.codedoc.resolver.java;

import static org.junit.Assert.*;

import org.junit.Test;

import de.flapdoodle.codedoc.CodeSample;
import de.flapdoodle.codedoc.common.Either;
import de.flapdoodle.codedoc.common.Error;

public class JavaSourceReferenceResolverImplTest {

	@Test
	public void resolveFullClass() {
		String code="import foo;\n"
				+ "\n"
				+ "public class Foo {\n"
				+ "}\n";
		
		Reference ref=Reference.parse("foo.Foo").get();
		Either<CodeSample, Error> result = new JavaSourceReferenceResolverImpl().resolve(ref, code);
		
		assertTrue(result.isLeft());
		assertEquals(CodeSample.of("java", code), result.left());
	}
}
