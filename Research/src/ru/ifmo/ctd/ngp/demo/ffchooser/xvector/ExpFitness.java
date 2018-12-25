package ru.ifmo.ctd.ngp.demo.ffchooser.xvector;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import ru.ifmo.ctd.ngp.demo.ffchooser.utils.GeneticUtils;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * Calculates fitness function of the individuals of the xVector problem
 * according to function k * a ^ (b * x) + c, where x is the number of true-bits.
 *  
 * @author Arina Buzdalova
 */
public class ExpFitness implements FitnessEvaluator<BitString> {

	private final double k;
	private final double a;
	private final double b;
	private final double c;
	
	/** 
	 * Constructs {@link XFitness} with the specified parameters <code>a, b, c</code>.
	 * Each call of {@link #getFitness(ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString, List)}
     * will return value of <code>k * a ^ (b * x) + c</code>,
	 * where x is the number of true-bits.
	 * @param k the specified parameter
	 * @param a the specified parameter
	 * @param b the specified parameter
	 * @param c the specified parameter
	 */
	public ExpFitness(double k, double a, double b, double c) {
		this.k = k;
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFitness(BitString candidate, List<? extends BitString> population) {
		return k * Math.pow(a, b * GeneticUtils.countBits(candidate)) + c;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNatural() {
		return true;
	}

}
