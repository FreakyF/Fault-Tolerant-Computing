package com.fudala.domain;

public record TransmissionResult(
        String originalData,
        String encoded,
        String received,
        boolean channelIntroducedError,
        DecodingOutcome decodingOutcome
) {
}