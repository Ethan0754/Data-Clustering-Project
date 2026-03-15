//Author: Ethan Pendergraft
//Java Style Guide: https://www.cs.cornell.edu/courses/JavaAndDS/JavaStyle.html#overcomment

import java.io.*;


public class Main {
    public static void main(String[] args) {

        Config config = ConfigParser.parse(args); //Read, parse, and validate cli args
        DataSet dataSet = DataSetReader.readFile(config.myFile()); //Read data from file and store

        //ResultPrinter.setupOutputFile(config.myFile()); //reroutes output to myFile_out.txt


        KMeansRunner kMeansRunner = new KMeansRunner();
        kMeansRunner.run(config, dataSet);

        ResultPrinter.printBestRun(kMeansRunner.getBestRun(), kMeansRunner.getBestSSE());
    }
}
