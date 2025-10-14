package com.fudala.lab01.infrastructure.io;

import com.fudala.lab01.domain.io.FileIO;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public final class NioFileIO implements FileIO {

    @Override
    public void writeString(Path target, String content) throws IOException {
        if (target == null) throw new NullPointerException("target must not be null");
        if (content == null) content = "";

        final Path dir;
        try {
            dir = target.toAbsolutePath().getParent();
        } catch (SecurityException se) {
            throw new IOException("Cannot resolve target path.", se);
        }
        if (dir == null) throw new IOException("Target has no parent directory: " + target);

        Files.createDirectories(dir);

        Path tmp = null;
        try {
            tmp = Files.createTempFile(dir, ".write-", ".tmp");
            Files.writeString(tmp, content, StandardCharsets.UTF_8);
            try (FileChannel ch = FileChannel.open(tmp, WRITE)) { ch.force(true); }
            moveWithAtomicFallback(tmp, target);
        } catch (IOException e) {
            deleteQuietly(tmp);
            throw e;
        } catch (SecurityException se) {
            deleteQuietly(tmp);
            throw new IOException("Security manager blocked file I/O.", se);
        }
    }

    @Override
    public String readString(Path path) throws IOException {
        if (path == null) throw new NullPointerException("path must not be null");
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (java.nio.charset.MalformedInputException | java.nio.charset.UnmappableCharacterException e) {
            throw new IOException("Invalid UTF-8 content: " + path, e);
        } catch (SecurityException se) {
            throw new IOException("Security manager blocked file I/O.", se);
        }
    }

    private static void moveWithAtomicFallback(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, ATOMIC_MOVE, REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException _) {
            Files.move(source, target, REPLACE_EXISTING);
        }
    }
    private static void deleteQuietly(Path p) {
        if (p == null) return;
        try {
            Files.deleteIfExists(p);
        } catch (IOException | SecurityException _) {
            // Best-effort cleanup of temp file. Safe to ignore:
            // failure to delete here does not affect correctness.
        }
    }
}
