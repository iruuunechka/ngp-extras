package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.TSP;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import java.util.List;

/**
 * @author Irene Petrova
 */
public class TSPJahneFitness implements FitnessEvaluator<List<Integer>> {
    private final double max;
    private final TSPProblem problem;
    private final List<Integer> points;

    /**
     * Constructs the {@link TSPFitness} with the specified parameters.
     *
     * @param points the points used to split the tours while computing auxiliary fitness values.
     */
    public TSPJahneFitness(TSPProblem problem, List<Integer> points) {
        this.points = points;
        this.max = problem.getMax();
        this.problem = problem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getFitness(List<Integer> individual, List<? extends List<Integer>> population) {
        double flowTimes = 0;
        for (int p : points) {
            int pointPos = 0;
            for (int i = 0; i < individual.size(); ++i) {
                if (p == individual.get(i)) {
                    pointPos = i;
                    break;
                }
            }
            int cPrevPos = pointPos == 0 ? individual.size() - 1 : pointPos - 1;
            int cNextPos = pointPos == individual.size() - 1 ? 0 : pointPos + 1;
            int cPrev = individual.get(cPrevPos);
            int cNext = individual.get(cNextPos);
            flowTimes += problem.tsp[cPrev][p] + problem.tsp[p][cNext];
        }

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

