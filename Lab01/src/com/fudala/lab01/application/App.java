package com.fudala.lab01.application;

import com.fudala.lab01.domain.io.FileIO;
import com.fudala.lab01.domain.io.PathProvider;
import com.fudala.lab01.domain.time.DateTimeProvider;
import com.fudala.lab01.domain.time.SystemDateTimeProvider;
import com.fudala.lab01.domain.time.TimeFormatter;
import com.fudala.lab01.infrastructure.io.NioFileIO;
import com.fudala.lab01.infrastructure.io.SystemTempPathProvider;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystemException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class App {
    private static final Logger LOG = Logger.getLogger(App.class.getName());
    private App() { throw new AssertionError("No instances"); }

    private static final int EXIT_OK = 0;
    private static final int EXIT_PATH = 4;
    private static final int EXIT_WRITE = 2;
    private static final int EXIT_READ = 3;
    private static final int EXIT_UNEXPECTED = 1;

    static void main() {
        PathProvider pathProvider = new SystemTempPathProvider("lab01", "timestamp.txt");
        FileIO fileIO = new NioFileIO();
        DateTimeProvider clock = new SystemDateTimeProvider();
        TimeFormatter formatter = new TimeFormatter();

        final Path target;
        try {
            target = pathProvider.getPath();
        } catch (AccessDeniedException e) {
            LOG.log(Level.SEVERE, () -> "Access denied while preparing path: " + e.getFile());
            System.exit(EXIT_PATH); return;
        } catch (FileSystemException e) {
            LOG.log(Level.SEVERE, () -> "File system error while preparing path: " + e.getMessage());
            System.exit(EXIT_PATH); return;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, () -> "I/O error while preparing path: " + e.getMessage());
            System.exit(EXIT_PATH); return;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e, () -> "Unexpected error while preparing path: " + e.getMessage());
            System.exit(EXIT_UNEXPECTED); return;
        }

        String payload = formatter.format(clock.now());

        try {
            fileIO.writeString(target, payload);
        } catch (AccessDeniedException e) {
            LOG.log(Level.SEVERE, () -> "Access denied while writing: " + e.getFile());
            System.exit(EXIT_WRITE); return;
        } catch (NoSuchFileException e) {
            LOG.log(Level.SEVERE, () -> "Missing directory for: " + e.getFile());
            System.exit(EXIT_WRITE); return;
        } catch (FileSystemException e) {
            LOG.log(Level.SEVERE, () -> "File system error while writing: " + e.getMessage());
            System.exit(EXIT_WRITE); return;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, () -> "I/O error while writing: " + e.getMessage());
            System.exit(EXIT_WRITE); return;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e, () -> "Unexpected error while writing: " + e.getMessage());
            System.exit(EXIT_UNEXPECTED); return;
        }

        final String content;
        try {
            content = fileIO.readString(target);
        } catch (NoSuchFileException e) {
            LOG.log(Level.SEVERE, () -> "File not found while reading: " + e.getFile());
            System.exit(EXIT_READ); return;
        } catch (AccessDeniedException e) {
            LOG.log(Level.SEVERE, () -> "Access denied while reading: " + e.getFile());
            System.exit(EXIT_READ); return;
        } catch (FileSystemException e) {
            LOG.log(Level.SEVERE, () -> "File system error while reading: " + e.getMessage());
            System.exit(EXIT_READ); return;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, () -> "I/O error while reading: " + e.getMessage());
            System.exit(EXIT_READ); return;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e, () -> "Unexpected error while reading: " + e.getMessage());
            System.exit(EXIT_UNEXPECTED); return;
        }

        LOG.info(() -> content);
        System.exit(EXIT_OK);
    }
}