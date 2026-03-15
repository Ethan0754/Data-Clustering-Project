public final class Utils {
    private Utils() {} //Prevents instantiation of configparser class

    public static void exitWithError(String msg) {
        System.err.println("Error: " + msg);
        System.exit(1);
    }
}
