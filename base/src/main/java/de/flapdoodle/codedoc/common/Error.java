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
package de.flapdoodle.codedoc.common;

import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

import com.google.common.base.Optional;

@Value.Immutable
public abstract class Error {

	@Parameter
	public abstract String message();
	
	@Parameter
	public abstract Optional<Throwable> exception();
	
	public static Error with(String message) {
		return ImmutableError.of(message, Optional.<Throwable>absent());
	}
	
	public static Error with(String message, Throwable throwable) {
		return ImmutableError.of(message, Optional.of(throwable));
	}
}
