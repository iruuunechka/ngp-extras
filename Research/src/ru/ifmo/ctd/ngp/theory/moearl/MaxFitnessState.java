package ru.ifmo.ctd.ngp.theory.moearl;

import java.util.List;

/**
 * @author Irene Petrova
 */
public class MaxFitnessState implements State {
    @Override
    public int getCurrentState(List<Individual> population, int targetIndex) {
        int max = Integer.MIN_VALUE;
        //this foreach loop is a bottleneck
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < population.size(); ++i) {
            max = Math.max(max, population.get(i).fitness[targetIndex]);
        }
        return max;
    }
}
