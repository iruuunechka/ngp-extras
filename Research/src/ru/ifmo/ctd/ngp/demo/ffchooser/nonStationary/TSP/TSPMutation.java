package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.TSP;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Irene Petrova
 */
public class TSPMutation implements EvolutionaryOperator<List<Integer>> {
    /**
     * {@inheritDoc}
     */
    @Override
    public List<List<Integer>> apply(List<List<Integer>> population, Random rng) {
        List<List<Integer>> mutated = new ArrayList<>();
        for (List<Integer> individual : population) {
            mutated.add(mutate(individual, rng));
        }
        return mutated;
    }

//    private List<Integer> mutate(List<Integer> individual, Random rng) {
//        int len = individual.size();
//        int pos1 = rng.nextInt(len);
//        int pos2 = rng.nextInt(len);
//
//        List<Integer> mutated = new ArrayList<>(individual);
//        Integer removed = mutated.remove(pos1);
//        mutated.add(pos2, removed);
//
//        return mutated;
//    }
    public List<Integer> mutate(List<Integer> individual, Random rng) {
        int len = individual.size();
        int pos1 = rng.nextInt(len);
        int pos2 = rng.nextInt(len);
        while (pos2 == pos1) {
            pos2 = rng.nextInt(len);
        }

        List<Integer> mutated = new ArrayList<>(individual);
        int segment = pos2 > pos1 ? pos2 - pos1 + 1 : pos2 - pos1 + len + 1;
        for (int i = 0; i <= segment / 2 - 1; ++i) {
            int curPos1 = (pos1 + i) % len;
            int curPos2 = (pos2 - i) % len;
            if (curPos2 < 0) {
                curPos2 += len;
            }

            int tmp = mutated.get(curPos1);
            mutated.set(curPos1, mutated.get(curPos2));
            mutated.set(curPos2, tmp);
        }

        return mutated;
    }
}
