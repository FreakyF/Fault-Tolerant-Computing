package com.fudala.application;

import com.fudala.domain.DecodingOutcome;
import com.fudala.domain.ErrorControlCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record CodeComparisonService(List<ErrorControlCode> codes) {

    public CodeComparisonService {
        Objects.requireNonNull(codes, "codes must not be null");
        if (codes.isEmpty()) {
            throw new IllegalArgumentException("codes must not be empty");
        }
        codes = List.copyOf(codes);
    }

    public List<CodeTestCaseResult> runExample(String dataBits) {
        requireBinary(dataBits, "dataBits");
        var results = new ArrayList<CodeTestCaseResult>();

        for (var code : codes) {
            var encoded = code.encode(dataBits);

            var encodedWithSingleError = flipBit(encoded, 2);
            var encodedWithBurstError = flipRange(encoded, 1, 3);

            DecodingOutcome outcomeNoError = code.decode(encoded);
            DecodingOutcome outcomeSingleError = code.decode(encodedWithSingleError);
            DecodingOutcome outcomeBurstError = code.decode(encodedWithBurstError);

            var result = new CodeTestCaseResult(
                    code.name(),
                    dataBits,
                    encoded,
                    encodedWithSingleError,
                    encodedWithBurstError,
                    outcomeNoError,
                    outcomeSingleError,
                    outcomeBurstError
            );
            results.add(result);
        }
        return List.copyOf(results);
    }

    private String flipBit(String bits, @SuppressWarnings("SameParameterValue") int index) {
        requireBinary(bits, "bits");
        if (index < 0 || index >= bits.length()) {
            throw new IllegalArgumentException("index out of bounds: " + index);
        }
        var array = bits.toCharArray();
        array[index] = array[index] == '0' ? '1' : '0';
        return new String(array);
    }

    private String flipRange(String bits, @SuppressWarnings("SameParameterValue") int startInclusive,
                             @SuppressWarnings("SameParameterValue") int length) {
        requireBinary(bits, "bits");
        if (startInclusive < 0 || length < 1 || startInclusive + length > bits.length()) {
            throw new IllegalArgumentException("range out of bounds");
        }
        var array = bits.toCharArray();
        for (var i = 0; i < length; i++) {
            var index = startInclusive + i;
            array[index] = array[index] == '0' ? '1' : '0';
        }
        return new String(array);
    }

    private void requireBinary(String bits, String label) {
        if (bits == null || bits.isEmpty() || !bits.matches("[01]+")) {
            throw new IllegalArgumentException(label + " must be a non-empty binary string");
        }
    }
}
