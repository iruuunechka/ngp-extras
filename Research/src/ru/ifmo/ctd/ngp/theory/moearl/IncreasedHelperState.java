package ru.ifmo.ctd.ngp.theory.moearl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author Irene Petrova
 */
public class IncreasedHelperState implements State {
    private final Random rand  = new Random();
    private List<Individual> prevPopulation = new ArrayList<>();

    @Override
    public int getCurrentState(List<Individual> population, int targetIndex) {
        if (prevPopulation.isEmpty()) {
            while (true) {
                int state = rand.nextInt(population.size());
                if (state != targetIndex) {
                    return state;
                }
            }
        }
        int criterionNumber = population.get(0).fitness.length;
        double[] averageFitness = new double[criterionNumber];
        Arrays.fill(averageFitness, 0);
        for (Individual ind : population) {
            for (int i = 0; i < criterionNumber; ++i) {
                averageFitness[i] += ind.fitness[i];
            }
        }
//        for (int i = 0; i < criterionNumber; ++i) {
//            averageFitness[i] /= population.size();
//        }
        int maxCriterion = Integer.MIN_VALUE;
        for (int i = 0; i < criterionNumber; ++i) {
            if (averageFitness[i] > maxCriterion && i != targetIndex) {
                maxCriterion = i;
            }
        }
        prevPopulation = new ArrayList<>(population);
        return maxCriterion;
    }
}
