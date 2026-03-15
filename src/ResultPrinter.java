import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class ResultPrinter {

    public static void printRunStart(int run) {
        System.out.println("Run " + run);
    }

    public static void printIteration(int iter, double sse) {
        System.out.println("Iteration " + iter + ": SSE = " + sse);
    }

    public static void printBestRun(int bestRun, double bestSSE) {
        System.out.println("Best Run: " + bestRun + ": SSE = " + bestSSE);
    }


    public static void setupOutputFile(File inputFile) {
        /**
         * Redirects System.out to a .txt file named after the input dataset.
         * Example: iris_bezdek.txt → iris_bezdek_out.txt
         */
        try {
            String name = inputFile.getName();
            int dotIndex = name.lastIndexOf(".");
            if (dotIndex > 0) {
                name = name.substring(0, dotIndex);
            }

            String outName = name + "_out.txt";

            PrintStream fileOut = new PrintStream(new FileOutputStream(outName));
            System.setOut(fileOut);

        } catch (IOException e) {
            System.err.println("Error: cannot create output file");
            System.exit(1);
        }
    }
}
