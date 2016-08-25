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
package de.flapdoodle.codedoc.common;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;

public abstract class Optionals {

	private Optionals() {
		// no instance
	}
	
	public static <T> Optional<T> flatmap(Optional<Optional<T>> src) {
		return src.isPresent() ? src.get() : Optional.<T>absent();
	}
	
	public static <L,R> Either<L, R> or(Optional<L> left, Supplier<R> right) {
		return left.isPresent() ? Either.<L,R>left(left.get()) : Either.<L,R>right(right.get());
	}
}
