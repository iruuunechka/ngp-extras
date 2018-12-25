package ru.ifmo.ctd.ngp.demo.paramchooser.experiments;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import java.util.List;

/**
 * @author Arkadii Rost
 */
public interface NumericProblem extends FitnessEvaluator<List<Double>> {
    int getDim();
    double getLowerBound(int i);
    double getUpperBound(int i);
}
