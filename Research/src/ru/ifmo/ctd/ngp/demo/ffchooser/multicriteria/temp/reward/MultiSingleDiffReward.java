package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.reward;

import ru.ifmo.ctd.ngp.demo.ffchooser.OptimizationAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MultiOptAlgEnvironment;

import java.util.List;

public class MultiSingleDiffReward implements MultiRewardCalculator {
    /**
     * {@inheritDoc}
     * Calculates single change of the target parameter's value
     */
    @Override
    public double calculate(MultiOptAlgEnvironment<?, ?> environment) {
        OptimizationAlgorithm algorithm = environment.getAlgorithm();
        int target = algorithm.getTargetParameter();
        List<Double> prevValues = algorithm.getCurrentBest();
        List<Double> newValues = algorithm.computeValues();
        return newValues.get(target) - prevValues.get(target);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "target-simple";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass();
    }
}
