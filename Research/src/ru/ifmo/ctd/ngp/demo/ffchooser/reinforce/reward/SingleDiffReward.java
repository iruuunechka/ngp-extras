package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward;

import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.OptimizationAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OptAlgEnvironment;

/**
 * Simple implementation of the {@link RewardCalculator}.
 *  
 * @author Arina Buzdalova
 */
public class SingleDiffReward implements RewardCalculator {
	private static final long serialVersionUID = 2843313820934830096L;

	/**
	 * {@inheritDoc}
	 * Calculates single change of the target parameter's value
	 */
	@Override
	public double calculate(OptAlgEnvironment<?, ?> environment) {
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
