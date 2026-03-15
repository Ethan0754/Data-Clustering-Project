import java.util.*;

public class KMeans {

    private List<List<Double>> centers;
    private Map<Integer, List<Double>> distanceDict;
    private Map<Integer, Integer> clusterDict;
    private final Config config;
    private final DataSet dataSet;

    public KMeans(Config config, DataSet dataSet) {
        centers = new ArrayList<>(); //stores centers as data

        //Generate new, random centers
        Random random = new Random();
        for (int i = 0; i < config.clusters(); i++) {
            int centerIndex = random.nextInt(dataSet.outerArray().size());
            centers.add(dataSet.outerArray().get(centerIndex));
        }

        distanceDict = new HashMap<>(); //index next to distance to each cluster

        //insert index keys into distanceDict with empty list
        for (int i = 0; i < dataSet.outerArray().size(); i++) {
            distanceDict.put(i, new ArrayList<>());
        }

        clusterDict = new HashMap<>(); //index next to int corresponding to cluster

        this.config = config;
        this.dataSet = dataSet;
    }

    public Double run() {

        int currIter = 0;
        Double SSE = 0.0;
        Double previousSSE = 0.0;

        while (currIter < config.maxIterations()) {

            for (List<Double> distanceList : distanceDict.values()) {
                distanceList.clear();
            }

            distanceDict = distance(dataSet.dimensionality(), centers, dataSet.outerArray(), distanceDict); //find distances to centers
            clusterDict = cluster_assignment(dataSet.outerArray(), distanceDict); //assign points to clusters

            previousSSE = SSE;
            SSE = calculateSSE(centers, clusterDict, distanceDict, dataSet.outerArray());

            ResultPrinter.printIteration(currIter, SSE);

            if (checkConvergence(previousSSE, SSE, config.convergenceThreshold())) {
                break;
            }

            centers = recompute_centers(clusterDict, centers, dataSet.outerArray(), dataSet.dimensionality()); //find new centers by mean of points
            currIter += 1;
        }
        return SSE;
    }

    private boolean checkConvergence(Double previousSSE, Double SSE, Double convergenceThreshold) {
        //Check SSE against convergence threshold
        if (previousSSE > 0) { //skip first to avoid divide by 0
            double relChange = Math.abs(previousSSE - SSE) / previousSSE;
            return relChange < convergenceThreshold; //returns bool
        }
        return false;
    }

    private Map<Integer, List<Double>> distance(int dimensionality, List<List<Double>> centers, List<List<Double>> outerArray, Map<Integer, List<Double>> distanceDict) {
        List<Double> currValue;
        for (int i = 0; i <centers.size(); i++) //For each center
            for (int j = 0; j < outerArray.size(); j++) { //For each line in data
                double sum = 0.0;
                double num = 0.0;

                //Get sum of squared error
                for (int k = 0; k < dimensionality; k++) { //For each data point in a line
                    num = outerArray.get(j).get(k) - centers.get(i).get(k);
                    sum += num * num;
                }

                currValue = distanceDict.get(j); //get current distances listed in dict

                currValue.add(sum); //add new distance element to list of current distances
                distanceDict.put(j, currValue); //put back in dict
            }
        return distanceDict;
    }

    private Map<Integer, Integer> cluster_assignment(List<List<Double>> outerArray, Map<Integer, List<Double>> distanceDict) {
        Map<Integer, Integer> clusterDict = new HashMap<>();


        for (int i = 0; i < outerArray.size(); i ++) { //For each line in data
            List <Double> distanceList;
            distanceList = distanceDict.get(i);
            int minDistanceIndex = 0;

            //Of the distances listed, find the smallest. The index of the smallest corresponds to cluster num
            for (int j = 0; j <distanceList.size(); j++) {
                if (distanceList.get(j) < distanceList.get(minDistanceIndex)) {
                    minDistanceIndex = j;
                }
            }
            clusterDict.put(i, minDistanceIndex); //put mapping in dict
        }
        return clusterDict;
    }

    private Double calculateSSE(List<List<Double>> centers, Map<Integer, Integer> clusterDict, Map<Integer, List<Double>> distanceDict, List<List<Double>> outerArray) {
        Double sum = 0.0;
        for (int i = 0; i < outerArray.size(); i ++) { //for each line in data
            int cluster = 0;
            List<Double> distances = new ArrayList<>();

            cluster = clusterDict.get(i);
            distances = distanceDict.get(i);

            sum += distances.get(cluster); //index distances list by cluster to get distance to assigned cluster for current line

        }
        return sum;

    }

    static List<List<Double>> recompute_centers(Map<Integer, Integer> clusterDict, List<List<Double>> centers, List<List<Double>> outerArray, int dimensionality) {
        List<List<Double>> centerList = new ArrayList<>();

        for (int i = 0; i <centers.size(); i++) { //for each cluster
            List<Double> newCenter = new ArrayList<>();
            int centerCount = 0; //number of points a cluster has

            for (int j = 0; j < outerArray.size(); j++) { //for each line in data
                Integer currCluster = clusterDict.get(j); //get cluster index listed on each line

                if (currCluster == i) { //if cluster index is the current loop
                    for (int k = 0; k < dimensionality; k++) { //for each data point listed in line item
                        if (newCenter.size() == dimensionality) { //Runs after first loop
                            newCenter.set(k, newCenter.get(k) + outerArray.get(j).get(k)); //add to an existing element
                        }
                        else { //runs for first loop
                            newCenter.add(outerArray.get(j).get(k)); //add a totally new element
                        }

                    }
                    centerCount += 1; //point found for current cluster
                }
            }
            if (centerCount == 0) { //if no points found in new cluster, keep current center
                newCenter = centers.get(i);
            }
            else {
                //Divide each point in newCenter by points found in cluster
                for (int j = 0; j < dimensionality; j++) {
                    Double currValue = newCenter.get(j);
                    newCenter.set(j, currValue / centerCount);
                }
            }
            centerList.add(newCenter);
        }
        return centerList;
    }
}
