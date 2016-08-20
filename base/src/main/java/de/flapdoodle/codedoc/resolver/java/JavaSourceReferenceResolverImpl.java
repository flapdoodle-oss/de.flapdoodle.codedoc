package de.flapdoodle.codedoc.resolver.java;

import java.io.StringReader;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

import de.flapdoodle.codedoc.CodeSample;
import de.flapdoodle.codedoc.common.Either;
import de.flapdoodle.codedoc.common.Error;

public class JavaSourceReferenceResolverImpl implements JavaSourceReferenceResolver {

	@Override
	public Either<CodeSample, Error> resolve(Reference ref, String code) {
		try {
			CompilationUnit unit = JavaParser.parse(new StringReader(code), true);
			return resolve(ref, unit);
		} catch (ParseException e) {
			return Either.right(Error.with("could not get ref"+ref, e));
		}
	}

	private static Either<CodeSample, Error> resolve(Reference ref, CompilationUnit unit) {
		if (ref.constructor().isPresent()) {
			
		} else if (ref.method().isPresent()) {
			
		} else {
			return Either.left(CodeSample.of("java", unit.toString()));
		}
		return Either.right(Error.with("fooo"));
	}

}
