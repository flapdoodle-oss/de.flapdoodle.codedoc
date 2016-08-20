package de.flapdoodle.codedoc.common;

import java.io.File;
import java.nio.file.Path;

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
		Path resolvedLocation = currentDirectory.resolve(resourceLocator.value());
		File resolvedFile = resolvedLocation.toFile();
		if (resolvedFile.exists() && resolvedFile.isFile() && resolvedFile.canRead()) {
			String fileName = resolvedFile.getName();
			String codeType = "?";
			int idx=fileName.lastIndexOf('.');
			if (idx!=-1) {
				codeType=codeTypeMapping.codeTypeOf(fileName.substring(idx+1));
			}
			return Optional.of(CodeSample.of(codeType, Files.readFile().apply(resolvedFile)));
		}
		return Optional.absent();
	}


}
