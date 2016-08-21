package de.flapdoodle.codedoc.resolver.java;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.google.common.primitives.Chars;

import de.flapdoodle.codedoc.CodeSample;
import de.flapdoodle.codedoc.common.Either;
import de.flapdoodle.codedoc.common.Error;
import de.flapdoodle.codedoc.exceptions.Exceptions;

public class JavaSourceReferenceResolverImpl implements JavaSourceReferenceResolver {

	@Override
	public Either<CodeSample, Error> resolve(Reference ref, String code) {
		try {
			CompilationUnit unit = JavaParser.parse(new StringReader(code), true);
			return resolve(ref, code, unit);
		} catch (ParseException e) {
			return Either.right(Error.with("could not get ref"+ref, e));
		}
	}

	private static Either<CodeSample, Error> resolve(Reference ref, String code, CompilationUnit unit) {
		if (ref.constructor().isPresent()) {
			
		} else if (ref.method().isPresent()) {
			
		} else {
			return Either.left(CodeSample.of("java", cut(code, unit.getBeginLine(), unit.getBeginColumn(), unit.getEndLine(), unit.getEndColumn())));
		}
		return Either.right(Error.with("fooo"));
	}

	private static String cut(String code, int beginLine, int beginColumn, int endLine, int endColumn) {
//		System.out.println("" + beginLine + ":" + beginColumn + " - " + endLine + ":" + endColumn);
		List<String> lines = Splitter.on('\n').splitToList(code);
//		System.out.println(" -> " + formated(lines)+"="+lines.size());
		List<String> matchingLines = lines.subList(beginLine-1, endLine);
//		System.out.println(" --> " + formated(matchingLines)+"="+matchingLines.size());		
		String firstLine = matchingLines.get(0).substring(beginColumn-1);
//		System.out.println(" [" + firstLine);
		String lastLine = matchingLines.get(matchingLines.size() - 1).substring(0, endColumn-1);
//		System.out.println("" + lastLine+"]");

		return Joiner.on("\n").join(ImmutableList.<String>builder()
				.add(firstLine)
				.addAll(matchingLines.subList(1, matchingLines.size() - 1))
				.add(lastLine)
				.build());
	}

	private static String formated(List<String> lines) {
		return "["+Joiner.on('|').join(lines)+"]";
	}

}
