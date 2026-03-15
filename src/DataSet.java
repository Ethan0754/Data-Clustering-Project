import java.util.List;

public record DataSet(
        int numPoints,
        int dimensionality,
        List<List<Double>> outerArray
) {}
