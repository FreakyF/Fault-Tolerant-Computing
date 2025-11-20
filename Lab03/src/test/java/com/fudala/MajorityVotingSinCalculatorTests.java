package com.fudala;

import com.fudala.math.LibrarySinCalculator;
import com.fudala.math.RectangleSinCalculatorWithBug;
import com.fudala.math.TaylorSinCalculator;
import com.fudala.math.TrigFunctionCalculator;
import com.fudala.voting.MajorityVotingSinCalculator;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ExtractMethodRecommender")
public final class MajorityVotingSinCalculatorTests {
    @Test
    public void allImplementationsAgreeForZero() {
        var library = new LibrarySinCalculator();
        var taylor = new TaylorSinCalculator();
        var rectangleWithBug = new RectangleSinCalculatorWithBug();

        var votingCalculator = new MajorityVotingSinCalculator(
                List.of(library, taylor, rectangleWithBug)
        );

        var x = 0.0;
        var result = votingCalculator.calculate(x);

        assertTrue(result.consensus());
        assertEquals(0.0, result.value(), 1e-9);
        assertEquals(library.calculate(x), result.value(), 1e-9);
        assertEquals(taylor.calculate(x), result.value(), 1e-9);
        assertEquals(rectangleWithBug.calculate(x), result.value(), 1e-9);
    }

    @Test
    public void majorityVoteMasksBugForTypicalValue() {
        var library = new LibrarySinCalculator();
        var taylor = new TaylorSinCalculator();
        var rectangleWithBug = new RectangleSinCalculatorWithBug();

        var votingCalculator = new MajorityVotingSinCalculator(
                List.of(library, taylor, rectangleWithBug)
        );

        var x = Math.PI / 3;
        var result = votingCalculator.calculate(x);

        assertTrue(result.consensus());
        var expected = Math.sin(x);
        assertEquals(expected, result.value(), 1e-6);
        assertEquals(library.calculate(x), result.value(), 1e-9);
        assertEquals(taylor.calculate(x), result.value(), 1e-6);
        assertNotEquals(expected, rectangleWithBug.calculate(x), 1e-3);
    }

    @Test
    public void bugIsVisibleInSingleImplementation() {
        var library = new LibrarySinCalculator();
        var taylor = new TaylorSinCalculator();
        var rectangleWithBug = new RectangleSinCalculatorWithBug();

        var x = Math.PI / 4;
        var correct = Math.sin(x);
        var buggy = rectangleWithBug.calculate(x);

        assertEquals(correct, library.calculate(x), 1e-9);
        assertEquals(correct, taylor.calculate(x), 1e-6);
        assertTrue(Math.abs(correct + buggy) < 1e-3);
    }

    @Test
    public void noConsensusWhenAllResultsDiffer() {
        var calculator1 = new TrigFunctionCalculator() {
            @Override
            public double calculate(double x) {
                return 1.0;
            }
        };
        var calculator2 = new TrigFunctionCalculator() {
            @Override
            public double calculate(double x) {
                return 2.0;
            }
        };
        var calculator3 = new TrigFunctionCalculator() {
            @Override
            public double calculate(double x) {
                return 3.0;
            }
        };

        var votingCalculator = new MajorityVotingSinCalculator(
                List.of(calculator1, calculator2, calculator3),
                1e-12
        );

        var result = votingCalculator.calculate(0.5);

        assertFalse(result.consensus());
        assertTrue(Double.isNaN(result.value()));
    }
}
