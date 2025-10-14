package com.fudala.lab01.infrastructure.io;

import com.fudala.lab01.domain.io.PathProvider;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public record SystemTempPathProvider(String subdirName, String fileName) implements PathProvider {

    @Override
    public Path getPath() throws IOException {
        final String tmpDir;
        try {
            tmpDir = System.getProperty("java.io.tmpdir");
        } catch (SecurityException se) {
            throw new IOException("Cannot read system property 'java.io.tmpdir'.", se);
        }
        if (tmpDir == null || tmpDir.isBlank()) {
            throw new IOException("System property 'java.io.tmpdir' is not set.");
        }

        final Path baseDir;
        try {
            baseDir = Paths.get(tmpDir, subdirName);
        } catch (InvalidPathException ipe) {
            throw new IOException("Invalid temp directory path.", ipe);
        }

        try {
            Files.createDirectories(baseDir);
        } catch (AccessDeniedException ade) {
            throw new IOException("Access denied while creating directory: " + baseDir, ade);
        } catch (FileSystemException fse) {
            throw new IOException("File system error while creating directory: " + baseDir, fse);
        }

        final Path validatedFileName;
        try {
            validatedFileName = Paths.get(fileName);
        } catch (InvalidPathException ipe) {
            throw new IOException("Invalid file name: " + fileName, ipe);
        }

        return baseDir.resolve(validatedFileName);
    }
}