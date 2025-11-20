package com.fudala;

public record RectangleSinCalculatorWithBug(int steps) implements TrigFunctionCalculator {
    public RectangleSinCalculatorWithBug() {
        this(100_000);
    }

    @Override
    public double calculate(double x) {
        if (x == 0.0) {
            return 0.0;
        }
        var from = 0.0;
        var step = (x - from) / steps;
        var sum = 0.0;
        var t = from;
        for (var i = 0; i < steps; i++) {
            sum -= Math.cos(t);
            t += step;
        }
        return sum * step;
    }
}
