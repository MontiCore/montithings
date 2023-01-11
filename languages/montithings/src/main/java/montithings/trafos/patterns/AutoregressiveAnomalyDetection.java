package montithings.trafos.patterns;

import java.util.*;

/**
 * One of the most basic stochastical models for univariate time-series is the Autoregressive model (AR). AR is a
 * linear model where current value Xt of the stochastic process (dependent variable) is based one a finite set of
 * previous values (independent variables) of length p and an error value e:
 */
public class AutoregressiveAnomalyDetection extends LinearRegression implements UnivariateAnomalyDetection{

    private final int windowSize;
    private final double tolerance;
    private final List<Double> pastValues;

    public AutoregressiveAnomalyDetection(int windowSize, double tolerance) {
        this.windowSize = windowSize;
        this.tolerance = tolerance;
        this.pastValues = new ArrayList<>();
    }

    @Override
    public boolean isAnomaly(Double input) {
        List<Double> regressionValues = this.getRegressionValues();

        this.pastValues.add(input);

        Double predictedValue = this.predictValue(input, regressionValues);

        if (predictedValue == null) {
            return false;
        }

        double err = Math.abs(predictedValue - input);

        return err > this.tolerance;
    }

    private List<Double> getRegressionValues() {
        List<Double> values = new ArrayList<>();

        ListIterator<Double> li = this.pastValues.listIterator(this.pastValues.size());

        while (li.hasPrevious() && values.size() < this.windowSize) {
            values.add(li.previous());
        }

        return values;
    }
}
