package de.flapdoodle.codedoc.resolver.files;

import java.io.File;
import java.nio.file.Path;

import com.google.common.base.Function;
import com.google.common.base.Optional;

import de.flapdoodle.codedoc.CodeResolver;
import de.flapdoodle.codedoc.CodeSample;
import de.flapdoodle.codedoc.ResourceLocator;
import de.flapdoodle.codedoc.io.Files;

public class FilesystemCodeResolver implements CodeResolver {

	private FileExtensionCodeTypeMapping codeTypeMapping;

	public FilesystemCodeResolver(FileExtensionCodeTypeMapping codeTypeMapping) {
		this.codeTypeMapping = codeTypeMapping;
	}
	
	@Override
	public Optional<CodeSample> resolve(Path currentDirectory, ResourceLocator resourceLocator) {
		final Path resolvedLocation = currentDirectory.resolve(resourceLocator.value());
		return Files.contentOf(resolvedLocation)
				.transform(new Function<String, CodeSample>() {

					@Override
					public CodeSample apply(String code) {
						return CodeSample.of(codeType(resolvedLocation.getFileName().toString()), code);
					}
				});
	}

	private String codeType(String fileName) {
		String codeType = "?";
		int idx=fileName.lastIndexOf('.');
		if (idx!=-1) {
			codeType=codeTypeMapping.codeTypeOf(fileName.substring(idx+1));
		}
		return codeType;
	}


}
