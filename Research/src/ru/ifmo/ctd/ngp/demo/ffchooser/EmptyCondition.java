package ru.ifmo.ctd.ngp.demo.ffchooser;

/**
 * {@link TargetCondition}, which is never satisfied.
 * 
 * @author Arina Buzdalova
 */

@SuppressWarnings("UnusedDeclaration")
public class EmptyCondition implements TargetCondition<OptimizationAlgorithm> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean targetReached(OptimizationAlgorithm algorithm) {
		return false;
	}
}
