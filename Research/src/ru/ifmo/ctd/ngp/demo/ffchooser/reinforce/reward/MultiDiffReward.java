package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward;

import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.OptimizationAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OptAlgEnvironment;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDef;

/**
 * Simple implementation of the {@link RewardCalculator}.
 * The reward equals with the change of the target parameter's value
 * during several iterations of the {@link OptimizationAlgorithm}.
 * 
 * @author Arina Buzdalova
 */
public class MultiDiffReward implements RewardCalculator {
	private static final long serialVersionUID = -7651296225112739925L;
	private final int iterations;
	
	/**
	 * Constructs {@link MultiDiffReward} with the specified number
	 * of iterations taken to calculate reward
	 * @param iterations the specified number of iterations taken to calculate reward
	 */
	public MultiDiffReward(@ParamDef(name = "iterations") int iterations) {
		this.iterations = iterations;
	}
	
	/**
	 * {@inheritDoc}
	 * Calculates total change of the target parameter's value
	 * during several iterations
	 */
	@Override
	public double calculate(OptAlgEnvironment<?, ?> environment) {
		OptimizationAlgorithm algorithm = environment.getAlgorithm();
		int target = algorithm.getTargetParameter();
		List<Double> prevValues = algorithm.getCurrentBest();		
		for (int i = 0; i < iterations - 1; i++) {
			algorithm.computeValues();
		}
		
		List<Double> newValues = algorithm.computeValues();
		
		return 	newValues.get(target) -	prevValues.get(target);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return String.format("multi-steps%d", iterations);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + iterations;
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
		MultiDiffReward other = (MultiDiffReward) obj;
        return iterations == other.iterations;
    }
}
