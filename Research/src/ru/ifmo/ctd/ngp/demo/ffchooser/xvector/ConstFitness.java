package ru.ifmo.ctd.ngp.demo.ffchooser.xvector;

import org.uncommons.watchmaker.framework.FitnessEvaluator;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.GString;

import java.util.List;

/**
 * Calculates fitness function of the individuals of the xVector problem
 * according to some constant value.
 * 
 * @author Arina Buzdalova
 */
public class ConstFitness implements FitnessEvaluator<GString<Boolean>>{
	private final double c;
	
	/**
	 * Constructs {@link ConstFitness} with the specified constant.
	 * This constant will be returned by all the {@link #getFitness(GString, List)}
	 * calls.
	 * @param c the specified constant
	 */
	public ConstFitness(double c) {
		this.c = c;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFitness(GString<Boolean> candidate,
			List<? extends GString<Boolean>> population) {
		return c;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNatural() {
		return true;
	}

}
