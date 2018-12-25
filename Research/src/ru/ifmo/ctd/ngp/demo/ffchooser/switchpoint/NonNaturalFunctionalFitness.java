package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint;

import java.util.List;

import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * Wrapper of {@link FunctionalFitness}. It's strategy is "the smaller fitness the fitter individual".
 * 
 * @author Arina Buzdalova
 */
@SuppressWarnings("UnusedDeclaration")
public class NonNaturalFunctionalFitness extends FunctionalFitness {
	
	private final FunctionalFitness fitness;

	/**
	 * Constructs {@link NonNaturalFunctionalFitness} with the specified natural {@link FunctionalFitness}.
	 * @param fitness the specified source of original fitness
	 */
	public NonNaturalFunctionalFitness(FunctionalFitness fitness) {
		super(fitness.getFunction());
		this.fitness = fitness;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFitness(BitString candidate, List<? extends BitString> population) {
		return fitness.getFitness(candidate, population);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNatural() {
		return false;
	}
}
