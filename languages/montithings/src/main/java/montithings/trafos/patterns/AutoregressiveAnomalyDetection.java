package montithings.trafos.patterns;

import java.util.*;

/**
 * One of the most basic stochastical models for univariate time-series is the Autoregressive model (AR). AR is a
 * linear model where current value Xt of the stochastic process (dependent variable) is based one a finite set of
 * previous values (independent variables) of length p and an error value e:
 */
public class AutoregressiveAnomalyDetection implements UnivariateAnomalyDetection{

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

    private Double predictValue(Double input, List<Double> values) {
        List<Double> xValues = this.xValues(values);

        Double meanX = this.mean(xValues);
        Double meanY = this.mean(values);

        Double stdX = this.std(xValues, meanX);
        Double stdY = this.std(values, meanY);

        Double covariance = this.covariance(xValues, values, meanX, meanY);

        Double slope = this.slope(covariance, stdX, stdY);

        Double yIntercept = this.yIntercept(slope, meanX, meanY);

        return slope != null && yIntercept != null ? slope * input - yIntercept : null;
    }

    private Double mean(List<Double> values) {
        if (values.size() == 0) {
            return null;
        }

        double sum = values.stream().mapToDouble(Double::doubleValue).sum();

        return sum / values.size();
    }

    private Double std(List<Double> values, Double mean) {
        if (mean == null) {
            return null;
        }

        return values.stream().reduce((double) 0, (subtotal, elt) -> subtotal + (elt - mean) * (elt - mean));
    }

    private Double covariance(List<Double> xValues, List<Double> yValues, Double meanX, Double meanY) {
        if (xValues.size() != yValues.size() || xValues.size() == 0 || meanX == null || meanY == null) {
            return null;
        }

        double nominator = 0;

        Iterator<Double> itX = xValues.iterator();
        Iterator<Double> itY = yValues.iterator();

        while(itX.hasNext() && itY.hasNext()) {
            double x = itX.next();
            double y = itY.next();
            nominator += (x - meanX) * (y - meanY);
        }

        return nominator / xValues.size();
    }

    private Double slope(Double covariance, Double stdX, Double stdY) {
        if (covariance == null || stdX == null || stdY == null || stdX == 0) {
            return null;
        }

        return covariance * (stdY / stdX);
    }

    private Double yIntercept(Double slope, Double meanX, Double meanY) {
        if (slope == null || meanX == null || meanY == 0) {
            return null;
        }

        return meanY - slope * meanX;
    }

    private List<Double> xValues(List<Double> values) {
        List<Double> xValues = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            xValues.add((double) i);
        }

        return xValues;
    }
}
