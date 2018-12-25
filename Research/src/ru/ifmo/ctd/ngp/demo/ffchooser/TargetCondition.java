package ru.ifmo.ctd.ngp.demo.ffchooser;


/**
 * Interface for observing whether the {@link OptimizationAlgorithm}
 * reached some target condition.
 *  
 * @author Arina Buzdalova
 */
public interface TargetCondition<T extends OptimizationAlgorithm> {
	
	/**
	 * Returns whether the specified {@link OptimizationAlgorithm}
	 * reached the target defined by this condition or not
	 * @param algorithm the specified algorithm
	 * @return whether the <code>algorithm</code> reached the target or not
	 */
    boolean targetReached(T algorithm);
}
