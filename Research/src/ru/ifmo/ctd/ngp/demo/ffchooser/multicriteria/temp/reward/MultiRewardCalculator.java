package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.reward;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MultiOptAlgEnvironment;

import java.io.Serializable;

/**
 * Reward calculator, which is used by {@link ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MultiOAEnvironment}.
 * The calculation is based on changes in the performance of the
 * {@link ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MulticriteriaOptimizationAlgorithm} encapsulated by the environment.
 *
 */
public interface MultiRewardCalculator extends Serializable{

    /**
     * Calculates the reward based on changes in the specified environments's performance.
     * Interacts with the algorithm encapsulated by the environment.
     * @param environment the specified environment
     * @return the reward
     */
    double calculate(MultiOptAlgEnvironment<?, ?> environment);

    /**
     * Gets short mnemonic string that represents this reward calculator.
     * Names of calculators can be equal only if they are equal themself.
     * @return short mnemonic string that represents this reward calculator
     */
    String getName();

    /**
     * {@inheritDoc}
     */
    @Override
    boolean equals(Object obj);

    /**
     * {@inheritDoc}
     */
    @Override
    int hashCode();
}

