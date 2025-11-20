package com.fudala;

public record TaylorSinCalculator(int maxIterations, double tolerance)
        implements TrigFunctionCalculator {
    public TaylorSinCalculator() {
        this(20, 1e-15);
    }

    @Override
    public double calculate(double x) {
        var term = x;
        var sum = x;
        var n = 1;
        while (n < maxIterations && Math.abs(term) > tolerance) {
            term *= -x * x / ((2.0 * n) * (2.0 * n + 1.0));
            sum += term;
            n++;
        }
        return sum;
    }
}
