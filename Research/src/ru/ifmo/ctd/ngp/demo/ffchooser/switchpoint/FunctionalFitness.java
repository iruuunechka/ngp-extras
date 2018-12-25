package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import ru.ifmo.ctd.ngp.demo.ffchooser.utils.GeneticUtils;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * Fitness evaluator that constructs from {@link RealFunction}.
 * The <code>true</code> bits count is passed as an argument to
 * the {@link RealFunction} to evaluate the fitness.
 * 
 * @author Arina Buzdalova
 */
public class FunctionalFitness implements FitnessEvaluator<BitString> {

	private final RealFunction function;
	
	/**
	 * <p>
	 * Constructs this fitness evaluator with the specified
	 * {@link RealFunction}.
	 * </p><p>
	 * The <code>true</code> bits count is passed as an argument to
	 * the {@link RealFunction} to evaluate fitness.
	 * </p>
	 * @param function the specified integer function
	 */
	public FunctionalFitness(RealFunction function) {
		this.function = function;
	}
	
	/**
	 * Gets the inner {@link RealFunction} that is used to calculate
	 * fitness value
	 * @return the function that is used to calculate fitness value
	 */
	public RealFunction getFunction() {
		return function;
	}
	
	/**
	 * Calculates the difference ratio of this fitness function's values and its arguments 
	 * with the specified difference between the arguments
	 * 
	 * @param individual the argument of this fitness function
	 * @param delta the specified difference between the arguments
	 * @return difference ratio of this function's values and its arguments
	 */
	public double deltaRatio(BitString individual, double delta) {
		return function.deltaRatio(GeneticUtils.countBits(individual), delta);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFitness(BitString candidate, List<? extends BitString> population) {
		return function.getValue((GeneticUtils.countBits(candidate)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNatural() {
		return true;
	}

}
