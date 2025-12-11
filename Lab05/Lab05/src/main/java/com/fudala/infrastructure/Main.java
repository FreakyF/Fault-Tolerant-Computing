package com.fudala.infrastructure;

import com.fudala.application.TransmissionSimulator;
import com.fudala.application.TransmissionStatistics;
import com.fudala.domain.CrcCode;
import com.fudala.domain.ErrorControlCode;
import com.fudala.domain.HammingCode;
import com.fudala.domain.RandomBitErrorChannel;
import com.fudala.domain.TransmissionResult;

import java.util.Random;
import java.util.Scanner;

@SuppressWarnings("java:S106")
public final class Main {

    private Main() {
    }

    static void main() {
        var scanner = new Scanner(System.in);
        var simulator = new TransmissionSimulator();
        var random = new Random();

        while (true) {
            System.out.println("Data transmission simulator");
            System.out.println("1) Manual transmission");
            System.out.println("2) Test mode (1000 packets)");
            System.out.println("0) Exit");
            System.out.print("Choose option: ");
            var option = scanner.nextLine().trim();

            if ("0".equals(option)) {
                break;
            }

            var code = chooseCode(scanner);
            if (code != null) {
                var probability = readErrorProbability(scanner);
                var channel = new RandomBitErrorChannel(probability, random);

                switch (option) {
                    case "1" -> runManual(scanner, simulator, code, channel);
                    case "2" -> runTest(scanner, simulator, code, channel);
                    default -> System.out.println("Unknown option");
                }
            }

            System.out.println();
        }
    }

    private static ErrorControlCode chooseCode(Scanner scanner) {
        System.out.println("Choose code:");
        System.out.println("1) Hamming(7,4)");
        System.out.println("2) CRC-8");
        System.out.print("Option: ");
        var choice = scanner.nextLine().trim();

        return switch (choice) {
            case "1" -> new HammingCode();
            case "2" -> new CrcCode();
            default -> {
                System.out.println("Unknown code option");
                yield null;
            }
        };
    }

    private static double readErrorProbability(Scanner scanner) {
        while (true) {
            System.out.print("Bit error probability in percent (e.g. 1, 5, 10): ");
            var line = scanner.nextLine().trim();
            try {
                var percent = Double.parseDouble(line);
                if (percent < 0.0 || percent > 100.0) {
                    System.out.println("Value must be between 0 and 100");
                    continue;
                }
                return percent / 100.0;
            } catch (NumberFormatException _) {
                System.out.println("Invalid number");
            }
        }
    }

    private static void runManual(
            Scanner scanner,
            TransmissionSimulator simulator,
            ErrorControlCode code,
            RandomBitErrorChannel channel
    ) {
        var dataBits = readDataBits(scanner, code);
        TransmissionResult result = simulator.simulateSingle(code, channel, dataBits);

        System.out.println("Original data:   " + result.originalData());
        System.out.println("Encoded:         " + result.encoded());
        System.out.println("After channel:   " + result.received());
        System.out.println("Channel error:   " + (result.channelIntroducedError() ? "YES" : "NO"));
        System.out.println("Decoded data:    " + result.decodingOutcome().dataBits());
        System.out.println("Error detected:  " + result.decodingOutcome().errorDetected());
        System.out.println("Error corrected: " + result.decodingOutcome().errorCorrected());
    }

    private static void runTest(
            Scanner scanner,
            TransmissionSimulator simulator,
            ErrorControlCode code,
            RandomBitErrorChannel channel
    ) {
        var dataBits = readDataBits(scanner, code);
        TransmissionStatistics stats = simulator.runDefaultTest(code, channel, dataBits);

        System.out.println("Test mode: 1000 packets");
        System.out.println("Total packets:          " + stats.totalPackets());
        System.out.println("Packets with errors:    " + stats.channelErrors());
        System.out.println("Detected errors:        " + stats.detectedErrors());
        System.out.println("Corrected errors:       " + stats.correctedErrors());
        System.out.println("Undetected errors:      " + stats.undetectedErrors());
        System.out.println("False alarms (no error, detected): " + stats.falseAlarms());
    }

    private static String readDataBits(Scanner scanner, ErrorControlCode code) {
        while (true) {
            if (code instanceof HammingCode) {
                System.out.print("Enter 4 data bits for Hamming(7,4): ");
            } else {
                System.out.print("Enter binary data for CRC-8: ");
            }
            var data = scanner.nextLine().trim();

            var isHamming = code instanceof HammingCode;
            var isBinary = data.matches("[01]+");
            var lengthOk = !isHamming || data.length() == 4;

            if (!isBinary) {
                System.out.println("Data must be a binary string (0/1 only)");
            } else if (!lengthOk) {
                System.out.println("Hamming(7,4) requires exactly 4 bits");
            } else {
                return data;
            }
        }
    }
}
