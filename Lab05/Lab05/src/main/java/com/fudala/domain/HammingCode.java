package com.fudala.domain;

public final class HammingCode implements ErrorControlCode {

    private static final int DATA_LENGTH = 4;
    private static final int CODEWORD_LENGTH = 7;

    @Override
    public String name() {
        return "Hamming(7,4)";
    }

    @Override
    public String encode(String dataBits) {
        requireDataBits(dataBits);
        var d = new int[DATA_LENGTH];
        for (var i = 0; i < DATA_LENGTH; i++) {
            d[i] = dataBits.charAt(i) - '0';
        }

        var p1 = d[0] ^ d[1] ^ d[3];
        var p2 = d[0] ^ d[2] ^ d[3];
        var p4 = d[1] ^ d[2] ^ d[3];

        return new String(new char[] {
                (char) ('0' + p1),
                (char) ('0' + p2),
                (char) ('0' + d[0]),
                (char) ('0' + p4),
                (char) ('0' + d[1]),
                (char) ('0' + d[2]),
                (char) ('0' + d[3])
        });
    }

    @Override
    public DecodingOutcome decode(String receivedBits) {
        requireCodeword(receivedBits);
        var c = new int[CODEWORD_LENGTH];
        for (var i = 0; i < CODEWORD_LENGTH; i++) {
            c[i] = receivedBits.charAt(i) - '0';
        }

        var s1 = c[0] ^ c[2] ^ c[4] ^ c[6];
        var s2 = c[1] ^ c[2] ^ c[5] ^ c[6];
        var s4 = c[3] ^ c[4] ^ c[5] ^ c[6];
        var errorPos = s1 + 2 * s2 + 4 * s4;

        var detected = errorPos != 0;
        var corrected = false;

        if (errorPos > 0 && errorPos <= CODEWORD_LENGTH) {
            c[errorPos - 1] ^= 1;
            corrected = true;
        }

        var data = "%d%d%d%d".formatted(c[2], c[4], c[5], c[6]);

        return new DecodingOutcome(data, detected, corrected);
    }

    private void requireDataBits(String dataBits) {
        if (dataBits == null || dataBits.length() != DATA_LENGTH || !dataBits.matches("[01]+")) {
            throw new IllegalArgumentException("Hamming(7,4) expects " + DATA_LENGTH + " data bits");
        }
    }

    private void requireCodeword(String receivedBits) {
        if (receivedBits == null || receivedBits.length() != CODEWORD_LENGTH || !receivedBits.matches("[01]+")) {
            throw new IllegalArgumentException("Hamming(7,4) expects " + CODEWORD_LENGTH + "-bit code word");
        }
    }
}