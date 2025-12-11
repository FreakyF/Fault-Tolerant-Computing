package com.fudala.domain;

import java.util.Objects;
import java.util.Random;

public record RandomBitErrorChannel(double bitErrorProbability, Random random) {

    public RandomBitErrorChannel {
        if (bitErrorProbability < 0.0 || bitErrorProbability > 1.0) {
            throw new IllegalArgumentException("bitErrorProbability must be between 0.0 and 1.0");
        }
        Objects.requireNonNull(random, "random must not be null");
    }

    public String transmit(String bits) {
        Objects.requireNonNull(bits, "bits must not be null");
        if (bits.isEmpty()) {
            throw new IllegalArgumentException("bits must not be empty");
        }
        var chars = bits.toCharArray();
        for (var i = 0; i < chars.length; i++) {
            if (random.nextDouble() < bitErrorProbability) {
                chars[i] = chars[i] == '0' ? '1' : '0';
            }
        }
        return new String(chars);
    }
}
