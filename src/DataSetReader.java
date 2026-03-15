import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataSetReader {
    static DataSet readFile(File myFile) {
        //vars found in file
        int numPoints = 0;
        int dimensionality = 0;

        List<List<Double>> outerArray = new ArrayList<>();

        try (Scanner scanner = new Scanner(myFile)) {
            if (!scanner.hasNextInt()) {
                Utils.exitWithError("Missing numPoints in header");
            }
            numPoints = scanner.nextInt();

            if (!scanner.hasNextInt()) {
                Utils.exitWithError("Missing dimensionality in header");
            }
            dimensionality = scanner.nextInt();

            if (numPoints <= 0) {
                Utils.exitWithError("Invalid numPoints: " + numPoints);
            }
            if (dimensionality <= 0) {
                Utils.exitWithError("Invalid dimensionality: " + dimensionality);
            }

            if (scanner.hasNextLine()) {//Skip to real data
                scanner.nextLine();
            }

            int lineNum = 1;

            while (scanner.hasNextLine()) { //Main file reading loop

                String line = scanner.nextLine().trim(); //read a full line
                lineNum++;

                if (line.isEmpty()) continue;

                String[] dataPointsStr = line.split("\\s+"); //then get individual numbers from that full line

                if (dataPointsStr.length != dimensionality) { //ensure each line has dimensionality num of points
                    Utils.exitWithError("Line " + lineNum + " has " + dataPointsStr.length + " values; expected " + dimensionality);
                }

                List<Double> innerArray = new ArrayList<>();

                for (String data : dataPointsStr) {
                    try {
                        Double dataPoint = Double.parseDouble(data);
                        innerArray.add(dataPoint); //add individual (now Doubles) values to inner array
                    } catch (NumberFormatException e) {
                        Utils.exitWithError("Invalid number on line " + lineNum + ": " + data);
                    }
                }
                outerArray.add(innerArray);

            }

            if (outerArray.size() != numPoints) {
                Utils.exitWithError("Header numPoints=" + numPoints + " but found " + outerArray.size() + " rows");
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error: cannot open file");
            System.exit(1);
        }

        return new DataSet(numPoints, dimensionality, outerArray);
    }
}
