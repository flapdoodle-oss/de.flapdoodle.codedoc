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

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

import de.flapdoodle.codedoc.CodeSample;
import de.flapdoodle.codedoc.common.Either;
import de.flapdoodle.codedoc.common.Error;

public class JavaSourceReferenceResolverImpl implements JavaSourceReferenceResolver {

	@Override
	public Either<CodeSample, Error> resolve(final Reference ref, final String code) {
		return JavaParserAdapter.parse(code)
		.flatmapLeft(new Function<CompilationUnit, Either<CodeSample, Error>>() {
			@Override
			public Either<CodeSample, Error> apply(CompilationUnit unit) {
				System.out.println("=>"+ JavaParserAdapter.tree(unit, code, 0));
				return resolve(ref, code, unit);
			}
		});
	}

	private static Either<CodeSample, Error> resolve(final Reference ref, String code, CompilationUnit unit) {
		Optional<ClassOrInterfaceDeclaration> rootClass = JavaParserTreeWalker.rootClass(unit);
		Preconditions.checkArgument(rootClass.isPresent(),"no root class found in unit");
		
		Optional<ClassOrInterfaceDeclaration> classOrInterface = JavaParserTreeWalker.firstOf(unit, ClassOrInterfaceDeclaration.class, new Predicate<ClassOrInterfaceDeclaration>() {
			@Override
			public boolean apply(ClassOrInterfaceDeclaration input) {
				return input.getName().equals(ref.className());
			}
		});
		
		boolean typeMatches = !JavaParserAdapter.typeDeclarationOf(ref, unit).isEmpty();
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

	private static Either<CodeSample, Error> codeOfClass(String code, CompilationUnit unit, Reference ref) {
		switch (ref.scope()) {
		case All:
			return Either.left(CodeSample.of("java", code));
		case Exact:
			return Either.left(CodeSample.of("java", JavaParserAdapter.cut(code, unit)));
		case Body:
			TypeDeclaration typeDecl = JavaParserAdapter.typeDeclarationOf(ref, unit).get(0);
//			System.out.println("children: "+typeDecl.getChildrenNodes());
			return Either.left(CodeSample.of("java", JavaParserAdapter.bodyOf(code, typeDecl)));
		}
		return Either.right(Error.with("unknown scope " + ref.scope()));
	}

}
