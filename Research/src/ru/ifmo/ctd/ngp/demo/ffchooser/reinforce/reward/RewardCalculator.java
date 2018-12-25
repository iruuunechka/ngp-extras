package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward;

import java.io.Serializable;

import ru.ifmo.ctd.ngp.demo.ffchooser.OptimizationAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OAEnvironment;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OptAlgEnvironment;

/**
 * Reward calculator, which is used by {@link OAEnvironment}.
 * The calculation is based on changes in the performance of the
 * {@link OptimizationAlgorithm} encapsulated by the environment.
 * 
 * @author Arina Buzdalova
 */
public interface RewardCalculator extends Serializable {
	/**
	 * Calculates the reward based on changes in the specified environments's performance.
	 * Interacts with the algorithm encapsulated by the environment.
	 * @param environment the specified environment
	 * @return the reward
	 */
    double calculate(OptAlgEnvironment<?, ?> environment);
	
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
