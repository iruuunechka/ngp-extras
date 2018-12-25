package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.TSP;

import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.EvolutionStrategyEngine;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import java.util.List;

/**
 * @author Irene Petrova
 */
public class EvolutionStrategyTSP extends GeneticAlgorithmTSP {
    private final boolean plus;
    private final int multiplier;

    /**
     * Creates {@link EvolutionStrategyTSP} with the specified parameters
     *
     * @param targetCriterion the index of target optimization criteria in the evaluators list
     * @param curEvaluator the index of current optimization criteria in the evaluators list
     * @param evaluators the list of possible fitness evaluators
     * @param plus the type of evolution strategy
     * @param multiplier the multiplier used in this evolution strategy
     */
    public EvolutionStrategyTSP(int targetCriterion,
                                int curEvaluator,
                                List<FitnessEvaluator<List<Integer>>> evaluators,
                                List<EvolutionaryOperator<List<Integer>>> operators,
                                AbstractCandidateFactory<List<Integer>> tspFactory,
                                boolean plus, int multiplier) {
        super(targetCriterion, curEvaluator, evaluators, operators, tspFactory);
        this.plus = plus;
        this.multiplier = multiplier;
    }

    public EvolutionStrategyTSP (int targetCriterion,
                                 int curEvaluator,
                                 List<FitnessEvaluator<List<Integer>>> evaluators,
                                 EvolutionaryOperator<List<Integer>> crossover,
                                 EvolutionaryOperator<List<Integer>> mutation,
                                 AbstractCandidateFactory<List<Integer>> jsFactory,
                                 boolean plus, int multiplier) {

         super(targetCriterion, curEvaluator, evaluators,
                getOperators(crossover, mutation), jsFactory);
        this.plus = plus;
        this.multiplier = multiplier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected EvaluatedCandidate<List<Integer>> iterate() {
        iterations++;

        EvolutionStrategyEngine<List<Integer>> engine = new EvolutionStrategyEngine<>(
                factory, pipeline, currentEvaluator, plus, multiplier, rng);

        engine.addEvolutionObserver(observer);

        seedPopulation =
                getPopulation(engine.evolvePopulation(generationSize, eliteCount, seedPopulation, new GenerationCount(2)));

        bestIndividual = observer.getLastBestCandidate().getCandidate();
        return observer.getLastBestCandidate();
    }
}
