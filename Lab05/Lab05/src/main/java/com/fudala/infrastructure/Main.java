package com.fudala.infrastructure;
import com.fudala.application.CodeComparisonService;
import com.fudala.application.CodeTestCaseResult;
import com.fudala.domain.CrcCode;
import com.fudala.domain.DecodingOutcome;
import com.fudala.domain.ErrorControlCode;
import com.fudala.domain.HammingCode;

import java.util.List;

public final class Main {

    void main() {
        ErrorControlCode hamming = new HammingCode();
        ErrorControlCode crc = new CrcCode("1011");

        var service = new CodeComparisonService(List.of(hamming, crc));

        var dataBits = "1011";

        var results = service.runExample(dataBits);

        for (var result : results) {
            printResult(result);
        }
    }

    private void printResult(CodeTestCaseResult result) {
        var summary = """
                Code: %s
                Original data: %s
                Encoded: %s
                Encoded with single-bit error: %s
                Encoded with burst error: %s
                Outcome no error: %s
                Outcome single-bit error: %s
                Outcome burst error: %s
                """.formatted(
                result.codeName(),
                result.originalData(),
                result.encoded(),
                result.encodedWithSingleError(),
                result.encodedWithBurstError(),
                formatOutcome(result.outcomeNoError()),
                formatOutcome(result.outcomeSingleError()),
                formatOutcome(result.outcomeBurstError())
        );

        IO.println(summary);
    }

    private String formatOutcome(DecodingOutcome outcome) {
        return "data=%s, errorDetected=%s, errorCorrected=%s".formatted(
                outcome.dataBits(),
                outcome.errorDetected(),
                outcome.errorCorrected()
        );
    }
}