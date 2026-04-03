import java.util.List;
import java.util.Map;

public record KMeansResult(
        double initialSSE,
        double finalSSE,
        int iterations,
        List<List<Double>> centers,
        Map<Integer, Integer> clusterDict,
        int k
) {}