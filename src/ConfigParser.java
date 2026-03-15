import java.io.File;

public final class ConfigParser {
    private ConfigParser() {} //Prevents instantiation of configparser class

    public static Config parse(String[] args) {

        if (args.length != 5) {
            printUsageAndExit();
        }

        //Variable assignments
        File myFile = new File(args[0]);
        int clusters = parsePositiveInt(args[1], "K (number of clusters)");
        int maxIterations = parsePositiveInt(args[2], "I (max iterations)");
        double convergenceThreshold = parseNonNegativeDouble(args[3], "T (convergence threshold)");
        int numRuns = parsePositiveInt(args[4], "R (number of runs)");

        validateInputFile(myFile);


        return new Config(myFile, clusters, maxIterations, convergenceThreshold, numRuns);
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: java Main <F> <K> <I> <T> <R>");
        System.err.println("  F: input file");
        System.err.println("  K: clusters (>0)");
        System.err.println("  I: max iterations (>0)");
        System.err.println("  T: convergence threshold (>=0)");
        System.err.println("  R: runs (>0)");
        System.exit(1);
    }

    private static int parsePositiveInt(String s, String name) {
        try {
            int v = Integer.parseInt(s);
            if (v <= 0) {
                Utils.exitWithError(name + " must be > 0, got: " + v);
            }
            return v;
        } catch (NumberFormatException e) {
            Utils.exitWithError(name + " must be an integer, got: " + s);
            return -1;
        }
    }

    private static double parseNonNegativeDouble(String s, String name) {
        try {
            double v = Double.parseDouble(s);
            if (v < 0) {
                Utils.exitWithError(name + " must be >= 0, got: " + v);
            }
            return v;
        } catch (NumberFormatException e) {
            Utils.exitWithError(name + " must be a double, got: " + s);
            return -1;
        }
    }

    private static void validateInputFile(File f) {
        if (!f.exists()) {
            Utils.exitWithError("File does not exist: " + f.getPath());
        }
        if (!f.isFile()) {
            Utils.exitWithError("Not a regular file: " + f.getPath());
        }
        if (!f.canRead()) {
            Utils.exitWithError("File not readable: " + f.getPath());
        }
        if (f.length() == 0) {
            Utils.exitWithError("File is empty: " + f.getPath());
        }
    }


}
