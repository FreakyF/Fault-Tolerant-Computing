package com.fudala.domain;

public sealed interface ErrorControlCode permits HammingCode, CrcCode {
    String name();
    String encode(String dataBits);
    DecodingOutcome decode(String receivedBits);
}