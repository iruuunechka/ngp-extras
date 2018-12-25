package ru.ifmo.ctd.ngp.demo.ffchooser.xvector;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * Calculates fitness function of the individuals of the xVector problem
 * by casting to integer the value of the specified fitness function.
 *  
 * @author Arina Buzdalova
 */
public class IntFitness implements FitnessEvaluator<BitString> {

	private final FitnessEvaluator<BitString> evaluator;
	
	/** 
	 * Constructs {@link IntFitness} with the specified fitness evaluator
	 * @param evaluator the fitness evaluator, whose values will be casted to integer
	 */
	public IntFitness(FitnessEvaluator<BitString> evaluator) {
		this.evaluator = evaluator;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFitness(BitString candidate, List<? extends BitString> population) {
		return (int)(evaluator.getFitness(candidate, population));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNatural() {
		return true;
	}

}
