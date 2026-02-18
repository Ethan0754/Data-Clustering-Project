//Author: Ethan Pendergraft
//Java Style Guide: https://www.cs.cornell.edu/courses/JavaAndDS/JavaStyle.html#overcomment

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;


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

        List<Integer> centers = new ArrayList<>();
        List<List<Double>> outerArray = new ArrayList<>();

        Map<String, List<Double>> distanceDict = new HashMap<>();
        Map<String, Integer> clusterDict = new HashMap<>();

        List<List<Double>> updatedCenters = new ArrayList<>();

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
                distanceDict.put(innerArray.toString(), new ArrayList<>()); //Add full line to distance dictionary

            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: cannot open file");
            System.exit(1);
        }


        Random random = new Random();

        String outputFileName = args[0].replace(".txt", "") + "_output.txt"; //name output files based on input file


        for (int i = 0; i < clusters; i++) {
            int center = random.nextInt(numPoints);
            centers.add(center);
        }

        distanceDict = distance(dimensionality, centers, outerArray, distanceDict);
        clusterDict = cluster_assignment(centers, outerArray, distanceDict);
        updatedCenters = recompute_centers(clusterDict, centers, outerArray, dimensionality);
    }

    static Map<String, List<Double>> distance(int dimensionality, List<Integer> centers, List<List<Double>> outerArray, Map<String, List<Double>> distanceDict) {
        List<Double> currValue;
        for (int i = 0; i <centers.size(); i++)
            for (List<Double> innerArray : outerArray) {
                double sum = 0.0;
                double num = 0.0;
                for (int j = 0; j < dimensionality; j++) {
                    num = innerArray.get(j) - outerArray.get(centers.get(i)).get(j);
                    sum += num * num;
                }

                currValue = distanceDict.get(innerArray.toString());

                currValue.add(sum);
                distanceDict.put(innerArray.toString(), currValue);
        }

        System.out.println("Horray!");
        return distanceDict;
    }

    static Map<String, Integer> cluster_assignment(List <Integer> centers, List<List<Double>> outerArray, Map<String, List<Double>> distanceDict) {
        Map<String, Integer> clusterDict = new HashMap<>();


        for (List<Double> innerArray : outerArray) {
            List <Double> distanceList;
            distanceList = distanceDict.get(innerArray.toString());
            int minDistanceIndex = 0;
            for (int i = 0; i <distanceList.size(); i++) {
                if (distanceList.get(i) < distanceList.get(minDistanceIndex)) {
                    minDistanceIndex = i;
                }
            }
            clusterDict.put(innerArray.toString(), minDistanceIndex);
        }
        System.out.println("Hooray!");
        return clusterDict;
    }


    static List<List<Double>> recompute_centers(Map<String, Integer> clusterDict, List<Integer> centers, List<List<Double>> outerArray, int dimensionality) {
        List<List<Double>> centerList = new ArrayList<>();
        for (int i = 0; i <centers.size(); i++) {
            List<Double> newCenter = new ArrayList<>();
            int centerCount = 0;
            for (List<Double> innerArray : outerArray) {
                List<Double> distanceList;
                Integer currCluster;
                currCluster = clusterDict.get(innerArray.toString());

                if (currCluster == i) {
                    for (int j = 0; j < dimensionality; j++) {
                        if (newCenter.size() == dimensionality) {
                            newCenter.set(j, newCenter.get(j) +innerArray.get(j));
                        }
                        else {
                            newCenter.add(innerArray.get(j));
                        }

                    }
                    centerCount += 1;
                }
            }
            for (int j = 0; j < dimensionality; j++) {
                Double currValue = newCenter.get(j);
                newCenter.set(j, currValue/centerCount); //Need to add a step here to check for centerCount = 0
            }
            centerList.add(newCenter);
        }
        System.out.println("Horray!");
        return centerList;
    }

}




