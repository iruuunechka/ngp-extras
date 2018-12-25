package ru.ifmo.ctd.ngp.theory.moearl;

import ru.ifmo.ctd.ngp.util.FastRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Irene Petrova
 */
public class EARL {
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
    public int maxFitnessInCurPopulation;
    private long fitnessCalcCount;


    public void setCurrentCriterion(int criterion) {
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

    public EARL(int mu, int n, BitFitness target, List<BitFitness> helpers, BitMutation mutation, long maxFitnessCalc, Agent agent, State state, int optimum) {
        this.mu = mu;
        this.n = n;
        this.target = target;
        this.helpers = helpers;
        this.mutation = mutation;
        this.maxFitnessCalc = maxFitnessCalc;
        this.agent = agent;
        this.state = state;
        this.optimum = optimum;
        targetIndex = helpers.size();
        optimalFound = false;
        rand = FastRandom.threadLocal();
        fitnessCalcCount = 0;
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

    private void removeWorst() {
        int minFitness = Integer.MAX_VALUE;
        Individual indToRemove = null;
        for (Individual ind : population) {
            if (ind.fitness[currentCriterion] < minFitness) {
                minFitness = ind.fitness[currentCriterion];
                indToRemove = ind;
            }
        }
        population.remove(indToRemove);
    }

    //Iteration with simple state
//    private void makeIteration() {
//        int curState = maxFitnessInCurPopulation;
//        int curAction = agent.selectAction(curState);
//        setCurrentCriterion(curAction);
//        int randIndividual = rand.nextInt(population.size());
//        boolean[] mutatedIndividual = mutation.mutate(population.get(randIndividual).individual);
//        population.add(new Individual(mutatedIndividual, calculateFitness(mutatedIndividual)));
//        removeWorst();
//        maxFitnessInCurPopulation = findMaxFitnessInCurPopulation();
//        if (maxFitnessInCurPopulation == optimum) {
//            optimalFound = true;
//        }
//        int reward = maxFitnessInCurPopulation - curState;
//        agent.updateExperience(curState, maxFitnessInCurPopulation, curAction, reward);
//    }

    private void makeIteration() {
        int prevMaxFitness = maxFitnessInCurPopulation;
        int curState = state.getCurrentState(population, targetIndex);
        int curAction = agent.selectAction(curState);
        setCurrentCriterion(curAction);
        int randIndividual = rand.nextInt(population.size());
        boolean[] mutatedIndividual = mutation.mutate(population.get(randIndividual).individual);
        population.add(new Individual(mutatedIndividual, calculateFitness(mutatedIndividual)));
        removeWorst();
        maxFitnessInCurPopulation = findMaxFitnessInCurPopulation();
        if (maxFitnessInCurPopulation == optimum) {
            optimalFound = true;
        }
        int reward = maxFitnessInCurPopulation - prevMaxFitness;
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

