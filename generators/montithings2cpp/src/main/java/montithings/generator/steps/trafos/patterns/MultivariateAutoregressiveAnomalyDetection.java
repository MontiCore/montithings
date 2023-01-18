package montithings.generator.steps.trafos.patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Combines all input data into a single autogressive anomaly detection
 */
public class MultivariateAutoregressiveAnomalyDetection extends LinearRegression implements MultivariateAnomalyDetection {
    private final int windowSize;
    private final double tolerance;
    private final List<List<Double>> pastValues;

    public MultivariateAutoregressiveAnomalyDetection(int windowSize, double tolerance) {
        this.windowSize = windowSize;
        this.tolerance = tolerance;
        this.pastValues = new ArrayList<>();
    }

    @Override
    public List<Boolean> isAnomaly(List<Double> inputs) {
        List<Boolean> res = new ArrayList<>();

        List<Double> regressionValues = this.getRegressionValues();
        this.pastValues.add(inputs);

        for (Double input : inputs) {
            Double predictedValue = this.predictValue(input, regressionValues);

            if (predictedValue == null) {
                res.add(false);
                continue;
            }

            double err = Math.abs(predictedValue - input);

            res.add(err > this.tolerance);
        }

        return res;
    }

    private List<Double> getRegressionValues() {
        List<Double> values = new ArrayList<>();

        for (int i = 0; i < this.pastValues.size(); i++) {
            values.addAll(this.getRegressionValues(i));
        }

        return values;
    }

    private List<Double> getRegressionValues(int idx) {
        List<Double> values = new ArrayList<>();

        ListIterator<Double> li = this.pastValues.get(idx).listIterator(this.pastValues.get(idx).size());

        while (li.hasPrevious() && values.size() < this.windowSize) {
            values.add(li.previous());
        }

        return values;
    }
}
