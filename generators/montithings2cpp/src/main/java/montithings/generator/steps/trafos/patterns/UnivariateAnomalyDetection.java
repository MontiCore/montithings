package montithings.generator.steps.trafos.patterns;

/**
 * Implement UnivariateAnomalyDetection interface for different implementations of
 * univariate anomaly detection. E.g. the simplest univariate anomaly detection algorithm
 * is the "Autoregressive" algorithm
 */
public interface UnivariateAnomalyDetection {
    boolean isAnomaly(Double input);
}
