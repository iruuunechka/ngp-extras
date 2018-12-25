package ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.splitter;

import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;

import java.util.Comparator;
import java.util.List;

/**
 * Due to Java 1.8.0.45 (and around) bug, static methods from Splittable moved here.
 * @author Maxim Buzdalov
 */
public final class Splittables {
    public static SplitResult split(KolmogorovSmirnovTest splitter, List<? extends Splittable> data, int offset, double eps) {
        data.sort(Comparator.comparingDouble(Splittable::getX));
        double[] qs = data.stream().mapToDouble(Splittable::getY).toArray();
        double prev = data.get(offset - 1).getX();
        SplitResult bestSplit = null;
        for (int p = offset; p < data.size() - offset; p++) {
            double cur = data.get(p).getX();
            if (Math.abs(prev - cur) < eps)
                continue;
            double[] x = new double[p];
            System.arraycopy(qs, 0, x, 0, p);
            double[] y = new double[qs.length - p];
            System.arraycopy(qs, p, y, 0, qs.length - p);
            double pValue = getPValue(splitter, x, y);
            if (isSufficient(pValue)) {
                if(bestSplit == null || pValue < bestSplit.getpValue())
                    bestSplit = new SplitResult((prev + cur) / 2, pValue);
            }
            prev = cur;
        }
        return bestSplit;
    }

    public static double getPValue(KolmogorovSmirnovTest splitter, double[] x, double[] y) {
        return splitter.approximateP(splitter.kolmogorovSmirnovStatistic(x, y), x.length, y.length);
    }

    public static boolean isSufficient(double pValue) {
        return pValue < 0.05;
    }
}
