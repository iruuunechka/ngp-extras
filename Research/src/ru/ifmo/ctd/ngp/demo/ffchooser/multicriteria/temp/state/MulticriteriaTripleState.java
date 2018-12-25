package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.state;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MultiOptAlgEnvironment;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDef;

import java.util.List;

/**
 * @author Irene Petrova
 */
public class MulticriteriaTripleState implements MultiStateCalculator<String, Integer> {
    private final double[] firstPoints;
    private final double[] secondPoints = {0.8, 0.9, 0.92, 0.95};
    private final double bestValue;
    private double fitSum;
    private List<List<Double>> curPoints;

        /**
         * Constructs {@link MulticriteriaTripleState} with the specified number of iterations.
         * The length should be equal to the maximal number of iterations
         * @param iter the specified number of iterations
         * @param best the best value of the target criterion
         */
        public MulticriteriaTripleState(@ParamDef(name = "iterations") int iter, @ParamDef(name = "best") double best) {
            this.bestValue = best;
            firstPoints = new double[4];
            for (int i = 0; i < 4; ++i) {
                firstPoints[i] = Math.log(iter) / Math.log(2) / 4 * i;
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
            curPoints = environment.getAlgorithm().getCurrentPoints();

            return firstPart(environment) + secondPart(environment) + thirdPart(environment);
        }

        private double fitSum(MultiOptAlgEnvironment<String, Integer> environment) {
            double fitSum = 0;
            int target = environment.getAlgorithm().getTargetParameter();
            for (List<Double> curPoint : curPoints) {
                fitSum += curPoint.get(target);
            }
            return fitSum;
        }

        private String secondPart(MultiOptAlgEnvironment<String, Integer> environment) {
            double idealSum = curPoints.size() * bestValue;
            fitSum = fitSum(environment);
            return findInterval(fitSum / idealSum, secondPoints);
        }

        private String thirdPart(MultiOptAlgEnvironment<String, Integer> environment) {
            double[] thirdPoints = new double[3];
            int pointsCou = curPoints.size();
            for (int i = 0; i < 3; ++i) {
                thirdPoints[i] = Math.log(pointsCou) / Math.log(2) * (24 + i) / 27;
            }
            double entropy = 0;
            int target = environment.getAlgorithm().getTargetParameter();
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
