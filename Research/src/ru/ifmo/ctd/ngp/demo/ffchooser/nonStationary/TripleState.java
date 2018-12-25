package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary;

import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OptAlgEnvironment;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.StateCalculator;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDef;

import java.util.List;

/**
 * Strange state which consists of three parts. Was implemented by Meynster
 * @author Irene Petrova
 */
public class TripleState implements StateCalculator<String, Integer> {
    private final double[] firstPoints;
    private final double[] secondPoints = {0, 0.6, 0.8, 0.9, 0.91, 0.92, 0.95, 0.96, 0.97};//{0, 0.3, 0.4, 0.6};
    private final double bestValue;

    /**
     * Constructs {@link TripleState} with the specified number of iterations.
     * The length should be equal to the maximal number of iterations
     * @param iter the specified number of iterations
     * @param best the best value of the target criterion
     */
    public TripleState(@ParamDef(name = "iterations") int iter, @ParamDef(name = "best") double best) {
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

    private String firstPart(OptAlgEnvironment<String, Integer> environment) {
        int curIter = environment.getAlgorithm().getIterationsNumber() + 1;
        return findInterval(Math.log(curIter) / Math.log(2), firstPoints);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(OptAlgEnvironment<String, Integer> environment) {
        List<List<Double>> curPoints = environment.getAlgorithm().getCurrentPoints();
//        String s = firstPart(environment) + secondPart(environment) + thirdPart(environment);
        return firstPart(environment) + secondPart(environment, curPoints) + thirdPart(environment, curPoints);
    }

    private double fitSum(OptAlgEnvironment<String, Integer> environment, List<List<Double>> curPoints) {
        double fitSum = 0;
        int target = environment.getAlgorithm().getTargetParameter();
        for (List<Double> curPoint : curPoints) {
            fitSum += curPoint.get(target);
        }
        return fitSum;
    }

    private String secondPart(OptAlgEnvironment<String, Integer> environment, List<List<Double>> curPoints) {
        double idealSum = environment.getAlgorithm().getCurrentPoints().size() * bestValue;
        return findInterval(fitSum(environment, curPoints) / idealSum, secondPoints);
    }

    private String thirdPart(OptAlgEnvironment<String, Integer> environment, List<List<Double>> curPoints) {
        int thirdPointsCou = 10;
        double[] thirdPoints = new double[thirdPointsCou];
        int pointsCou = curPoints.size();
        for (int i = 0; i < thirdPointsCou; ++i) {
            thirdPoints[i] = Math.log(pointsCou) / Math.log(2) / thirdPointsCou * i;
        }
        double entropy = 0;
        int target = environment.getAlgorithm().getTargetParameter();
        double fitSum = fitSum(environment, curPoints);
        for (List<Double> curPoint : curPoints) {
            double pm = curPoint.get(target) / fitSum;
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
