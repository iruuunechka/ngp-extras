package ru.ifmo.ctd.ngp.demo.paramchooser.experiments;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Arkadii Rost
 */
public class NumericCandidateFactory extends AbstractCandidateFactory<List<Double>> {
    private final NumericProblem problem;

    public NumericCandidateFactory(NumericProblem problem) {
        this.problem = problem;
    }

    @Override
    public List<Double> generateRandomCandidate(Random random) {
        int dim = problem.getDim();
        List<Double> res = new ArrayList<>(dim);
        for (int i = 0; i < dim; i++) {
            double upperBound = problem.getUpperBound(i);
            double lowerBound = problem.getLowerBound(i);
            res.add(lowerBound + random.nextDouble() * (upperBound - lowerBound));
        }
        return res;
    }
}
