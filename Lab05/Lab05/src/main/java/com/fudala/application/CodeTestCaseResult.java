package com.fudala.application;

import com.fudala.domain.DecodingOutcome;

public record CodeTestCaseResult(String codeName, String originalData, String encoded, String encodedWithSingleError,
                                 String encodedWithBurstError, DecodingOutcome outcomeNoError,
                                 DecodingOutcome outcomeSingleError, DecodingOutcome outcomeBurstError) {
}