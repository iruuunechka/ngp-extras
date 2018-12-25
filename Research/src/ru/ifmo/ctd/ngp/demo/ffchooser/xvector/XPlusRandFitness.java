package ru.ifmo.ctd.ngp.demo.ffchooser.xvector;

import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import ru.ifmo.ctd.ngp.demo.ffchooser.utils.GeneticUtils;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * Calculates fitness function of the individuals of the xVector problem
 * according to function a*x + b * (random value).
 *  
 * @author Arina Buzdalova
 */
public class XPlusRandFitness implements FitnessEvaluator<BitString> {

	private final double a;
	private final double b;
	private final Random rng;
	
	/**
     * Constructs {@link XFitness} with the specified parameters {@code a} and {@code b}.
     * Each call of {@link #getFitness(ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString, List)}
     * will return value of {@code a * x + b * (random value)},
     * where random value is in the range of {@code [0, 1)}.
     * @param a the specified parameter
     * @param b the specified parameter
     * @param rng the source of randomness
     */
	public XPlusRandFitness(double a, double b, Random rng) {
		this.a = a;
		this.b = b;
		this.rng = rng;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFitness(BitString candidate, List<? extends BitString> population) {
		return a * GeneticUtils.toInt(candidate) + b * rng.nextDouble();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNatural() {
		return true;
	}
}
