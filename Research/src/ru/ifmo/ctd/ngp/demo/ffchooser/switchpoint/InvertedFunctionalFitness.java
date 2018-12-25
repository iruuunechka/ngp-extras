package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint;

import java.util.List;

import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * Wrapper of {@link FunctionalFitness} that inverts it.
 * 
 * @author Arina Buzdalova
 */
public class InvertedFunctionalFitness extends FunctionalFitness {
	
	private final double constant;
	private final FunctionalFitness fitness;

	/**
	 * Constructs {@link InvertedFunctionalFitness} with the specified {@link FunctionalFitness} to be inverted.
	 * The inverted fitness function has values of <code>constant - original fitness</code>.
	 * @param fitness the specified source of original fitness
	 * @param constant the specified constant used in inversion
	 */
	public InvertedFunctionalFitness(FunctionalFitness fitness, double constant) {
		super(fitness.getFunction());
		this.fitness = fitness;
		this.constant = constant;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFitness(BitString candidate, List<? extends BitString> population) {
		return constant - fitness.getFitness(candidate, population);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNatural() {
		return true;
	}
}
