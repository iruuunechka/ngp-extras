package ru.ifmo.ctd.ngp.theory.moearl;

import ru.ifmo.ctd.ngp.util.FastRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Irene Petrova
 */
public class EARLNoDecrease  {
    private final int mu;
    private final int n;
    private final BitFitness target;
    private final List<BitFitness> helpers;
    private final BitMutation mutation;
    private final long maxFitnessCalc;
    private final Agent agent;
    private final int targetIndex;
    private final State state;
    private final int optimum;

    private List<Individual> population;
    private boolean optimalFound;
    private final Random rand;
    private int currentCriterion;
    private int maxFitnessInCurPopulation;
    private long fitnessCalcCount;

    private final boolean learnOnMistakes;

    private void setCurrentCriterion(int criterion) {
        currentCriterion = criterion;
    }

    private int[] calculateFitness(boolean[] individual) {
        int[] fitness = new int[helpers.size() + 1];
        for (int j = 0; j < helpers.size(); ++j) {
            fitness[j] = helpers.get(j).calculate(individual);
        }
        fitness[fitness.length - 1] = target.calculate(individual);
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
            maxFitnessInCurPopulation = Math.max(maxFitnessInCurPopulation, population.get(population.size() - 1).fitness[targetIndex]);
        }
        if (maxFitnessInCurPopulation == optimum) {
            optimalFound = true;
        }
        return population;
    }

    public EARLNoDecrease(int mu, int n, BitFitness target, List<BitFitness> helpers, BitMutation mutation,
                          long maxFitnessCalc, Agent agent, State state, int optimum, boolean learnOnMistakes) {
        this.mu = mu;
        this.n = n;
        this.target = target;
        this.helpers = helpers;
        this.mutation = mutation;
        this.maxFitnessCalc = maxFitnessCalc;
        this.agent = agent;
        this.optimum = optimum;
        this.state = state;
        targetIndex = helpers.size();
        optimalFound = false;
        rand = FastRandom.threadLocal();
        fitnessCalcCount = 0;
        this.learnOnMistakes = learnOnMistakes;
    }

    public long run() {
        population = generateRandomInitialPopulation();
        fitnessCalcCount += population.size();
        while (!optimalFound && (fitnessCalcCount < maxFitnessCalc)) {
            makeIteration();
            fitnessCalcCount += 1;
//            System.out.println("population size: " + population.size() + " maxfitness: " + maxFitness + " criterion: " + currentCriterion);
        }
        return fitnessCalcCount;
    }

    private int removeWorst() {
        int minFitness = Integer.MAX_VALUE;
        int maxTarget = Integer.MIN_VALUE;
        int reward = 0;
        Individual indToRemove = null;
        for (Individual ind : population) {
            if (ind.fitness[currentCriterion] < minFitness) {
                minFitness = ind.fitness[currentCriterion];
                indToRemove = ind;
            }
            if (ind.fitness[targetIndex] > maxTarget) {
                maxTarget = ind.fitness[targetIndex];
            }
        }
        if (population.indexOf(indToRemove) == 1) {
            population.remove(indToRemove);
            return reward;
        }
        if (indToRemove == null) {
            throw new AssertionError("Something's going wrong");
        }
        if (indToRemove.fitness[targetIndex] == maxTarget) {
            int secondFitness = maxTarget;
            Individual indWithSecondFitness = null;
            for (Individual ind : population) {
                if (ind.fitness[targetIndex] < secondFitness && !ind.equals(indToRemove)) {
                    secondFitness = ind.fitness[targetIndex];
                    indWithSecondFitness = ind;
                }
            }
            if (indWithSecondFitness != null) {
                reward = indWithSecondFitness.fitness[targetIndex] - maxTarget;
                indToRemove = indWithSecondFitness;

            }
        }
        population.remove(indToRemove);
        return reward;
    }

    private void makeIteration() {
        int prevMaxFitness = maxFitnessInCurPopulation;
        int curState = state.getCurrentState(population, targetIndex);
        int curAction = agent.selectAction(curState);
        setCurrentCriterion(curAction);
        int randIndividual = rand.nextInt(population.size());
        boolean[] mutatedIndividual = mutation.mutate(population.get(randIndividual).individual);
        population.add(new Individual(mutatedIndividual, calculateFitness(mutatedIndividual)));
        int reward = removeWorst();
        maxFitnessInCurPopulation = findMaxFitnessInCurPopulation();
        if (maxFitnessInCurPopulation == optimum) {
            optimalFound = true;
        }
        if (learnOnMistakes) {
            reward = (reward < 0) ? reward : maxFitnessInCurPopulation - prevMaxFitness;
        } else {
            reward = maxFitnessInCurPopulation - prevMaxFitness;
        }
        agent.updateExperience(curState, state.getCurrentState(population, targetIndex), curAction, reward);
    }

    private int findMaxFitnessInCurPopulation() {
        int max = Integer.MIN_VALUE;
        for (Individual ind : population) {
            max = Math.max(max, ind.fitness[targetIndex]);
        }
        return max;
    }
}
