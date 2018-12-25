package ru.ifmo.ctd.ngp.demo.ffchooser.xvector;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import ru.ifmo.ctd.ngp.demo.ffchooser.utils.GeneticUtils;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * Calculates fitness function of the individuals of the xVector problem
 * according to function <code>a*x + b</code>, where x is the number of true-bits.
 *  
 * @author Arina Buzdalova
 */
public class BitCountFitness implements FitnessEvaluator<BitString> {

	private final double a;
	private final double b;
	
	/** 
	 * Constructs {@link XFitness} with the specified parameters <code>a</code> and <code>b</code>.
	 * Each call of {@link #getFitness(ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString, List)}
     * will return value of <code>a * x + b</code>.
	 * @param a the specified parameter
	 * @param b the specified parameter
	 */
	public BitCountFitness(double a, double b) {
		this.a = a;
		this.b = b;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFitness(BitString candidate, List<? extends BitString> population) {
		return a * GeneticUtils.countBits(candidate) + b;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNatural() {
		return true;
	}
}
