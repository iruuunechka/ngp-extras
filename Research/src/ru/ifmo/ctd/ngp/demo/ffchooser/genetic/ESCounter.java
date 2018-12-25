package ru.ifmo.ctd.ngp.demo.ffchooser.genetic;

import org.uncommons.watchmaker.framework.AbstractEvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionStrategyEngine;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * <p>
 * A class for counting number of generations needed 
 * for evolution strategy to evolve the ideal individual
 * in the bit count problem.
 * </p><p>
 * It uses normal, non-incremental, genetic algorithm from Watchmaker library.
 * </p>
 * @author Arina Buzdalova
 */
public class ESCounter extends NoLearningCounter {
	private final boolean plus;
	private final int multiplier;
	
	/**
	 * Constructs {@link ESCounter} with the specified steps limit, 
	 * size of generation, elite count and evolutionary operators.
	 * If the <code>stepsLimit</code> is reached, counting stops.
	 * @param stepsLimit the maximal number of generations that can be evolved
	 * @param generationSize the size of a generation
	 * @param eliteCount the number of candidates kept by elitism 
	 * @param pipeline the evolutionary operators
     * @param plus evolution strategy parameter
	 * @param multiplier evolution strategy parameter
	 */
	public ESCounter(int stepsLimit, int generationSize, int eliteCount,
			EvolutionaryOperator<BitString> pipeline, boolean plus, int multiplier) {
		super(stepsLimit, generationSize, eliteCount, pipeline);
		this.plus = plus;
		this.multiplier = multiplier;
	}

	@Override
	protected AbstractEvolutionEngine<BitString> getEngine() {
		return new EvolutionStrategyEngine<>(factory, pipeline, evaluator, plus, multiplier, rng);
	}
	
}
