package com.fudala.application;

import com.fudala.domain.DecodingOutcome;
import com.fudala.domain.ErrorControlCode;
import com.fudala.domain.RandomBitErrorChannel;
import com.fudala.domain.TransmissionResult;

import java.util.Objects;

public final class TransmissionSimulator {

    private static final int DEFAULT_TEST_PACKET_COUNT = 1000;

    public TransmissionResult simulateSingle(
            ErrorControlCode code,
            RandomBitErrorChannel channel,
            String dataBits
    ) {
        Objects.requireNonNull(code, "code must not be null");
        Objects.requireNonNull(channel, "channel must not be null");
        requireBinary(dataBits, "dataBits");

        var encoded = code.encode(dataBits);
        var received = channel.transmit(encoded);
        DecodingOutcome outcome = code.decode(received);
        var errorIntroduced = !encoded.equals(received);

        return new TransmissionResult(dataBits, encoded, received, errorIntroduced, outcome);
    }

    public TransmissionStatistics runTest(
            ErrorControlCode code,
            RandomBitErrorChannel channel,
            String dataBits,
            int packetCount
    ) {
        Objects.requireNonNull(code, "code must not be null");
        Objects.requireNonNull(channel, "channel must not be null");
        requireBinary(dataBits, "dataBits");
        if (packetCount <= 0) {
            throw new IllegalArgumentException("packetCount must be positive");
        }

        var total = 0;
        var channelErrors = 0;
        var detectedErrors = 0;
        var correctedErrors = 0;
        var undetectedErrors = 0;
        var falseAlarms = 0;

        for (var i = 0; i < packetCount; i++) {
            var encoded = code.encode(dataBits);
            var received = channel.transmit(encoded);
            DecodingOutcome outcome = code.decode(received);
            var hasChannelError = !encoded.equals(received);

            total++;

            if (hasChannelError) {
                channelErrors++;
                if (outcome.errorDetected()) {
                    detectedErrors++;
                } else {
                    undetectedErrors++;
                }
            } else {
                if (outcome.errorDetected()) {
                    falseAlarms++;
                }
            }
            if (outcome.errorCorrected()) {
                correctedErrors++;
            }
        }

        return new TransmissionStatistics(
                total,
                channelErrors,
                detectedErrors,
                correctedErrors,
                undetectedErrors,
                falseAlarms
        );
    }

    public TransmissionStatistics runDefaultTest(
            ErrorControlCode code,
            RandomBitErrorChannel channel,
            String dataBits
    ) {
        return runTest(code, channel, dataBits, DEFAULT_TEST_PACKET_COUNT);
    }

    private void requireBinary(String bits, @SuppressWarnings("SameParameterValue") String label) {
        if (bits == null || bits.isEmpty() || !bits.matches("[01]+")) {
            throw new IllegalArgumentException(label + " must be a non-empty binary string");
        }
    }
}