package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.TSP;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import java.util.List;

/**
 * @author Irene Petrova
 */
public class TSPKnowlesFitness implements FitnessEvaluator<List<Integer>> {
    private final double max;
    private final TSPProblem problem;
    private final int a;
    private final int b;

    /**
     * Constructs the {@link TSPFitness} with the specified parameters.
     *
     * @param a start point
     * @param b end point
     */
    public TSPKnowlesFitness(TSPProblem problem, int a, int b) {
        this.max = problem.getMax();
        this.problem = problem;
        this.b = b;
        this.a = a;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getFitness(List<Integer> individual, List<? extends List<Integer>> population) {
        double flowTimes = 0;
        int posA = 0;
        int posB = 0;
        for (int i = 0; i < individual.size(); ++i) {
            if (a == individual.get(i)) {
                posA = i;
            }
            if (b == individual.get(i)) {
                posB = i;
            }
        }
        int curCityPos = posA;
//        int curCityPos = Math.min(posA, posB);
//        posB = Math.max(posA, posB);
        int pathLength = individual.size();
        int curCity;
        int nextCityPos;
        int nextCity;
        while (curCityPos != posB) {
            curCity = individual.get(curCityPos);
            nextCityPos = (curCityPos + 1) % pathLength;
            nextCity = individual.get(nextCityPos);
            flowTimes += problem.tsp[curCity][nextCity];
            curCityPos = nextCityPos;
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
