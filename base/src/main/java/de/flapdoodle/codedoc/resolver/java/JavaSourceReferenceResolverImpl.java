package de.flapdoodle.codedoc.resolver.java;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.google.common.primitives.Chars;

import de.flapdoodle.codedoc.CodeSample;
import de.flapdoodle.codedoc.common.Either;
import de.flapdoodle.codedoc.common.Either.Left;
import de.flapdoodle.codedoc.common.Error;
import de.flapdoodle.codedoc.exceptions.Exceptions;
import de.flapdoodle.codedoc.resolver.java.Reference.Scope;

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

	private static Either<CodeSample, Error> resolve(final Reference ref, String code, CompilationUnit unit) {
		boolean typeMatches = !typeDeclarationOf(ref, unit).isEmpty();
		if (!typeMatches) {
			return Either.right(Error.with("type does not match "+unit.getTypes()));
		}
		
		if (ref.constructor().isPresent()) {
			
		} else if (ref.method().isPresent()) {
			
		} else {
			return codeOfClass(code, unit, ref);
		}
		return Either.right(Error.with("fooo"));
	}

	private static ImmutableList<TypeDeclaration> typeDeclarationOf(final Reference ref, CompilationUnit unit) {
		return FluentIterable.from(unit.getTypes())
			.filter(new Predicate<TypeDeclaration>() {

				@Override
				public boolean apply(TypeDeclaration input) {
					return input.getName().equals(ref.className());
				}
			})
			.toList();
	}

	private static Either<CodeSample, Error> codeOfClass(String code, CompilationUnit unit, Reference ref) {
		switch (ref.scope()) {
		case All:
			return Either.left(CodeSample.of("java", code));
		case Exact:
			return Either.left(CodeSample.of("java",
					cut(code, unit.getBeginLine(), unit.getBeginColumn(), unit.getEndLine(), unit.getEndColumn())));
		case Body:
			TypeDeclaration typeDecl = typeDeclarationOf(ref, unit).get(0);
//			System.out.println("children: "+typeDecl.getChildrenNodes());
			return Either.left(CodeSample.of("java", bodyOf(code, typeDecl)));
		}
		return Either.right(Error.with("unknown scope " + ref.scope()));
	}

	private static String bodyOf(String code, Node node) {
		List<Node> children = node.getChildrenNodes();
		Node first=children.get(0);
		Node last=children.get(children.size()-1);
		return cut(code,first.getBeginLine(),first.getBeginColumn(),last.getEndLine(), last.getEndColumn());
	}

	private static String cut(String code, int beginLine, int beginColumn, int endLine, int endColumn) {
//		System.out.println("" + beginLine + ":" + beginColumn + " - " + endLine + ":" + endColumn);
		List<String> lines = Splitter.on('\n').splitToList(code);
//		System.out.println(" -> " + formated(lines)+"="+lines.size());
		List<String> matchingLines = lines.subList(beginLine-1, endLine);
//		System.out.println(" --> " + formated(matchingLines)+"="+matchingLines.size());		
		String firstLinePart="";
		Optional<String> lastLinePart=Optional.absent();
		List<String> between=Lists.newArrayList();
		if (matchingLines.size()==1) {
			firstLinePart = matchingLines.get(0).substring(beginColumn-1, endColumn-1);
//			System.out.println(" [" + firstLinePart+"]");
		} else {
			firstLinePart = matchingLines.get(0).substring(beginColumn-1);
//			System.out.println(" [" + firstLinePart);
			lastLinePart = Optional.of(matchingLines.get(matchingLines.size() - 1).substring(0, endColumn-1));
//			System.out.println("" + lastLinePart+"]");
			between = matchingLines.subList(1, matchingLines.size() - 1);
		}

		return Joiner.on("\n").join(ImmutableList.<String>builder()
				.add(firstLinePart)
				.addAll(between)
				.addAll(lastLinePart.asSet())
				.build());
	}

	private static String formated(List<String> lines) {
		return "["+Joiner.on('|').join(lines)+"]";
	}

}
