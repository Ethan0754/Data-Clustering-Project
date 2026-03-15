import java.util.Random;

public class KMeansRunner {
    private KMeans kMeans;
    private Double bestSSE;
    private int bestRun;

    public void run(Config config, DataSet dataSet) {

        if (config.clusters() > dataSet.numPoints()) {
            Utils.exitWithError("Invalid K: clusters (" + config.clusters() + ") > number of points (" + dataSet.numPoints() + ")");
        }

        int currRun = 0;
        while (currRun < config.numRuns()) {
            Double finalSSE = 0.0;

            ResultPrinter.printRunStart(currRun);
            kMeans = new KMeans(config, dataSet);
            finalSSE = kMeans.run();
            //Find bestSSE
            if (currRun == 0) {//For first run, set bestSSE to first seen sse
                bestSSE = finalSSE;
            }
            if (bestSSE > finalSSE) { //If better sse is found, set SSE and run number
                bestSSE = finalSSE;
                bestRun = currRun;
            }


            currRun += 1;
        }
    }
    public Double getBestSSE() {
        return bestSSE;
    }
    public int getBestRun() {
        return bestRun;
    }
}
