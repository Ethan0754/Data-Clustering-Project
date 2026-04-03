import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class ResultPrinter {

    public static void printRunStart(int run) {
        System.out.println("Run " + run);
    }

    public static void printIteration(int iter, double sse) {
        System.out.println("Iteration " + iter + ": SSE = " + sse);
    }

    public static void printInternalValidity(int k, double ch, double sw) {
        System.out.println("K - " + k + " (CH): " + ch + " (SW): " + sw);
    }

    public static void printKMeansTime(long duration) {
        System.out.println("KMeans All Runs Time Taken: " + duration);
    }

    public static void printCHTime(long duration) {
        System.out.println("CH Time Taken: " + duration);
    }

    public static void printSWTime(long duration) {
        System.out.println("SW Time Taken: " + duration);
    }


    public static void printBestRun(int bestRun, double bestSSE) {
        System.out.println("Best Run: " + bestRun + ": SSE = " + bestSSE);
    }

    public static void setupOutputFile(File inputFile) {
        try {
            String name = inputFile.getName();
            int dotIndex = name.lastIndexOf(".");
            if (dotIndex > 0) {
                name = name.substring(0, dotIndex);
            }

            String outName = name + "_out.txt";

            PrintStream fileOut = new PrintStream(outName);
            System.setOut(fileOut);

        } catch (IOException e) {
            System.err.println("Error: cannot create output file");
            System.exit(1);
        }
    }

    public static void writeCsvHeader(String outputFile) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Dataset,Normalization,Initialization,K,Best Run,Best Final SSE,CH");
        } catch (IOException e) {
            System.err.println("Error: cannot create CSV file: " + outputFile);
            System.exit(1);
        }
    }

    public static void appendCsvRow(
            String outputFile,
            String dataset,
            String normalization,
            String initialization,
            int k,
            int bestRun,
            double finalSSE,
            double ch,
            double sw) {

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile, true))) {
            writer.printf(
                    "%s,%s,%s,%d,%d,%.6f,%.6f,%.6f%n",
                    dataset,
                    normalization,
                    initialization,
                    k,
                    bestRun,
                    finalSSE,
                    ch,
                    sw
            );
        } catch (IOException e) {
            System.err.println("Error: cannot write to CSV file: " + outputFile);
            System.exit(1);
        }
    }
}
