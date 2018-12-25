package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.TSP;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import java.util.List;

/**
 * @author Irene Petrova
 */
public class TSPFitness implements FitnessEvaluator<List<Integer>> {
    private final double max;
    private final TSPProblem problem;

    /**
     * Constructs the {@link TSPFitness} with the specified parameters.
     *
     */
    public TSPFitness(TSPProblem problem) {
        this.max = problem.getMax();
        this.problem = problem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getFitness(List<Integer> individual, List<? extends List<Integer>> population) {
        double flowTimes = TSPUtils.evalTime(individual, problem.tsp);
        if (flowTimes == Double.POSITIVE_INFINITY) {
            flowTimes = 0;
        }
        return max - flowTimes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNatural() {
        return true;
    }

}

