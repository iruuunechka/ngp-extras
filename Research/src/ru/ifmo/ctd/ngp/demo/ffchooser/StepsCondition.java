package ru.ifmo.ctd.ngp.demo.ffchooser;

/**
 * {@link TargetCondition}, which is based on the 
 * number of iterations performed by {@link OptimizationAlgorithm}.
 * 
 * @author Arina Buzdalova
 */
public class StepsCondition implements TargetCondition<OptimizationAlgorithm> {
	private final int iterations;
	
	/**
	 * Constructs {@link StepsCondition} with the specified
	 * number of iterations to be performed by 
	 * {@link OptimizationAlgorithm} in order to satisfy
	 * this condition
	 * @param iterations the specified number of iterations
	 */
	public StepsCondition(int iterations) {
		this.iterations = iterations;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean targetReached(OptimizationAlgorithm algorithm) {
		return algorithm.getIterationsNumber() >= iterations;
	}

}
