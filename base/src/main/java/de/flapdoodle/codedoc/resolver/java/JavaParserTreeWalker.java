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

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;

public abstract class JavaParserTreeWalker {

	private JavaParserTreeWalker() {
		// no instance
	}

//	static <T extends Node,R> void visit(Node parent, Class<T> type, NodeVisitor<T,R> visitor) {
//		if (type.isInstance(parent)) {
//			Either<Navigation, Optional<R>> result = visitor.visit((T) parent);
//			if (result.isLeft()) {
//				
//			}
//		} else {
//			
//		}
//	}
//	
	static <T extends Node> ImmutableList<T> allOf(Node parent, Class<T> type, Predicate<? super T> matcher) {
		ImmutableList.Builder<T> builder=ImmutableList.builder(); 
		if (type.isInstance(parent) && matcher.apply((T) parent)) {
			builder.add((T) parent);
		} else {
			for (Node sub : parent.getChildrenNodes()) {
				ImmutableList<T> matching = allOf(sub, type, matcher);
				builder.addAll(matching);
			}
		}
		return builder.build();
	}

	static <T extends Node> Optional<T> firstOf(Node parent, Class<T> type, Predicate<? super T> matcher) {
		if (type.isInstance(parent) && matcher.apply((T) parent)) {
			return Optional.of((T) parent);
		} else {
			for (Node sub : parent.getChildrenNodes()) {
				Optional<T> match = firstOf(sub, type, matcher);
				if (match.isPresent()) {
					return match;
				}
			}
		}
		return Optional.absent();
	}

	static Optional<ClassOrInterfaceDeclaration> rootClass(Node parent) {
		return firstOf(parent, ClassOrInterfaceDeclaration.class, Predicates.alwaysTrue());
	}
	
//	public enum Navigation {
//		AllChildren,DontGoDeeper,Abort;
//	}
//	
//	public static interface NodeVisitor<T,R> {
//		Either<Navigation, Optional<R>> visit(T node);
//	}
}
