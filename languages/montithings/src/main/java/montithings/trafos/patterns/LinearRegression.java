package montithings.trafos.patterns;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class LinearRegression {

    protected Double predictValue(Double input, List<Double> values) {
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

    protected Double mean(List<Double> values) {
        if (values.size() == 0) {
            return null;
        }

        double sum = values.stream().mapToDouble(Double::doubleValue).sum();

        return sum / values.size();
    }

    protected Double std(List<Double> values, Double mean) {
        if (mean == null) {
            return null;
        }

        return values.stream().reduce((double) 0, (subtotal, elt) -> subtotal + (elt - mean) * (elt - mean));
    }

    protected Double covariance(List<Double> xValues, List<Double> yValues, Double meanX, Double meanY) {
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

    protected Double slope(Double covariance, Double stdX, Double stdY) {
        if (covariance == null || stdX == null || stdY == null || stdX == 0) {
            return null;
        }

        return covariance * (stdY / stdX);
    }

    protected Double yIntercept(Double slope, Double meanX, Double meanY) {
        if (slope == null || meanX == null || meanY == 0) {
            return null;
        }

        return meanY - slope * meanX;
    }

    protected List<Double> xValues(List<Double> values) {
        List<Double> xValues = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            xValues.add((double) i);
        }

        return xValues;
    }
}
