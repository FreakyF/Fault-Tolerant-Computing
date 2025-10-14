package com.fudala.lab01.domain.time;

import java.time.ZonedDateTime;

public interface DateTimeProvider {
    ZonedDateTime now();
}
