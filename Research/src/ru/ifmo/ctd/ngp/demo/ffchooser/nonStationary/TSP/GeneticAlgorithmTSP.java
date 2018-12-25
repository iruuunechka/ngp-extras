package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.TSP;

import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.GeneticAlgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Irene Petrova
 */
public class GeneticAlgorithmTSP extends GeneticAlgorithm<List<Integer>> {
    /**
     * Constructs {@link GeneticAlgorithmTSP} with the specified parameters
     *
     * @param targetCriterion the index of target optimization criterion in the evaluators list
     * @param curEvaluator the index of current optimization criterion in the evaluators list
     * @param evaluators the list of possible fitness evaluators
     * @param operators the genetic operators such as crossover and mutation
     */
    protected GeneticAlgorithmTSP(
            int targetCriterion,
            int curEvaluator,
            List<FitnessEvaluator<List<Integer>>> evaluators,
            List<EvolutionaryOperator<List<Integer>>> operators,
            AbstractCandidateFactory<List<Integer>> jsFactory) {
        super(jsFactory, targetCriterion, curEvaluator, evaluators, operators);
    }


    public static GeneticAlgorithmTSP newGAJS(
            int targetCriterion,
            int curEvaluator,
            List<FitnessEvaluator<List<Integer>>> evaluators,
            EvolutionaryOperator<List<Integer>> crossover,
            EvolutionaryOperator<List<Integer>> mutation,
            AbstractCandidateFactory<List<Integer>> jsFactory) {

        return new GeneticAlgorithmTSP(targetCriterion, curEvaluator, evaluators,
                getOperators(crossover, mutation), jsFactory);
    }

    public static List<EvolutionaryOperator<List<Integer>>> getOperators(EvolutionaryOperator<List<Integer>> crossover,
                                                                         EvolutionaryOperator<List<Integer>> mutation) {
        List<EvolutionaryOperator<List<Integer>>> operators = new ArrayList<>();
        operators.add(crossover);
        operators.add(mutation);
        return operators;
    }
    @Override
    protected List<Integer> emptyCandidate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLength(int length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected EvaluatedCandidate<List<Integer>> iterate() {
        iterations++;

        GenerationalEvolutionEngine<List<Integer>> engine =
                new GenerationalEvolutionEngine<>(
                        factory,
                        pipeline,
                        currentEvaluator,
                        selectionStrategy,
                        rng);

        engine.addEvolutionObserver(observer);
        engine.setSingleThreaded(singleThreaded);

        seedPopulation =
                getPopulation(engine.evolvePopulation(generationSize, eliteCount, seedPopulation, new GenerationCount(2)));
        bestIndividual = observer.getLastBestCandidate().getCandidate();
        return observer.getLastBestCandidate();
    }
}

