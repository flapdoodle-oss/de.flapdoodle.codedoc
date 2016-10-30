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
package de.flapdoodle.codedoc.resolver.java.javaparser;

import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.Type;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import de.flapdoodle.codedoc.CodeSample;
import de.flapdoodle.codedoc.common.Either;
import de.flapdoodle.codedoc.common.Error;
import de.flapdoodle.codedoc.resolver.java.JavaSourceReferenceResolver;
import de.flapdoodle.codedoc.resolver.java.Reference;
import de.flapdoodle.codedoc.resolver.java.Reference.Part;

public class JavaParserReferenceResolverImpl implements JavaSourceReferenceResolver {

	@Override
	public Either<CodeSample, Error> resolve(final Reference ref, final String code) {
		return JavaParserAdapter.parse(code)
		.flatmapLeft(new Function<CompilationUnit, Either<CodeSample, Error>>() {
			@Override
			public Either<CodeSample, Error> apply(CompilationUnit unit) {
				System.out.println("=>\n"+ JavaParserAdapter.tree(unit, code, 0));
				return resolve(ref, code, unit);
			}
		});
	}

	private static Either<CodeSample, Error> resolve(final Reference ref, String code, CompilationUnit unit) {
		Optional<ClassOrInterfaceDeclaration> rootClass = JavaParserTreeWalker.rootClass(unit);
		Preconditions.checkArgument(rootClass.isPresent(),"no root class found in unit");
		
		Optional<? extends Node> current=Optional.of(unit);
		for (final Reference.Part part : ref.parts()) {
			if (current.isPresent()) {
				if (part.constructor().isPresent()) {
					current = JavaParserTreeWalker.firstOf(current.get(), ConstructorDeclaration.class, new Predicate<ConstructorDeclaration>() {
						@Override
						public boolean apply(ConstructorDeclaration input) {
							System.out.println("Constructor "+part.constructor().get());
							System.out.println(" -> "+input.getParameters());
							return parameterMatch(part.constructor().get().arguments(), input.getParameters());
						}
					});
				} else {
					if (part.method().isPresent()) {
						current = JavaParserTreeWalker.firstOf(current.get(), MethodDeclaration.class, new Predicate<MethodDeclaration>() {
							@Override
							public boolean apply(MethodDeclaration input) {
								System.out.println("Method "+part.method().get());
								System.out.println(" -> "+input.getName());
								System.out.println(" -> "+input.getParameters());
								return input.getName().equals(part.method().get().name()) && parameterMatch(part.method().get().arguments(), input.getParameters());
							}
						});
					} else {
						current = JavaParserTreeWalker.firstOf(current.get(), ClassOrInterfaceDeclaration.class, new Predicate<ClassOrInterfaceDeclaration>() {
							@Override
							public boolean apply(ClassOrInterfaceDeclaration input) {
								return input.getName().equals(part.className());
							}
						});
					}
				}
			}
		}
		
		System.out.println(" current =>"+current);
		
		if (current.isPresent()) {
			switch (ref.scope()) {
			case All:
				if ((ref.parts().size()==1) && (ref.parts().get(0).isClassReference())) {
					return Either.left(CodeSample.of("java", code));
				}
				return Either.left(CodeSample.of("java", JavaParserAdapter.cut(code, current.get())));
			case Exact:
				return Either.left(CodeSample.of("java", JavaParserAdapter.cut(code, current.get())));
			case Body:
				return Either.left(CodeSample.of("java", JavaParserAdapter.bodyOf(code, current.get())));
			}
		}
		
//		Optional<ClassOrInterfaceDeclaration> classOrInterface = JavaParserTreeWalker.firstOf(unit, ClassOrInterfaceDeclaration.class, new Predicate<ClassOrInterfaceDeclaration>() {
//			@Override
//			public boolean apply(ClassOrInterfaceDeclaration input) {
//				
//				return input.getName().equals(parts.get(0).className());
//			}
//		});
		
//		boolean typeMatches = !JavaParserAdapter.typeDeclarationOf(ref, unit).isEmpty();
//		if (!typeMatches) {
//			return Either.right(Error.with("type does not match "+unit.getTypes()));
//		}
//		
//		if (ref.constructor().isPresent()) {
//			
//		} else if (ref.method().isPresent()) {
//			
//		} else {
//			return codeOfClass(code, unit, ref);
//		}
		return Either.right(Error.with("fooo"));
	}

	private static boolean parameterMatch(ImmutableList<String> arguments, List<Parameter> parameters) {
		if (arguments.size()==parameters.size()) {
			for (int i=0;i<arguments.size();i++) {
				String arg=arguments.get(i);
				Parameter param = parameters.get(i);
				String typeAsString  = param.getType().toString();
				System.out.println("Arg "+arg+" ? Param "+typeAsString);
				if (!arg.equals(typeAsString)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	private static Either<CodeSample, Error> codeOfClass(String code, CompilationUnit unit, Reference ref) {
		switch (ref.scope()) {
		case All:
			return Either.left(CodeSample.of("java", code));
		case Exact:
			return Either.left(CodeSample.of("java", JavaParserAdapter.cut(code, unit)));
//		case Body:
//			TypeDeclaration typeDecl = JavaParserAdapter.typeDeclarationOf(ref, unit).get(0);
////			System.out.println("children: "+typeDecl.getChildrenNodes());
//			return Either.left(CodeSample.of("java", JavaParserAdapter.bodyOf(code, typeDecl)));
		}
		return Either.right(Error.with("unknown scope " + ref.scope()));
	}

}
