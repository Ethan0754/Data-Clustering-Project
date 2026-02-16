//Author: Ethan Pendergraft
//Java Style Guide: https://www.cs.cornell.edu/courses/JavaAndDS/JavaStyle.html#overcomment

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Main {
    public static void main(String[] args) {

        if (args.length != 5) {
            System.out.println("Usage: java Program <F> <K> <I> <T> <R>");
            System.exit(1);
        }

        File myFile = new File(args[0]);
        int clusters = Integer.parseInt(args[1]);
        int maxIterations = Integer.parseInt(args[2]);
        double convergenceThreshold = Double.parseDouble(args[3]);
        int numRuns = Integer.parseInt(args[4]);

        List<List<Double>> outerArray = new ArrayList<>();

        int numPoints = 0;
        int dimensionality = 0;
        String temp = "";

        try (Scanner scanner = new Scanner(myFile)) {
            if (scanner.hasNextLine()) { //At the start of the file, harvest the number of points and the dimensionality, and throw the rest away
                numPoints = scanner.nextInt();
                dimensionality = scanner.nextInt();
                temp = scanner.nextLine();
            }
            while (scanner.hasNextLine()) { //Main file reading loop

                String line = scanner.nextLine(); //read a full line
                String[] dataPointsStr = line.split(" "); //then get individual numbers from that full line

                List<Double> innerArray = new ArrayList<>();

                for (String data : dataPointsStr) {
                    Double dataPoint = Double.parseDouble(data);
                    innerArray.add(dataPoint); //add individual (now Doubles) values to inner array
                }
                outerArray.add(innerArray);

            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: cannot open file");
            System.exit(1);
        }


        Random random = new Random();

        String outputFileName = args[0].replace(".txt", "") + "_output.txt"; //name output files based on input file
        StringBuilder outputBuilder = new StringBuilder(); //Use string builder to build input string for file

        for (int i = 0; i < clusters; i++) {
            int center = random.nextInt(numPoints);
            for (Double dataPoint : outerArray.get(center)) {
                System.out.print(dataPoint + " "); //print to console
                outputBuilder.append(dataPoint).append(" "); //add to string builder that adds to file
            }
            System.out.println();
            outputBuilder.append("\n"); //adds line break to file output
        }

        try (PrintWriter out = new PrintWriter(outputFileName)) {//Use printwriter to add
            out.print(outputBuilder.toString());
        } catch (IOException e) {
            System.out.println("Error: could not write output file");
            System.exit(1);
        }




    }

}
