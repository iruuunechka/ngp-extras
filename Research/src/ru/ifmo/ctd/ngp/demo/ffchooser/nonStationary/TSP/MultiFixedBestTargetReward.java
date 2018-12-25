package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.TSP;

import ru.ifmo.ctd.ngp.demo.ffchooser.OptimizationAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MultiOptAlgEnvironment;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.reward.MultiRewardCalculator;

/**
 * Implementation of the {@link MultiRewardCalculator}
 * with fixed rewards for the negative, zero and positive
 * changes of the target parameter.
 * It takes into account the best target value in population.
 *
 * @author Irene Petrova
 *
 */
public class MultiFixedBestTargetReward implements MultiRewardCalculator {
    private final double negative;
    private final double zero;
    private final double positive;

    /**
     * Constructs {@link MultiFixedBestTargetReward} reward calculator with the
     * specified rewards for different kind of the target parameter's change
     *
     * @param negative reward for a negative change of the target parameter
     * @param zero reward for a zero change of the target parameter
     * @param positive reward for a positive change of the target parameter
     */
    public MultiFixedBestTargetReward(double negative, double zero, double positive) {
        this.negative = negative;
        this.zero = zero;
        this.positive = positive;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double calculate(MultiOptAlgEnvironment<?, ?> environment) {
        OptimizationAlgorithm algorithm = environment.getAlgorithm();
        double prevValue = algorithm.getBestTargetValue();
        algorithm.computeValues();
        double newValue = algorithm.getBestTargetValue();
        double delta = newValue - prevValue;

        if (delta < 0) {
            return negative;
        }

        if (delta == 0) {
            return zero;
        }

        if (delta > 0) {
            return positive;
        }

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return String.format("multi-fixed-best-target%.2fn%.2fz%.2fp", negative, zero, positive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(negative);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(positive);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(zero);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MultiFixedBestTargetReward other = (MultiFixedBestTargetReward) obj;
        return Double.doubleToLongBits(negative) == Double.doubleToLongBits(other.negative)
                && Double.doubleToLongBits(positive) == Double.doubleToLongBits(other.positive)
                && Double.doubleToLongBits(zero) == Double.doubleToLongBits(other.zero);
    }

}
