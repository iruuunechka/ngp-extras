package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.TSP;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.EvaluatedIndividual;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.Utils;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.NSGA2Multicriteria;
import ru.ifmo.ctd.ngp.util.CollectionsEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Irene Petrova
 */
public class TSPNSGA2Multicriteria extends NSGA2Multicriteria<List<Integer>> {

    private final TSPProblem problem;

    public TSPNSGA2Multicriteria(FitnessEvaluator<List<Integer>> targetCriterion, List<FitnessEvaluator<List<Integer>>> helpers, AbstractCandidateFactory<List<Integer>> factory, EvolutionaryOperator<List<Integer>> mutation, EvolutionaryOperator<List<Integer>> crossover, double crossoverProbability, int generationSize, Random rng, TSPProblem problem) {
        super(targetCriterion, helpers, factory, mutation, crossover, crossoverProbability, generationSize, rng);
        this.problem = problem;
    }

    @Override
    public List<Individual<List<Integer>>> genChildren(List<EvaluatedIndividual<List<Integer>>> selected) {
        List<Individual<List<Integer>>> generatedIndividuals = new ArrayList<>();
        for (int i = 0; i < selected.size(); ++i) {
            List<Integer> p;
            if (crossoverProbability.nextEvent(rng)) {
                p = crossover.apply(CollectionsEx.listOf(
                        selection.select(selected, rng).ind(),
                        selection.select(selected, rng).ind()
                ), rng).get(0);
            } else {
                p = selection.select(selected, rng).ind();
                p = mutation.apply(CollectionsEx.listOf(p), rng).get(0);
            }
            TSPUtils.apply2Opt(p, problem);
            Individual<List<Integer>> child = new Individual<>(p, Utils.evaluate(p, CollectionsEx.listOf(p), criteria));
            generatedIndividuals.add(child);
        }
        return generatedIndividuals;
    }
}
