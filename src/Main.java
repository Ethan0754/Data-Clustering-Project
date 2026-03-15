//Author: Ethan Pendergraft
//Java Style Guide: https://www.cs.cornell.edu/courses/JavaAndDS/JavaStyle.html#overcomment


public class Main {
    public static void main(String[] args) {

        Config config = ConfigParser.parse(args); //Read, parse, and validate cli args
        DataSet dataSet = DataSetReader.readFile(config.myFile()); //Read data from file and store



        dataSet = PreProcessing.minmaxnormalization(dataSet);
        KMeansRunner kMeansRunner = new KMeansRunner();
        kMeansRunner.run(config, dataSet);

        //ResultPrinter.setupOutputFile(config.myFile()); //reroutes console output to myFile_out.txt
        ResultPrinter.printBestRun(kMeansRunner.getBestRun(), kMeansRunner.getBestFinalSSE()); //prints to console
        //ResultPrinter.writeCsvHeader("phase3_output.csv"); //prints csv header and makes file
        ResultPrinter.appendCsvRow("phase3_output.csv", config.myFile().toString(), "min-max", "random partition", //prints csv data
                kMeansRunner.getBestInitialSSE(),
                kMeansRunner.getBestFinalSSE(),
                kMeansRunner.getBestIteration());
    }
}
