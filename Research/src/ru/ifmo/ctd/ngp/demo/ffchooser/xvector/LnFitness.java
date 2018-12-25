package ru.ifmo.ctd.ngp.demo.ffchooser.xvector;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import ru.ifmo.ctd.ngp.demo.ffchooser.utils.GeneticUtils;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * Calculates fitness function of the individuals of the xVector problem
 * according to function a * ln(b*x) + c.
 *  
 * @author Arina Buzdalova
 */
@SuppressWarnings("UnusedDeclaration")
public class LnFitness implements FitnessEvaluator<BitString> {

	private final double a;
	private final double b;
	private final double c;
	
	/** 
	 * Constructs {@link XFitness} with the specified parameters <code>a, b, c</code>.
	 * Each call of {@link #getFitness(ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString, List)}
     * will return value of <code>a * ln(b * x) + c</code>.
	 * @param a the specified parameter
	 * @param b the specified parameter
	 * @param c the specified parameter
	 */
	public LnFitness(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFitness(BitString candidate, List<? extends BitString> population) {
		return a * Math.log(b * GeneticUtils.toInt(candidate)) + c;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNatural() {
		return true;
	}
}
