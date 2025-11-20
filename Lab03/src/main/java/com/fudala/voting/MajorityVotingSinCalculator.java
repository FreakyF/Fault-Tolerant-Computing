package com.fudala.voting;

import com.fudala.math.TrigFunctionCalculator;

import java.util.Arrays;
import java.util.List;

public record MajorityVotingSinCalculator(List<TrigFunctionCalculator> calculators, double epsilon) {
    public MajorityVotingSinCalculator(List<TrigFunctionCalculator> calculators) {
        this(calculators, 1e-9);
    }

    public MajorityVotingSinCalculator {
        if (calculators.size() != 3) {
            throw new IllegalArgumentException("Exactly three calculators are required");
        }
        calculators = List.copyOf(calculators);
    }

    public VotingResult calculate(double x) {
        var values = new double[3];
        for (var i = 0; i < calculators.size(); i++) {
            values[i] = calculators.get(i).calculate(x);
        }
        var v1 = values[0];
        var v2 = values[1];
        var v3 = values[2];

        if (areClose(v1, v2) || areClose(v1, v3)) {
            return new VotingResult(v1, true, values);
        }
        if (areClose(v2, v3)) {
            return new VotingResult(v2, true, values);
        }
        return new VotingResult(Double.NaN, false, values);
    }

    private boolean areClose(double a, double b) {
        return Math.abs(a - b) <= epsilon;
    }

    public record VotingResult(double value, boolean consensus, double[] allValues) {

        public double valueOrThrow() {
            if (!consensus) {
                throw new IllegalStateException("No consensus");
            }
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof VotingResult(double value1, boolean consensus1, double[] values))) return false;
            return Double.compare(value1, value) == 0
                    && consensus == consensus1
                    && Arrays.equals(allValues, values);
        }

        @Override
        public int hashCode() {
            var result = Double.hashCode(value);
            result = 31 * result + Boolean.hashCode(consensus);
            result = 31 * result + Arrays.hashCode(allValues);
            return result;
        }

        @Override
        public String toString() {
            return "VotingResult[value=%s, consensus=%s, allValues=%s]"
                    .formatted(value, consensus, Arrays.toString(allValues));
        }
    }
}
