package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.TSP;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import java.util.*;

/**
 * @author Irene Petrova
 */
public class TSPJensenOperator implements EvolutionaryOperator<List<Integer>>{
    private final TSPCrossover crossover;
    private final TSPMutation mutation;

    public TSPJensenOperator(TSPCrossover crossover, TSPMutation mutation, TSPProblem problem) {
        this.crossover = crossover;
        this.mutation = mutation;
    }

    @Override
    public List<List<Integer>> apply(List<List<Integer>> selectedCandidates, Random rng) {
        List<List<Integer>> selectionClone = new ArrayList<>(selectedCandidates);
        selectionClone.addAll(new ArrayList<>(selectedCandidates));
        Collections.shuffle(selectionClone, rng);
        List<List<Integer>> result = new ArrayList<>(selectedCandidates.size());
        Iterator<List<Integer>> iterator = selectionClone.iterator();
        while (iterator.hasNext()) {
            List<Integer> parent1 = iterator.next();
            if (iterator.hasNext()) {
                List<Integer> parent2 = iterator.next();
                if (crossover.crossoverProbability > rng.nextDouble()) {
                    List<Integer> cur = crossover.mate(parent1, parent2, rng);
                    result.add(cur);
                } else {
                    if (rng.nextBoolean()) {
                        List<Integer> cur = mutation.mutate(parent1, rng);
                        result.add(cur);
                    } else {
                        List<Integer> cur = mutation.mutate(parent2, rng);
                        result.add(cur);
                    }
                }
            } else {
                List<Integer> cur = mutation.mutate(parent1, rng);
                result.add(cur);
            }
        }
        return result;
    }
}
