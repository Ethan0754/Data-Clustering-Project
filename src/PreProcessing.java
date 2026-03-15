import java.util.ArrayList;
import java.util.List;

public final class PreProcessing {
    private PreProcessing() {}

    public static DataSet minmaxnormalization(DataSet dataSet) {

        int dims = dataSet.dimensionality();
        int points = dataSet.outerArray().size();

        List<Double> maximums = new ArrayList<>();
        List<Double> minimums = new ArrayList<>();

        // initialize using first row
        for (int j = 0; j < dims; j++) {
            double val = dataSet.outerArray().getFirst().get(j);
            maximums.add(val);
            minimums.add(val);
        }

        // find min and max for each dimension
        for (int i = 1; i < points; i++) {
            for (int j = 0; j < dims; j++) {

                double val = dataSet.outerArray().get(i).get(j);

                if (val > maximums.get(j)) {
                    maximums.set(j, val);
                }

                if (val < minimums.get(j)) {
                    minimums.set(j, val);
                }
            }
        }

        // apply min-max normalization
        for (int i = 0; i < points; i++) {
            for (int j = 0; j < dims; j++) {

                double val = dataSet.outerArray().get(i).get(j);
                double min = minimums.get(j);
                double max = maximums.get(j);

                double normValue;

                if (max == min) {
                    normValue = 0.0; // avoid divide by zero
                } else {
                    normValue = (val - min) / (max - min);
                }

                dataSet.outerArray().get(i).set(j, normValue);
            }
        }

        return dataSet;
    }

}
