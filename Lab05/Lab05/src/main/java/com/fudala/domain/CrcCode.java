package com.fudala.domain;

import java.util.Objects;

public final class CrcCode implements ErrorControlCode {

    private static final String BINARY_PATTERN = "[01]+";
    private static final String DEFAULT_POLYNOMIAL = "100000111";

    private final String polynomial;
    private final int degree;

    public CrcCode() {
        this(DEFAULT_POLYNOMIAL);
    }

    public CrcCode(String polynomial) {
        this.polynomial = validatePolynomial(polynomial);
        this.degree = this.polynomial.length() - 1;
    }

    @Override
    public String name() {
        return "CRC-8[" + polynomial + "]";
    }

    @Override
    public String encode(String dataBits) {
        requireDataBits(dataBits);
        var crc = calculateCrc(dataBits);
        return dataBits + crc;
    }

    @Override
    public DecodingOutcome decode(String receivedBits) {
        requireFrame(receivedBits);
        var valid = verify(receivedBits);
        var data = receivedBits.substring(0, receivedBits.length() - degree);
        var detected = !valid;
        return new DecodingOutcome(data, detected, false);
    }

    private String calculateCrc(String data) {
        var r = degree;
        var padded = (data + "0".repeat(r)).toCharArray();
        var poly = polynomial.toCharArray();

        for (var i = 0; i < data.length(); i++) {
            if (padded[i] == '1') {
                for (var j = 0; j < poly.length; j++) {
                    padded[i + j] = xorBit(padded[i + j], poly[j]);
                }
            }
        }

        var crc = new StringBuilder(r);
        for (var i = padded.length - r; i < padded.length; i++) {
            crc.append(padded[i]);
        }
        return crc.toString();
    }

    private boolean verify(String frame) {
        var r = degree;
        var padded = frame.toCharArray();
        var poly = polynomial.toCharArray();
        var dataLength = frame.length() - r;

        for (var i = 0; i < dataLength; i++) {
            if (padded[i] == '1') {
                for (var j = 0; j < poly.length; j++) {
                    padded[i + j] = xorBit(padded[i + j], poly[j]);
                }
            }
        }

        for (var i = padded.length - r; i < padded.length; i++) {
            if (padded[i] != '0') {
                return false;
            }
        }
        return true;
    }

    private char xorBit(char a, char b) {
        return a == b ? '0' : '1';
    }

    private String validatePolynomial(String polynomial) {
        Objects.requireNonNull(polynomial, "polynomial must not be null");
        if (polynomial.length() < 2 || !polynomial.matches(BINARY_PATTERN)) {
            throw new IllegalArgumentException("Polynomial must be a binary string of length at least 2");
        }
        if (polynomial.charAt(0) != '1' || polynomial.charAt(polynomial.length() - 1) != '1') {
            throw new IllegalArgumentException("Polynomial must start and end with 1");
        }
        return polynomial;
    }

    private void requireDataBits(String dataBits) {
        if (dataBits == null || dataBits.isEmpty() || !dataBits.matches(BINARY_PATTERN)) {
            throw new IllegalArgumentException("Data must be a non-empty binary string");
        }
    }

    private void requireFrame(String frame) {
        if (frame == null || frame.length() <= degree || !frame.matches(BINARY_PATTERN)) {
            throw new IllegalArgumentException("Frame must be a binary string longer than polynomial degree");
        }
    }
}