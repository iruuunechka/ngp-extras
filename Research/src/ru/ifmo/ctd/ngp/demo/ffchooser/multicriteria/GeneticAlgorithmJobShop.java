package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.GeneticAlgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Irene Petrova
 */
public class GeneticAlgorithmJobShop extends GeneticAlgorithm<List<Integer>> {
    /**
     * Constructs {@link GeneticAlgorithmJobShop} with the specified parameters
     *
     * @param targetCriterion the index of target optimization criterion in the evaluators list
     * @param curEvaluator the index of current optimization criterion in the evaluators list
     * @param evaluators the list of possible fitness evaluators
     * @param operators the genetic operators such as crossover and mutation
     */
    protected GeneticAlgorithmJobShop(
            int targetCriterion,
            int curEvaluator,
            List<FitnessEvaluator<List<Integer>>> evaluators,
            List<EvolutionaryOperator<List<Integer>>> operators,
            AbstractCandidateFactory<List<Integer>> jsFactory) {
        super(jsFactory, targetCriterion, curEvaluator, evaluators, operators);
    }


    public static GeneticAlgorithmJobShop newGAJS(
            int targetCriterion,
            int curEvaluator,
            List<FitnessEvaluator<List<Integer>>> evaluators,
            EvolutionaryOperator<List<Integer>> crossover,
            EvolutionaryOperator<List<Integer>> mutation,
            AbstractCandidateFactory<List<Integer>> jsFactory) {
        List<EvolutionaryOperator<List<Integer>>> operators = new ArrayList<>();
        operators.add(crossover);
        operators.add(mutation);
        return new GeneticAlgorithmJobShop(targetCriterion, curEvaluator, evaluators,
                operators, jsFactory);
    }
    @Override
    protected List<Integer> emptyCandidate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLength(int length) {
        throw new UnsupportedOperationException();
    }
}

