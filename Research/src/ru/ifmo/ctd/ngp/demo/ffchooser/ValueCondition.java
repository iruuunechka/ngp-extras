package ru.ifmo.ctd.ngp.demo.ffchooser;

/**
 * {@link TargetCondition}, which is based on the current
 * value of the target criteria, 
 * which should be reached by {@link OptimizationAlgorithm}
 * in order to satisfy this condition.
 * 
 * @author Arina Buzdalova
 */
public class ValueCondition implements TargetCondition<OptimizationAlgorithm> {
	private final double bestValue;
	
	/**
	 * Constructs {@link ValueCondition} with the specified
	 * value of the target criteria. This value should be reached
	 * by {@link OptimizationAlgorithm} in order to satisfy 
	 * this condition.
	 * @param bestValue the specified value of the target criteria to be reached
	 */
	public ValueCondition(double bestValue) {
		this.bestValue = bestValue;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean targetReached(OptimizationAlgorithm algorithm) {
		return algorithm.getBestTargetValue() >= bestValue;
	}

}
