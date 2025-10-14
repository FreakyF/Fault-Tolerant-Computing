package com.fudala.lab01.domain.time;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class TimeFormatter {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss VV (O)");

    public String format(ZonedDateTime zdt) {
        Objects.requireNonNull(zdt, "zdt");
        try {
            return zdt.format(FORMATTER);
        } catch (DateTimeException _) {
            return zdt.toOffsetDateTime().toString();
        }
    }
}