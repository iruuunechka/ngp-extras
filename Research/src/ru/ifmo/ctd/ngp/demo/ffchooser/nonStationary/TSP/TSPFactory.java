package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.TSP;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * @author Irene Petrova
 */
public class TSPFactory extends AbstractCandidateFactory<List<Integer>> {
    private final TSPProblem problem;

    public TSPFactory(TSPProblem tsp) {
        this.problem = tsp;
    }

    @Override
    public List<List<Integer>> generateInitialPopulation(int size, Random rng) {
        return TSPUtils.generateRandomPopulation(size, problem.tsp, rng);
    }

    @Override
    public List<List<Integer>> generateInitialPopulation(int size,
                                                         Collection<List<Integer>> population, Random rng) {
        List<List<Integer>> newPop = new ArrayList<>(population);
        newPop.addAll(TSPUtils.generateRandomPopulation(size - newPop.size(), problem.tsp, rng));
        return newPop;
    }

    @Override
    public List<Integer> generateRandomCandidate(Random rng) {
        return TSPUtils.generateRandomIndividual(problem.tsp, rng);
    }

}
