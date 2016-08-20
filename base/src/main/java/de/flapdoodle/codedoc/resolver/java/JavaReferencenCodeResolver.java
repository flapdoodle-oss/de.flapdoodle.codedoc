package de.flapdoodle.codedoc.resolver.java;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Strings;

import de.flapdoodle.codedoc.CodeResolver;
import de.flapdoodle.codedoc.CodeSample;
import de.flapdoodle.codedoc.ResourceLocator;
import de.flapdoodle.codedoc.common.Optionals;

public class JavaReferencenCodeResolver implements CodeResolver {

	private JavaSourceCodeResolver sourceCodeResolver;

	public JavaReferencenCodeResolver(JavaSourceCodeResolver sourceCodeResolver) {
		this.sourceCodeResolver = sourceCodeResolver;
	}
	
	@Override
	public Optional<CodeSample> resolve(Path currentDirectory, ResourceLocator resourceLocator) {
		Optional<? extends Reference> reference = Reference.parse(resourceLocator.value());
		return Optionals.flatmap(reference.transform(new Function<Reference, Optional<CodeSample>>() {

			@Override
			public Optional<CodeSample> apply(Reference input) {
				return resolve(input);
			}
		}));
	}

	protected Optional<CodeSample> resolve(Reference ref) {
		return codeOf(ref, sourceCodeResolver.resolve(asPath(ref)));
	}

	private Optional<CodeSample> codeOf(Reference ref, Optional<String> resolve) {
		// TODO Auto-generated method stub
		return null;
	}

	protected static Path asPath(Reference ref) {
		String[] packageAndClassname = ref.packageAndClassname().split("\\.");
		return packageAndClassname.length==1 ? Paths.get(packageAndClassname[0]) : Paths.get(packageAndClassname[0], Arrays.copyOfRange(packageAndClassname, 1,packageAndClassname.length));
	}

}
