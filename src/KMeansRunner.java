import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KMeansRunner {
    private KMeans kMeans;

    private DataSet dataSet;
    private Config config;

    private Double bestFinalSSE;
    private int bestRun;
    private KMeansResult bestResult;

    private KMeansResult result;

    private final int minK = 2;
    private long maxK;

    private final double[][] distanceMatrix;

    private List<KTestValues> AllKTests;

    public KMeansRunner(Config config, DataSet dataSet) {
        this.config = config;
        this.dataSet = dataSet;
        this.AllKTests = new ArrayList<>();
        this.distanceMatrix = buildDistanceMatrix();
    }

    public void run() {

        maxK = findMaxK(dataSet);
        int currK = minK;
        while (currK <= maxK) {
            bestFinalSSE = null;
            bestRun = -1;
            bestResult = null;

            int currRun = 0;
            long startTime = System.nanoTime();
            while (currRun < config.numRuns()) {
                //ResultPrinter.printRunStart(currRun);
                kMeans = new KMeans(config, dataSet, currK);
                result = kMeans.run();
                findBests(result, currRun);

                currRun += 1;
            }
            long endTime = System.nanoTime();
            ResultPrinter.printKMeansTime(endTime-startTime);

            startTime = System.nanoTime();
            double ch = calinski_harabasz_calc();
            endTime = System.nanoTime();
            ResultPrinter.printCHTime(endTime-startTime);

            startTime = System.nanoTime();
            double sw = silhouette_width_calc();
            endTime = System.nanoTime();
            ResultPrinter.printSWTime(endTime-startTime);

            AllKTests.add(new KTestValues(currK, bestRun, bestFinalSSE, ch, sw));
            ResultPrinter.printInternalValidity(currK, ch, sw);
            currK += 1;
        }
    }

    private void findBests(KMeansResult result, int currRun) {
        if (currRun == 0) {
            bestFinalSSE = result.finalSSE();
            bestRun = currRun + 1;
            bestResult = result;
        } else if (bestFinalSSE > result.finalSSE()) {
            bestFinalSSE = result.finalSSE();
            bestRun = currRun + 1;
            bestResult = result;
        }
    }

    private List<Integer> countPointCenters() {
        Map<Integer, Integer> clusterAssignments = bestResult.clusterDict();

        int K = bestResult.centers().size();

        List<Integer> clusterCount = new ArrayList<>();

        for (int i = 0; i < K; i++) {
            clusterCount.add(0);
        }

        for (Integer pointIndex : clusterAssignments.keySet()) {
            int clusterIndex = clusterAssignments.get(pointIndex);

            clusterCount.set(
                    clusterIndex,
                    clusterCount.get(clusterIndex) + 1
            );
        }

        return clusterCount;
    }

    private double calinski_harabasz_calc() {
        List<Double> overallMean = dataSetMean();
        List<Integer> centerPointCount = countPointCenters();

        int K = bestResult.centers().size();
        int N = dataSet.numPoints();

        double traceSb = 0.0;
        double traceSw = bestResult.finalSSE();

        for (int i = 0; i < K; i++) {
            double squaredDistance = 0.0;

            for (int j = 0; j < dataSet.dimensionality(); j++) {
                double diff = bestResult.centers().get(i).get(j) - overallMean.get(j);
                squaredDistance += diff * diff;
            }

            traceSb += centerPointCount.get(i) * squaredDistance;
        }

        return (traceSb / (K - 1)) / (traceSw / (N - K));
    }

    private double silhouette_width_calc() {
        List<List<Integer>> pointsInClusters = cluster_to_point_indices();

        double totalSilhouette = 0.0;
        int N = dataSet.outerArray().size();

        for (int pointIndex = 0; pointIndex < N; pointIndex++) {
            int ownCluster = bestResult.clusterDict().get(pointIndex);

            double s;

            if (pointsInClusters.get(ownCluster).size() <= 1) {
                s = 0.0;
            } else {
                double a = aofI(pointIndex, ownCluster, pointsInClusters, distanceMatrix);
                double b = bofI(pointIndex, ownCluster, pointsInClusters, distanceMatrix);
                s = (b - a) / Math.max(a, b);
            }

            totalSilhouette += s;
        }

        return totalSilhouette / N;
    }

    private double aofI(
            int pointIndex,
            int ownCluster,
            List<List<Integer>> pointsInClusters,
            double[][] distanceMatrix
    ) {
        List<Integer> ownClusterPoints = pointsInClusters.get(ownCluster);

        if (ownClusterPoints.size() <= 1) {
            return 0.0;
        }

        double sum = 0.0;
        int count = 0;

        for (int otherIndex : ownClusterPoints) {
            if (otherIndex != pointIndex) {
                sum += distanceMatrix[pointIndex][otherIndex];
                count += 1;
            }
        }

        return sum / count;
    }

    private double bofI(
            int pointIndex,
            int ownCluster,
            List<List<Integer>> pointsInClusters,
            double[][] distanceMatrix
    ) {
        double minAverageDistance = Double.POSITIVE_INFINITY;

        for (int clusterIndex = 0; clusterIndex < pointsInClusters.size(); clusterIndex++) {
            if (clusterIndex == ownCluster) {
                continue;
            }

            List<Integer> otherClusterPoints = pointsInClusters.get(clusterIndex);

            if (otherClusterPoints.isEmpty()) {
                continue;
            }

            double sum = 0.0;

            for (int otherIndex : otherClusterPoints) {
                sum += distanceMatrix[pointIndex][otherIndex];
            }

            double averageDistance = sum / otherClusterPoints.size();

            if (averageDistance < minAverageDistance) {
                minAverageDistance = averageDistance;
            }
        }

        return minAverageDistance;
    }

    private double[][] buildDistanceMatrix() {
        int N = dataSet.outerArray().size();
        int dims = dataSet.dimensionality();
        List<List<Double>> points = dataSet.outerArray();

        double[][] distanceMatrix = new double[N][N];

        for (int i = 0; i < N; i++) {
            List<Double> point1 = points.get(i);

            for (int j = i; j < N; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0.0;
                    continue;
                }

                List<Double> point2 = points.get(j);
                double sum = 0.0;

                for (int d = 0; d < dims; d++) {
                    double diff = point1.get(d) - point2.get(d);
                    sum += diff * diff;
                }

                double distance = Math.sqrt(sum);
                distanceMatrix[i][j] = distance;
                distanceMatrix[j][i] = distance;
            }
        }

        return distanceMatrix;
    }

    private List<List<Integer>> cluster_to_point_indices() {
        int K = bestResult.centers().size();
        List<List<Integer>> pointsInClusters = new ArrayList<>();

        for (int i = 0; i < K; i++) {
            pointsInClusters.add(new ArrayList<>());
        }

        for (int pointIndex : bestResult.clusterDict().keySet()) {
            int clusterIndex = bestResult.clusterDict().get(pointIndex);
            pointsInClusters.get(clusterIndex).add(pointIndex);
        }

        return pointsInClusters;
    }

    private List<Double> dataSetMean() {
        List<Double> sums = new ArrayList<>();

        int dims = dataSet.dimensionality();
        int points = dataSet.outerArray().size();

        for (int j = 0; j < dims; j++) {
            double val = dataSet.outerArray().getFirst().get(j);
            sums.add(val);
        }

        for (int i = 1; i < points; i++) {
            for (int j = 0; j < dims; j++) {
                double val = dataSet.outerArray().get(i).get(j);
                sums.set(j, sums.get(j) + val);
            }
        }

        for (int j = 0; j < dims; j++) {
            sums.set(j, sums.get(j) / dataSet.numPoints());
        }

        return sums;
    }

    private long findMaxK(DataSet dataSet) {
        return Math.round(Math.sqrt(dataSet.outerArray().size() / 2.0));
    }

    public KMeansResult getBestResult() {
        return bestResult;
    }

    public List<KTestValues> getAllKTests() {
        return AllKTests;
    }
}