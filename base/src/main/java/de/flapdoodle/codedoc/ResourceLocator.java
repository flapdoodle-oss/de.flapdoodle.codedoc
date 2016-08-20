package de.flapdoodle.codedoc;

import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

@Value.Immutable
public abstract class ResourceLocator {
	@Parameter
	public abstract String value();
	
	public static ResourceLocator of(String value) {
		return ImmutableResourceLocator.of(value);
	}
}
