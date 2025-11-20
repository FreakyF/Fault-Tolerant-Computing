package com.fudala.math;

public final class LibrarySinCalculator implements TrigFunctionCalculator {
    @Override
    public double calculate(double x) {
        return Math.sin(x);
    }
}