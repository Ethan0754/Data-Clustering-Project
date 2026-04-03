//Author: Ethan Pendergraft
//Java Style Guide: https://www.cs.cornell.edu/courses/JavaAndDS/JavaStyle.html#overcomment

public class Main {
    public static void main(String[] args) {

        Config config = ConfigParser.parse(args); // Read, parse, and validate cli args
        DataSet dataSet = DataSetReader.readFile(config.myFile()); // Read data from file and store

        dataSet = PreProcessing.minmaxnormalization(dataSet);

        KMeansRunner kMeansRunner = new KMeansRunner(config, dataSet);
        kMeansRunner.run();

        String outputFile = "phase4_output.csv";

        //ResultPrinter.writeCsvHeader(outputFile);

        for (KTestValues test : kMeansRunner.getAllKTests()) {

            ResultPrinter.appendCsvRow(
                    outputFile,
                    config.myFile().toString(),
                    "min-max",
                    "random partition",
                    test.k(),
                    test.bestRun(),
                    test.SSE(),
                    test.ch(),
                    test.sw()
            );
        }
    }
}