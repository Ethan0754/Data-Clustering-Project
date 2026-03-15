import java.io.File;

public record Config(
    File myFile,
    int clusters,
    int maxIterations,
    double convergenceThreshold,
    int numRuns
) {}
