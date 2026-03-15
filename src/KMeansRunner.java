public class KMeansRunner {
    private KMeans kMeans;
    private Double bestFinalSSE;
    private Double bestInitialSSE;
    private int bestIteration;
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
            findBests(kMeans.run(), currRun);
            //Find bestSSE



            currRun += 1;
        }
    }

    private void findBests(KMeansResult result, int currRun) {
        //Find best Final SSE
        if (currRun == 0) {//For first run, set bestSSE to first seen sse
            bestFinalSSE = result.finalSSE();
        }
        else if (bestFinalSSE > result.finalSSE()) { //If better sse is found, set SSE and run number
            bestFinalSSE = result.finalSSE();
            bestRun = currRun;
        }

        //Find best Initial SSE
        if (currRun == 0) {
            bestInitialSSE = result.initialSSE();
        }
        else if (bestInitialSSE > result.initialSSE()) {
            bestInitialSSE = result.initialSSE();
        }

        //Find best iteration count
        if (currRun == 0) {
            bestIteration = result.iterations();
        }
        else if (bestIteration > result.iterations()) {
            bestIteration = result.iterations();
        }

    }
    public Double getBestFinalSSE() {
        return bestFinalSSE;
    }
    public int getBestRun() {
        return bestRun;
    }
    public Double getBestInitialSSE() {return bestInitialSSE;}
    public int getBestIteration() {return bestIteration;}
}
