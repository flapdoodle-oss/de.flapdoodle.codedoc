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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.immutables.value.Value;
import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Parameter;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import de.flapdoodle.codedoc.resolver.java.ImmutablePart.Builder;
import de.flapdoodle.codedoc.resolver.java.Reference.Part;

@Value.Immutable
public abstract class Reference {
	
	private static final String EXACT = "exact";
	private static final String BODY = "body";
	private static final String ALL = "all";
	
	private static final String CLASS_CONSTR_OR_METHOD = "(?<class>[A-Z][A-Za-z0-9]+)(\\.((?<constructor>\\k<class>)|(?<methodName>[A-Za-z0-9]+))(?<body>\\([^\\)]*\\)))?";
	private static final Pattern CLASS_CONSTR_OR_METHOD_PATTERN=Pattern.compile(CLASS_CONSTR_OR_METHOD);
	private static final Pattern PACKAGE_AND_CLASS_PATTERN=Pattern.compile("^(?<package>([a-z0-9]+\\.)*)"
			+ "(?<all>("
			+ CLASS_CONSTR_OR_METHOD
			+ "\\.?)+)"
			+ "(\\s+(?<scope>(" + ALL + "|" + BODY + "|" + EXACT + ")))?");
	
	private static final Pattern METHOD_ARG_PATTERN=Pattern.compile("\\s?(?<arg>[^,.]+)\\s?,?\\s?");
	
	public abstract Optional<String> packageName();
	public abstract ImmutableList<Part> parts();
//	public abstract String className();
//	public abstract Optional<Method> method();
//	public abstract Optional<Constructor> constructor();
	
	@Default
	public Scope scope() {
		if (isClassReference()) {
			return Scope.Exact;
		}
		return Scope.Body;
	}
	
	public enum Scope { Body, Exact, All;

	public static Scope of(String scope) {
		switch (scope) {
		case ALL:
			return All;
		case BODY:
			return Body;
		case EXACT:
			return Exact;
		}
		throw new IllegalArgumentException("unknown scope");
	} };
	
//	@Auxiliary
//	public String packageAndClassname() {
//		return packageName().isPresent() ? packageName().get()+"."+className() : className();
//	}
//	
	@Auxiliary
	public String asString() {
		return (packageName().isPresent() ? packageName().get()+"." : "") +FluentIterable.from(parts()).transform(Part.asString).join(Joiner.on('.'));
	}
	
	
	@Auxiliary
	public boolean isClassReference() {
		Part lastPart = parts().get(parts().size()-1);
		return lastPart.isClassReference();
	};
	
	public boolean simpleReference() {
		return parts().size()==1;
	}
	
	@Value.Immutable
	public static abstract class Constructor {
		@Parameter
		public abstract ImmutableList<String> arguments();
		
		@Auxiliary
		public String asString() {
			return Joiner.on(", ").join(arguments());
		}
	}
	
	@Value.Immutable
	public static abstract class Method {
		@Parameter
		public abstract String name();
		
		@Parameter
		public abstract ImmutableList<String> arguments();
		
		@Auxiliary
		public String asString() {
			return name()+"("+Joiner.on(", ").join(arguments())+")";
		}

	}
	
	@Value.Immutable
	public static abstract class Part {
		public static Function<Part, String> asString=new Function<Reference.Part, String>() {
			
			@Override
			public String apply(Part input) {
				return input.asString();
			}
		};
		
		public abstract String className();
		public abstract Optional<Method> method();
		public abstract Optional<Constructor> constructor();
		
		@Auxiliary
		public String asString() {
			return className()+(method().isPresent() ? method().get().asString() : "")+(constructor().isPresent() ? className()+"("+ constructor().get().asString()+")" : "");
		}
		
		@Auxiliary
		public boolean isClassReference() {
			return !method().isPresent() && !constructor().isPresent();
		}
	}
	
	public static Optional<? extends Reference> parse(String src) {
		Optional<? extends Reference> ret=Optional.absent();
		
		Matcher matcher = PACKAGE_AND_CLASS_PATTERN.matcher(src);
		
//		if (matcher.matches()) {
//			ImmutableReference.Builder builder = ImmutableReference.builder();
//			String packageName = matcher.group("package");
//			String className = matcher.group("class");
//			builder.scope(Optional.fromNullable(matcher.group("scope")));
//			if (!packageName.isEmpty()) {
//				builder.packageName(packageName.substring(0, packageName.length()-1));
//			}
//			builder.className(className);
//			
//			ret=Optional.of(builder.build());
//		} else {
			if (matcher.matches()) {
				ImmutableReference.Builder builder = ImmutableReference.builder();
				String packageName = matcher.group("package");
				if (packageName!=null && !packageName.isEmpty()) {
					builder.packageName(packageName.substring(0, packageName.length()-1));
				}
				String scope = matcher.group("scope");
				if (scope!=null && !scope.isEmpty()) {
					builder.scope(Scope.of(scope));
				}
				String all = matcher.group("all");
				Matcher classMethodConstrMatcher = CLASS_CONSTR_OR_METHOD_PATTERN.matcher(all);
				
				while (classMethodConstrMatcher.find()) {
					ImmutablePart.Builder partBuilder = ImmutablePart.builder();
					
					String className = classMethodConstrMatcher.group("class");
					partBuilder.className(className);
					
					String constructor = classMethodConstrMatcher.group("constructor");
					String methodName = classMethodConstrMatcher.group("methodName");
					String methodArgsBody = classMethodConstrMatcher.group(BODY);
//					String left = src.substring(matcher.end());
//					Matcher methodMatcher = METHOD_ARGS_PATTERN.matcher(left);
					if (methodArgsBody!=null && !methodArgsBody.isEmpty() /*methodMatcher.matches()*/) {
						//String methodArgsBody = methodMatcher.group("body");
						ImmutableList<String> methodArgs = methodArgs(methodArgsBody.substring(1,methodArgsBody.length()-1));
						
						if (constructor!=null) {
							partBuilder.constructor(ImmutableConstructor.of(methodArgs));
						} else {
							if (methodName!=null) {
								partBuilder.method(ImmutableMethod.of(methodName, methodArgs));
							}
						}
					}
					builder.addParts(partBuilder.build());
				}
				ret=Optional.of(builder.build());
//			} else {
//				if (matcher.find(0)) {
//					System.out.println("no match, but found");
//					int count = matcher.groupCount();
//					for (int i=0;i<count;i++) {
//						System.out.println(""+i+": "+matcher.group(i));
//					}
//				} else {
//					System.out.println("nothing");
//				}
			}
//		}
		
//		System.out.println(src+" -> "+ret);
		
		return ret;
	}
	
	private static ImmutableList<String> methodArgs(String group) {
		ImmutableList.Builder<String> builder=ImmutableList.builder();
		
		String left=group;
		do {
			Matcher matcher = METHOD_ARG_PATTERN.matcher(left);
			if (matcher.find()) {
//				System.out.println("Arg: "+matcher.group("arg"));
				builder.add(matcher.group("arg"));
				left=left.substring(matcher.end());
			} else {
				System.out.println("end");
				left="";
			}
		} while (!left.isEmpty());
		
		return builder.build();
	}
}
