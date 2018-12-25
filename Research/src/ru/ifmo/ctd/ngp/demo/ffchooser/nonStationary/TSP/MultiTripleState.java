package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.TSP;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MultiOptAlgEnvironment;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.state.MultiStateCalculator;

/**
 * Strange state which consists of three parts. Was implemented by Meynster
 * @author Irene Petrova
 */
public class MultiTripleState implements MultiStateCalculator<String, Integer> {
    private final double[] firstPoints;
    private final double[] secondPoints = {0, 0.6, 0.8, 0.9, 0.91, 0.92, 0.95, 0.96, 0.97};//{0, 0.3, 0.4, 0.6};
    private final double bestValue;

    /**
     * Constructs {@link MultiTripleState} with the specified number of iterations.
     * The length should be equal to the maximal number of iterations
     * @param iter the specified number of iterations
     * @param best the best value of the target criterion
     */
    public MultiTripleState(int iter, double best) {
        this.bestValue = best;
        int firstPointsCou = 10;
        firstPoints = new double[firstPointsCou];
        for (int i = 0; i < firstPointsCou; ++i) {
            firstPoints[i] = Math.log(iter) / Math.log(2) / firstPointsCou * i;
        }
    }

    private String findInterval (double num, double[] points) {
        int i = points.length - 1;
        while (num < points[i]) {
            i--;
        }
        return String.valueOf(i);
    }

    private String firstPart(MultiOptAlgEnvironment<String, Integer> environment) {
        int curIter = environment.getAlgorithm().getIterationsNumber() + 1;
        return findInterval(Math.log(curIter) / Math.log(2), firstPoints);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(MultiOptAlgEnvironment<String, Integer> environment) {
        return firstPart(environment) + secondPart(environment) + thirdPart(environment);
    }

    private double fitSum(MultiOptAlgEnvironment<String, Integer> environment) {
        double fitSum = 0;
        int target = environment.getAlgorithm().getTargetParameter();
        for (int i = 0; i < environment.getAlgorithm().getCurrentPoints().size(); ++i) {
            fitSum += environment.getAlgorithm().getCurrentPoints().get(i).get(target);
        }
        return fitSum;
    }

    private String secondPart(MultiOptAlgEnvironment<String, Integer> environment) {
        double idealSum = environment.getAlgorithm().getCurrentPoints().size() * bestValue;
        return findInterval(fitSum(environment) / idealSum, secondPoints);
    }

    private String thirdPart(MultiOptAlgEnvironment<String, Integer> environment) {
        int thirdPointsCou = 10;
        double[] thirdPoints = new double[thirdPointsCou];
        int pointsCou = environment.getAlgorithm().getCurrentPoints().size();
        for (int i = 0; i < thirdPointsCou; ++i) {
            thirdPoints[i] = Math.log(pointsCou) / Math.log(2) / thirdPointsCou * i;
        }
        double entropy = 0;
        int target = environment.getAlgorithm().getTargetParameter();
        double fitSum = fitSum(environment);
        for (int m = 0; m < pointsCou; ++m) {
            double pm = environment.getAlgorithm().getCurrentPoints().get(m).get(target) / fitSum;
            entropy -= pm * Math.log(pm) / Math.log(2);
        }
        return findInterval(entropy, thirdPoints);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "tripleState";
    }
}
