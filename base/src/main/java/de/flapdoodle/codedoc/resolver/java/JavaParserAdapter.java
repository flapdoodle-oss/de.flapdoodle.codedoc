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

import java.io.StringReader;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.BlockComment;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.flapdoodle.codedoc.common.Either;
import de.flapdoodle.codedoc.common.Error;

public abstract class JavaParserAdapter {

	private JavaParserAdapter() {
		// no instance
	}

	static String tree(Node node, String code, int level) {
		StringBuilder sb = new StringBuilder();
		sb.append(Strings.repeat("  ", level));
		sb.append(info(node));
		sb.append(startAndEndOf(node, code));
		sb.append("\n");
		for (Node child : node.getChildrenNodes()) {
			sb.append(tree(child, code, level + 1));
		}
		return sb.toString();
	}

	private static String startAndEndOf(Node node, String code) {
		String block = cut(code, node);
		int idx = block.indexOf('\n');
		if (idx != -1) {
			int eidx = block.lastIndexOf('\n');
			block = "[" + block.substring(0, idx) + "..." + block.substring(eidx + 1) + "]";
		} else {
			block = "[" + block + "]";
		}
		return block;
	}

	static String info(Node node) {
		StringBuilder sb = new StringBuilder();
		sb.append(node.getClass());
		sb.append(" (").append(lineColumn(node.getBeginLine(), node.getBeginColumn())).append("-")
				.append(lineColumn(node.getEndLine(), node.getEndColumn())).append(")");
		return sb.toString();
	}

	private static String lineColumn(int line, int column) {
		return line + ":" + column;
	}

	static Either<CompilationUnit, Error> parse(String code) {
		try {
			return Either.left(JavaParser.parse(new StringReader(code), true));
		} catch (ParseException e) {
			return Either.right(Error.with("parse " + code, e));
		}
	}

	static String cut(String code, Node node) {
		if (node instanceof BlockComment) {
			return cut(code, node.getBeginLine(), node.getBeginColumn(), node.getEndLine(), node.getEndColumn()-1);
		}
		return cut(code, node.getBeginLine(), node.getBeginColumn(), node.getEndLine(), node.getEndColumn());
	}

	private static String cut(String code, int beginLine, int beginColumn, int endLine, int endColumn) {
		// System.out.println("" + beginLine + ":" + beginColumn + " - " +
		// endLine + ":" + endColumn);
		List<String> lines = codeLines(code);
		// System.out.println(" -> " + formated(lines)+"="+lines.size());
		List<String> matchingLines = lines.subList(beginLine - 1, endLine);
		// System.out.println(" --> " +
		// formated(matchingLines)+"="+matchingLines.size());
		String firstLinePart = "";
		Optional<String> lastLinePart = Optional.absent();
		List<String> between = Lists.newArrayList();
		if (matchingLines.size() == 1) {
			firstLinePart = matchingLines.get(0).substring(beginColumn - 1, endColumn);
			// System.out.println(" [" + firstLinePart+"]");
		} else {
			firstLinePart = matchingLines.get(0).substring(beginColumn - 1);
			// System.out.println(" [" + firstLinePart);
			lastLinePart = Optional.of(matchingLines.get(matchingLines.size() - 1).substring(0, endColumn));
			// System.out.println("" + lastLinePart+"]");
			between = matchingLines.subList(1, matchingLines.size() - 1);
		}

		return Joiner.on("").join(ImmutableList.<String>builder().add(firstLinePart).addAll(between)
				.addAll(lastLinePart.asSet()).build());
	}

	private static List<String> codeLines(String code) {
		return FluentIterable.from(Splitter.on('\n').split(code)).transform(new Function<String, String>() {

			@Override
			public String apply(String line) {
				return line+"\n";
			}
			
		}).toList();
	}

	static String cut(String code, Node first, Node last) {
		if (last instanceof BlockComment) {
			return cut(code, first.getBeginLine(), first.getBeginColumn(), last.getEndLine(), last.getEndColumn()-1);
		}
		return cut(code, first.getBeginLine(), first.getBeginColumn(), last.getEndLine(), last.getEndColumn());
	}

	private static String formated(List<String> lines) {
		return "[" + Joiner.on('|').join(lines) + "]";
	}

	static String bodyOf(String code, Node node) {
		System.out.println("bodyOf "+tree(node, code, 0));
		List<Node> children = node.getChildrenNodes();
		Node first = children.get(0);
		Node last = children.get(children.size() - 1);
		return cut(code, first, last);
	}

	@Deprecated
	static ImmutableList<TypeDeclaration> typeDeclarationOf(final Reference ref, CompilationUnit unit) {
		return FluentIterable.from(unit.getTypes()).filter(new Predicate<TypeDeclaration>() {

			@Override
			public boolean apply(TypeDeclaration input) {
				return input.getName().equals(ref.className());
			}
		}).toList();
	}
}
