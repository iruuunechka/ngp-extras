package ru.ifmo.ctd.ngp.theory.moearl;

import ru.ifmo.ctd.ngp.util.FastRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author Irene Petrova
 */
public class SEMORL {
    private final int mu;
    private final int n;
    private final BitMaskFitness target;
    private final List<BitFitness> helpers;
    private final BitMutation mutation;
    private final int maxFitnessCalc;
    private final Agent agent;
    private final int targetIndex;

    private List<Individual> population;
    private boolean optimalFound;
    private final Random rand;
    private int currentCriterion;
    public int maxFitness;
    private int fitnessCalcCount;


    public void setCurrentCriterion(int criterion) {
        currentCriterion = criterion;
    }

    private int[] calculateFitness(boolean[] individual) {
        int[] fitness = new int[helpers.size() + 1];
        for (int j = 0; j < fitness.length - 1; ++j) {
            fitness[j] = helpers.get(j).calculate(individual);
        }
        fitness[targetIndex] = target.calculate(individual);
        return fitness;
    }

    private List<Individual> generateRandomInitialPopulation() {
        List<Individual> population = new ArrayList<>();
        for (int i = 0; i < mu; i++) {
            boolean[] individual = new boolean[n];
            for (int j = 0; j < n; ++j) {
                individual[j] = rand.nextBoolean();
            }
            population.add(new Individual(individual, calculateFitness(individual)));
            maxFitness = Math.max(maxFitness, population.get(population.size() - 1).fitness[targetIndex]);
        }
        if (maxFitness == n) {
            optimalFound = true;
        }
        return population;
    }

    public SEMORL(int mu, int n, BitMaskFitness target, List<BitFitness> helpers, BitMutation mutation, int maxFitnessCalc, Agent agent) {
        this.mu = mu;
        this.n = n;
        this.target = target;
        this.helpers = helpers;
        this.mutation = mutation;
        this.maxFitnessCalc = maxFitnessCalc;
        this.agent = agent;
        targetIndex = helpers.size();
        optimalFound = false;
        rand = FastRandom.threadLocal();
        fitnessCalcCount = 0;
    }

    public int run() {
        population = generateRandomInitialPopulation();
        fitnessCalcCount += population.size();
        while (!optimalFound && (fitnessCalcCount < maxFitnessCalc)) {
            makeIteration();
            fitnessCalcCount += 1;
//           System.out.println("population size: " + population.size() + " maxfitness: " + maxFitness + " criterion: " + currentCriterion);
        }
        return fitnessCalcCount;
    }

    private List<Individual> nonDominatedSort() {
        int[] currentCriterions = new int[2];
        currentCriterions[0] = targetIndex;
        currentCriterions[1] = currentCriterion;
        List<Individual> filteredPopulation = new ArrayList<>();
        for (int ind1 = 0; ind1 < population.size(); ++ind1) {
            boolean removeInd1 = false;
            for (int ind2 = 0; ind2 < population.size(); ++ind2) {
                if (ind1 == ind2) {
                    continue;
                }
                removeInd1 = true;
                boolean equal = true;
                for (int criterion : currentCriterions) {
                    if (population.get(ind1).fitness[criterion] > population.get(ind2).fitness[criterion]) {
                        removeInd1 = false;
                        equal = false;
                        break;
                    } else if (population.get(ind1).fitness[criterion] < population.get(ind2).fitness[criterion]) {
                        equal = false;
                    }
                }
                if (equal) {
                    removeInd1 = false;
                }
                if (removeInd1) {
                    break;
                }                                                                      }
            if (!removeInd1) {
                filteredPopulation.add(population.get(ind1));
            }
        }
        return removeDuplicates(filteredPopulation);
    }

    public List<Individual> removeDuplicates(List<Individual> individuals) {
        List<Individual> filtered = new ArrayList<>();
        for (Individual ind : individuals) {
            boolean notInFiltered = true;
            for (Individual filt : filtered) {
                if (Arrays.equals(ind.fitness, filt.fitness)) {
                    notInFiltered = false;
                    break;
                }
            }
            if (notInFiltered) {
                filtered.add(ind);
            }
        }
        return filtered;
    }

    private void makeIteration() {
        int curState = maxFitness;
        int curAction = agent.selectAction(curState);
        setCurrentCriterion(curAction);
        int randIndividual = rand.nextInt(population.size());
        boolean[] mutatedIndividual = mutation.mutate(population.get(randIndividual).individual);
        population.add(new Individual(mutatedIndividual, calculateFitness(mutatedIndividual)));
        maxFitness = Math.max(maxFitness, population.get(population.size() - 1).fitness[targetIndex]);
        if (maxFitness == n) {
            optimalFound = true;
        }
        population = nonDominatedSort();
        int reward = maxFitness - curState;
        agent.updateExperience(curState, maxFitness, curAction, reward);
    }

}
