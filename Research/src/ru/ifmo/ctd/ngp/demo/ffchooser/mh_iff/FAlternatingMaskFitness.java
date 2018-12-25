package ru.ifmo.ctd.ngp.demo.ffchooser.mh_iff;

import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalFitness;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * Fitness function that calculates number of matches with the
 * 101010...10 mask.
 * 
 * @author Arina Buzdalova
 *
 */
public class FAlternatingMaskFitness extends FunctionalFitness {
	private final AlternatingMaskFitness<BitString> fitness;

	/**
	 * Constructs {@link FAlternatingMaskFitness} without any parameters.
	 */
	public FAlternatingMaskFitness() {
		super(null);
		this.fitness = new AlternatingMaskFitness<>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFitness(BitString candidate, List<? extends BitString> population) {
		return fitness.getFitness(candidate, population);
	}
	
	/**
	 * The operation is not supported for this type of fitness function 
	 */
	@Override
	public double deltaRatio(BitString individual, double delta) {
		throw new UnsupportedOperationException();
	}
}
