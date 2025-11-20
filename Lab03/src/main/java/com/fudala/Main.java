package com.fudala;

import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final String CONSENSUS_TEMPLATE = """
            x = %f
            library      = %f
            taylor       = %f
            rectangleBug = %f
            majority     = %f
            consensus
            """;

    private static final String NO_CONSENSUS_TEMPLATE = """
            x = %f
            library      = %f
            taylor       = %f
            rectangleBug = %f
            no consensus
            """;

    private Main() {
        throw new AssertionError("Cannot instantiate Main");
    }

    static {
        var root = Logger.getLogger("");
        for (var handler : root.getHandlers()) {
            handler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord logRecord) {
                    return logRecord.getMessage() + System.lineSeparator();
                }
            });
        }
    }

    static void main() {
        var library = new LibrarySinCalculator();
        var taylor = new TaylorSinCalculator();
        var rectangleWithBug = new RectangleSinCalculatorWithBug();

        var votingCalculator = new MajorityVotingSinCalculator(
                List.of(library, taylor, rectangleWithBug)
        );

        var inputs = List.of(
                0.0,
                Math.PI / 6,
                Math.PI / 4,
                Math.PI / 2,
                Math.PI
        );

        for (var x : inputs) {
            logVotingResult(x, library, taylor, rectangleWithBug, votingCalculator);
        }

        demonstrateNoConsensus(library, taylor, rectangleWithBug);
    }

    private static void logVotingResult(
            double x,
            TrigFunctionCalculator library,
            TrigFunctionCalculator taylor,
            TrigFunctionCalculator rectangleWithBug,
            MajorityVotingSinCalculator votingCalculator
    ) {
        var result = votingCalculator.calculate(x);

        var libraryValue = library.calculate(x);
        var taylorValue = taylor.calculate(x);
        var rectangleValue = rectangleWithBug.calculate(x);

        String message;
        if (result.consensus()) {
            var majority = result.valueOrThrow();
            message = CONSENSUS_TEMPLATE.formatted(
                    x,
                    libraryValue,
                    taylorValue,
                    rectangleValue,
                    majority
            );
        } else {
            message = NO_CONSENSUS_TEMPLATE.formatted(
                    x,
                    libraryValue,
                    taylorValue,
                    rectangleValue
            );
        }

        LOGGER.info(message);
    }

    private static void demonstrateNoConsensus(
            TrigFunctionCalculator library,
            TrigFunctionCalculator taylor,
            TrigFunctionCalculator rectangleWithBug
    ) {
        var strictVotingCalculator = new MajorityVotingSinCalculator(
                List.of(library, taylor, rectangleWithBug),
                1e-20
        );

        var x = Math.PI;
        logVotingResult(x, library, taylor, rectangleWithBug, strictVotingCalculator);
    }
}
