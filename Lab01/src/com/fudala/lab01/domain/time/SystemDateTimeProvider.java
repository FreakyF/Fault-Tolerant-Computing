package com.fudala.lab01.domain.time;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class SystemDateTimeProvider implements DateTimeProvider {
    @Override
    public ZonedDateTime now() {
        try {
            ZoneId zone = ZoneId.systemDefault();
            return ZonedDateTime.now(zone);
        } catch (DateTimeException | SecurityException _) {
            return ZonedDateTime.now(ZoneOffset.UTC);
        }
    }
}