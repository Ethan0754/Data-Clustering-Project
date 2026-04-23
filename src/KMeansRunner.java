import javax.xml.transform.Result;
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

    private List<KTestValues> AllKTests;

    private record PairResults(long a, long b, long c, long d) {}

    public KMeansRunner(Config config, DataSet dataSet) {
        this.config = config;
        this.dataSet = dataSet;
        this.AllKTests = new ArrayList<>();
    }

    public void run() {


        bestFinalSSE = null;
        bestRun = -1;
        bestResult = null;

        int currRun = 0;
        double bestRand = -1;
        double bestJaccard = -1;

        while (currRun < config.numRuns()) {
            ResultPrinter.printRunStart(currRun);

            kMeans = new KMeans(config, dataSet, dataSet.trueClusters());
            result = kMeans.run();

            findBests(result, currRun);

            PairResults pairResults = calculate_pairs(result);

            double rand = calculate_rand(pairResults);
            double jaccard = calculate_jaccard(pairResults);

            if (rand > bestRand) bestRand = rand;
            if (jaccard > bestJaccard) bestJaccard = jaccard;

            currRun++;
        }


        AllKTests.add(new KTestValues(dataSet.trueClusters(), bestRun, bestFinalSSE, bestRand, bestJaccard));
        ResultPrinter.printExternalValidity(dataSet.trueClusters(), bestRand, bestJaccard);

    }

    private PairResults calculate_pairs(KMeansResult result) {

        long a = 0, b = 0, c = 0, d = 0;

        Map<Integer,Integer> pred =
                result.clusterDict();

        for (int i = 0; i < dataSet.numPoints() - 1; i++) {

            int trueI =
                    dataSet.clusterAssignments().get(i);

            int predI =
                    pred.get(i);

            for (int j = i + 1; j < dataSet.numPoints(); j++) {

                if (trueI ==
                        dataSet.clusterAssignments().get(j)) {

                    if (predI == pred.get(j))
                        a++;
                    else
                        c++;

                } else {

                    if (predI == pred.get(j))
                        b++;
                    else
                        d++;
                }
            }
        }

        return new PairResults(a,b,c,d);
    }
    private double calculate_rand(PairResults p) {

        long total =
                p.a() + p.b() + p.c() + p.d();

        return (double)(p.a() + p.d()) / total;
    }

    private double calculate_jaccard(PairResults p) {

        long denom =
                p.a() + p.b() + p.c();

        if (denom == 0)
            return 0.0;

        return (double)p.a() / denom;
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