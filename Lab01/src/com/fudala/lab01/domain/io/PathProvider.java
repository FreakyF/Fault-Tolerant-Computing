package com.fudala.lab01.domain.io;

import java.io.IOException;
import java.nio.file.Path;

public interface PathProvider {
    Path getPath() throws IOException;
}