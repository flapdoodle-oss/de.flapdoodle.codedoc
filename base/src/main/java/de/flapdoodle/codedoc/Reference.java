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
package de.flapdoodle.codedoc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.immutables.value.Value;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

@Value.Immutable
public abstract class Reference {
	
	private static final Pattern PACKAGE_AND_CLASS_PATTERN=Pattern.compile("^(?<package>([a-z0-9]+\\.)+)(?<class>[A-Z][A-Za-z0-9]+)(\\.(?<constructor>\\k<class>))?(\\.(?<method>[A-Za-z0-9]+))?");
	private static final Pattern METHOD_ARGS_PATTERN=Pattern.compile("\\("
			+ "(?<body>.*)"
			+ "\\)");
	private static final Pattern METHOD_ARG_PATTERN=Pattern.compile("\\s?(?<arg>[^,.]+)\\s?,?\\s?");
	
	public abstract String className();
	public abstract Optional<String> method();
	
	public static abstract class WithArgumentTypes {
		public abstract ImmutableList<String> arguments();
	}
	
	@Value.Immutable
	public static abstract class Constructor extends WithArgumentTypes {
		
	}
	
	public static Reference parse(String src) {
		int lastDot=src.lastIndexOf('.');
		int openBrace=src.indexOf('(');
		
		System.out.println("->"+src);
		
		Matcher matcher = PACKAGE_AND_CLASS_PATTERN.matcher(src);
		if (matcher.matches()) {
			System.out.println("is class: "+src+" -> "+matcher.group("package")+matcher.group("class"));
		} else {
			if (matcher.find(0)) {
				System.out.println("start with class: "+src+" -> "+matcher.group("package")+matcher.group("class")+"?Const="+matcher.group("constructor")+"?Method="+matcher.group("method"));
				String left = src.substring(matcher.end());
				System.out.println("left: "+left);
				Matcher methodMatcher = METHOD_ARGS_PATTERN.matcher(left);
				if (methodMatcher.matches()) {
					System.out.println("method body: "+methodMatcher.group("body"));
					System.out.println("args: "+methodArgs(methodMatcher.group("body")));
				}
			} else {
				System.out.println("unknown: "+src);
			}
		}
		return ImmutableReference.builder()
				.build();
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
