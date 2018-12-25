package ru.ifmo.ctd.ngp.demo.ffchooser.xvector;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import ru.ifmo.ctd.ngp.demo.ffchooser.utils.GeneticUtils;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * Calculates fitness function of the individuals of the xVector problem
 * according to function a * |sin(b*x)| + c.
 *  
 * @author Arina Buzdalova
 */
public class SinFitness implements FitnessEvaluator<BitString> {

	private final double a;
	private final double b;
	private final double c;
	
	/**
     * Constructs {@link XFitness} with the specified parameters {@code a, b, c}.
     * Each call of {@link #getFitness(ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString, List)}
     * will return value of {@code a * |sin(b * x)| + c}.
     * @param a the specified parameter
     * @param b the specified parameter
     * @param c the specified parameter
     */
	public SinFitness(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFitness(BitString candidate, List<? extends BitString> population) {
		return a * Math.abs(Math.sin(b * GeneticUtils.toInt(candidate))) + c;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNatural() {
		return true;
	}

}
