import java.io.File;

public record Config(
    File myFile,
    int maxIterations,
    double convergenceThreshold,
    int numRuns
) {}
