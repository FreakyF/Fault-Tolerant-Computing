package com.fudala.domain;

import java.util.Objects;

public record DecodingOutcome(String dataBits, boolean errorDetected, boolean errorCorrected) {
    public DecodingOutcome {
        Objects.requireNonNull(dataBits, "dataBits must not be null");
    }
}