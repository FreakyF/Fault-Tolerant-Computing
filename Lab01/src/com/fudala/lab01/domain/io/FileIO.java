package com.fudala.lab01.domain.io;

import java.io.IOException;
import java.nio.file.Path;

public interface FileIO {
    void writeString(Path path, String content) throws IOException;
    String readString(Path path) throws IOException;
}
