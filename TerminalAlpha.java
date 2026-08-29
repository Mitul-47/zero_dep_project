import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;

/**
 * TerminalAlpha
 * 
 * A high-performance, zero-dependency financial backtesting engine.
 * Demonstrates mechanical sympathy, OS-level optimizations, and zero-allocation processing.
 */
public class TerminalAlpha {

    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }

        boolean isOptimize = false;
        String generateRows = null;
        String filePath = null;
        String strategyName = null;
        boolean isBenchmark = false;
        boolean isTest = false;

        // Simple CLI Parser
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--generate":
                    if (i + 1 < args.length) generateRows = args[++i];
                    break;
                case "--benchmark":
                    isBenchmark = true;
                    if (i + 1 < args.length) filePath = args[++i];
                    break;
                case "--file":
                    if (i + 1 < args.length) filePath = args[++i];
                    break;
                case "--strategy":
                    if (i + 1 < args.length) strategyName = args[++i];
                    break;
                case "--optimize":
                    isOptimize = true;
                    break;
                case "--test":
                    isTest = true;
                    break;
                default:
                    // Ignore unknown args for now
                    break;
            }
        }

        // Route commands
        if (generateRows != null) {
            int rows = Integer.parseInt(generateRows);
            SyntheticDataGenerator.generate("benchmark.csv", rows);
        } else if (isTest) {
            System.out.println("[STAGE 4 Placeholder] Running native assertions (-ea)...");
        } else if (isBenchmark && filePath != null && strategyName != null) {
            System.out.printf("[STAGE 2 & 3 Placeholder] Executing End-to-End Stress Test...\n");
            System.out.printf("File: %s | Strategy: %s\n", filePath, strategyName);
        } else if (isOptimize && filePath != null && strategyName != null) {
            System.out.printf("[STAGE 4 Placeholder] Running ForkJoinPool optimization...\n");
            System.out.printf("File: %s | Strategy: %s\n", filePath, strategyName);
        } else if (filePath != null && strategyName != null) {
            System.out.printf("[STAGE 3 & 4 Placeholder] Running backtest and rendering UI...\n");
            System.out.printf("File: %s | Strategy: %s\n", filePath, strategyName);
        } else {
            printHelp();
        }
    }

    private static void printHelp() {
        System.out.println("TerminalAlpha - Zero Dependency Financial Backtesting Engine");
        System.out.println("Usage:");
        System.out.println("  --generate <rows>                                 (Generates benchmark.csv)");
        System.out.println("  --benchmark <path> --strategy <name>              (End-to-End Stress Test)");
        System.out.println("  --file <path> --strategy <name>                   (Run & Render UI)");
        System.out.println("  --optimize --file <path> --strategy <name>        (Parallel Optimization)");
        System.out.println("  --test                                            (Native Assertions)");
    }

    // =================================================================================
    // STAGE 1: Synthetic Data Generator (The GBM Engine)
    // =================================================================================
    static class SyntheticDataGenerator {
        private static final double MU = 0.0001; // Drift
        private static final double SIGMA = 0.002; // Volatility
        private static final double INITIAL_PRICE = 100.0;
        private static final long START_TIMESTAMP = 1700000000000L; 
        private static final int BUFFER_SIZE = 1024 * 1024 * 64; // 64 MB Direct NIO Buffer

        public static void generate(String filePath, int numRows) {
            System.out.println("Initializing GBM Engine...");
            System.out.println("Target Rows: " + numRows);
            System.out.println("Output File: " + filePath);

            long startTime = System.nanoTime();
            Random random = new Random(42); // Fixed seed
            Path path = Paths.get(filePath);

            try (FileChannel channel = FileChannel.open(path, 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.WRITE, 
                    StandardOpenOption.TRUNCATE_EXISTING)) {

                // Allocate a direct memory buffer to bypass JVM heap overhead when writing to OS page cache
                ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
                double currentPrice = INITIAL_PRICE;
                long currentTimestamp = START_TIMESTAMP;

                for (int i = 0; i < numRows; i++) {
                    // Geometric Brownian Motion step
                    double z = random.nextGaussian();
                    currentPrice = currentPrice * Math.exp((MU - (SIGMA * SIGMA) / 2.0) + SIGMA * z);
                    
                    int volume = 100 + random.nextInt(900);
                    currentTimestamp += 1000; // 1 second intervals

                    // Format: Timestamp,Price,Volume\n
                    // Zero-allocation ASCII encoding to avoid GC pauses during huge file generation
                    putLongAscii(buffer, currentTimestamp);
                    buffer.put((byte) ',');
                    putDoubleAscii(buffer, currentPrice);
                    buffer.put((byte) ',');
                    putIntAscii(buffer, volume);
                    buffer.put((byte) '\n');

                    // Flush to disk if buffer is nearly full
                    if (buffer.remaining() < 128) {
                        buffer.flip();
                        channel.write(buffer);
                        buffer.clear();
                    }
                }

                // Final flush
                if (buffer.position() > 0) {
                    buffer.flip();
                    channel.write(buffer);
                }

            } catch (IOException e) {
                System.err.println("I/O Error during data generation: " + e.getMessage());
                System.exit(1);
            }

            long endTime = System.nanoTime();
            double durationSec = (endTime - startTime) / 1_000_000_000.0;
            System.out.printf("Generation complete. Wrote %d rows in %.3f seconds.\n", numRows, durationSec);
        }

        // --- Zero-Allocation ASCII Encoders ---

        private static void putLongAscii(ByteBuffer buffer, long value) {
            if (value == 0) {
                buffer.put((byte) '0');
                return;
            }
            if (value < 0) {
                buffer.put((byte) '-');
                value = -value;
            }
            long temp = value;
            int length = 0;
            while (temp > 0) {
                temp /= 10;
                length++;
            }
            int startPos = buffer.position();
            buffer.position(startPos + length);
            int currentPos = startPos + length - 1;
            while (value > 0) {
                long digit = value % 10;
                buffer.put(currentPos--, (byte) ('0' + digit));
                value /= 10;
            }
        }

        private static void putIntAscii(ByteBuffer buffer, int value) {
            putLongAscii(buffer, value);
        }

        private static void putDoubleAscii(ByteBuffer buffer, double value) {
            long intPart = (long) value;
            putLongAscii(buffer, intPart);
            buffer.put((byte) '.');
            
            double fracPart = value - intPart;
            if (fracPart < 0) fracPart = -fracPart;
            
            // Fixed 4 decimal places for price
            int fracInt = (int) Math.round(fracPart * 10000);
            if (fracInt < 1000) buffer.put((byte) '0');
            if (fracInt < 100) buffer.put((byte) '0');
            if (fracInt < 10) buffer.put((byte) '0');
            if (fracInt == 0) {
                buffer.put((byte) '0');
            } else {
                putLongAscii(buffer, fracInt);
            }
        }
    }
}
