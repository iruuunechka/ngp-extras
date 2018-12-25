package ru.ifmo.ctd.ngp.demo.ffchooser.genetic;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 * Non-natural {@link FitnessEvaluator} that is used to minimize fitness.
 * The fitness is calculated according to the formula
 * <code>constant - naturalEvaluator</code> value,
 * where <code>constant</code> and <code>naturalEvaluator</code>
 * are specified.
 * 
 * @author Arina Buzdalova
 *
 * @param <T> the type of an individual
 */
@SuppressWarnings("UnusedDeclaration")
public class ReverseFitness<T> implements FitnessEvaluator<T> {
	private final FitnessEvaluator<T> evaluator;
	private final double constant;
	
	/**
	 * Constructs non-natural {@link FitnessEvaluator} that is used to
	 * minimize fitness. The fitness is calculated according to
	 * the formula <code>constant - naturalEvaluator</code>.
	 * @param evaluator the specified natural evaluator that is normally used to maximize fitness
	 * @param constant the specified constant
	 */
	public ReverseFitness(FitnessEvaluator<T> evaluator, double constant) {
		this.evaluator = evaluator;
		this.constant = constant;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFitness(T candidate, List<? extends T> population) {
		return constant - evaluator.getFitness(candidate, population);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNatural() {
		return false;
	}

}
