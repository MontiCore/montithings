package montithings.trafos.patterns;

import java.util.List;

/**
 * Implement MultivariateAnomalyDetection interface for different implementations of
 * multivariate anomaly detection. E.g. the simplest multivariate anomaly detection algorithm
 * is the "Combined Autoregressive" algorithm
 */
public interface MultivariateAnomalyDetection {
    List<Boolean> isAnomaly(List<Double> input);
}
