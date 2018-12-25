package ru.ifmo.ctd.ngp.demo.paramchooser.experiments;

import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Arkadii Rost
 */
public class NumericMutation implements EvolutionaryOperator<List<Double>> {
    private final NumericProblem problem;
    private final NumberGenerator<Double> sigma;

    public NumericMutation(NumericProblem problem, NumberGenerator<Double> sigma) {
        this.problem = problem;
        this.sigma = sigma;
    }

    @Override
    public List<List<Double>> apply(List<List<Double>> population , Random random) {
        List<List<Double>> res = new ArrayList<>(population.size());
        for (List<Double> individual : population)
            res.add(mutate(individual, random));
        return res;
    }

    protected List<Double> mutate(List<Double> individual, Random random) {
        int dim = problem.getDim();
        List<Double> res = new ArrayList<>(dim);
        for (int i = 0; i < dim; i++) {
            double g = individual.get(i) + random.nextGaussian() * sigma.nextValue();
            g = Math.max(problem.getLowerBound(i), Math.min(problem.getUpperBound(i), g));
            res.add(g);
        }
        return res;
    }
}
