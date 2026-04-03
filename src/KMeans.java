import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class KMeans {

    private List<List<Double>> centers;
    private Map<Integer, Integer> clusterDict;
    private final Config config;
    private final DataSet dataSet;
    private final int k;

    public KMeans(Config config, DataSet dataSet, int k) {
        this.config = config;
        this.dataSet = dataSet;
        this.k = k;

        centers = new ArrayList<>();
        randomPartition();
        // randomSelection();


        clusterDict = new HashMap<>();
    }

    public KMeansResult run() {
        int currIter = 0;
        double SSE = 0.0;
        double initialSSE = 0.0;
        double previousSSE = 0.0;

        while (currIter < config.maxIterations()) {

            if (currIter != 0) {
                centers = recomputeCenters(clusterDict, centers, dataSet.outerArray(), dataSet.dimensionality());
            }

            previousSSE = SSE;
            SSE = assignClustersAndComputeSSE(centers, dataSet.outerArray(), dataSet.dimensionality());

            if (currIter == 0) {
                initialSSE = SSE;
            }

            //ResultPrinter.printIteration(currIter, SSE);

            if (checkConvergence(previousSSE, SSE, config.convergenceThreshold())) {
                break;
            }

            currIter += 1;
        }

        return new KMeansResult(initialSSE, SSE, currIter + 1, centers, clusterDict, k);
    }

    private boolean checkConvergence(double previousSSE, double SSE, double convergenceThreshold) {
        if (previousSSE > 0) {
            double relChange = Math.abs(previousSSE - SSE) / previousSSE;
            return relChange < convergenceThreshold;
        }
        return false;
    }

    /**
     * Assign each point to its nearest center and compute SSE in the same pass.
     * This removes the separate distance, cluster assignment, and SSE passes.
     */
    private double assignClustersAndComputeSSE(List<List<Double>> centers, List<List<Double>> outerArray, int dimensionality) {
        clusterDict.clear();
        double sse = 0.0;

        for (int pointIndex = 0; pointIndex < outerArray.size(); pointIndex++) {
            List<Double> point = outerArray.get(pointIndex);

            int bestCluster = 0;
            double bestDistance = Double.POSITIVE_INFINITY;

            for (int centerIndex = 0; centerIndex < centers.size(); centerIndex++) {
                List<Double> center = centers.get(centerIndex);
                double sum = 0.0;

                for (int dim = 0; dim < dimensionality; dim++) {
                    double diff = point.get(dim) - center.get(dim);
                    sum += diff * diff;
                }

                if (sum < bestDistance) {
                    bestDistance = sum;
                    bestCluster = centerIndex;
                }
            }

            clusterDict.put(pointIndex, bestCluster);
            sse += bestDistance;
        }

        return sse;
    }

    /**
     * Recompute centroids in one pass over the dataset.
     * This is much faster than scanning all points مرة per cluster.
     */
    private List<List<Double>> recomputeCenters(
            Map<Integer, Integer> clusterAssignment,
            List<List<Double>> previousCenters,
            List<List<Double>> outerArray,
            int dimensionality
    ) {
        double[][] sums = new double[k][dimensionality];
        int[] counts = new int[k];

        for (int pointIndex = 0; pointIndex < outerArray.size(); pointIndex++) {
            int clusterIndex = clusterAssignment.get(pointIndex);
            List<Double> point = outerArray.get(pointIndex);

            counts[clusterIndex]++;

            for (int dim = 0; dim < dimensionality; dim++) {
                sums[clusterIndex][dim] += point.get(dim);
            }
        }

        List<List<Double>> newCenters = new ArrayList<>();

        for (int clusterIndex = 0; clusterIndex < k; clusterIndex++) {
            List<Double> center = new ArrayList<>();

            if (counts[clusterIndex] == 0) {
                center.addAll(previousCenters.get(clusterIndex));
            } else {
                for (int dim = 0; dim < dimensionality; dim++) {
                    center.add(sums[clusterIndex][dim] / counts[clusterIndex]);
                }
            }

            newCenters.add(center);
        }

        return newCenters;
    }

    private void randomSelection() {
        Random random = new Random();
        centers.clear();

        for (int i = 0; i < k; i++) {
            int centerIndex = random.nextInt(dataSet.outerArray().size());
            centers.add(new ArrayList<>(dataSet.outerArray().get(centerIndex)));
        }
    }

    private void randomPartition() {
        Map<Integer, Integer> initialAssignment = new HashMap<>();
        Random random = new Random();

        for (int i = 0; i < dataSet.outerArray().size(); i++) {
            initialAssignment.put(i, random.nextInt(k));
        }

        centers.clear();
        for (int i = 0; i < k; i++) {
            int randomPointIndex = random.nextInt(dataSet.outerArray().size());
            centers.add(new ArrayList<>(dataSet.outerArray().get(randomPointIndex)));
        }

        centers = recomputeCenters(
                initialAssignment,
                centers,
                dataSet.outerArray(),
                dataSet.dimensionality()
        );
    }
}