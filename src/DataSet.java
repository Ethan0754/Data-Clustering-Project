import java.util.List;

public record DataSet(
        int numPoints,
        int dimensionality,
        int trueClusters,
        List<Integer> clusterAssignments,
        List<List<Double>> outerArray
) {}
