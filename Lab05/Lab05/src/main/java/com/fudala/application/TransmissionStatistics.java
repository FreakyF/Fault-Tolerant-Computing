package com.fudala.application;

public record TransmissionStatistics(
        int totalPackets,
        int channelErrors,
        int detectedErrors,
        int correctedErrors,
        int undetectedErrors,
        int falseAlarms
) {
}
