package ru.ifmo.ctd.ngp.demo.ffchooser.genetic;

import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.AbstractEvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;

import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * <p>
 * A class for counting number of generations needed 
 * for genetic algorithm to evolve the ideal individual
 * in the bit count problem.
 * </p><p>
 * It uses normal, non-incremental, genetic algorithm from Watchmaker library.
 * </p>
 * @author Arina Buzdalova
 */
public class GACounter extends NoLearningCounter {
	
	/**
	 * Constructs {@link GACounter} with the specified steps limit, 
	 * size of generation, elite count and evolutionary operators.
	 * If the <code>stepsLimit</code> is reached, counting stops.
	 * @param stepsLimit the maximal number of generations that can be evolved
	 * @param generationSize the size of a generation
	 * @param eliteCount the number of candidates kept by elitism 
	 * @param pipeline the evolutionary operators
	 */
	public GACounter(int stepsLimit, int generationSize, int eliteCount,
			EvolutionaryOperator<BitString> pipeline) {
		super(stepsLimit, generationSize, eliteCount, pipeline);
	}

	@Override
	protected AbstractEvolutionEngine<BitString> getEngine() {
		return new GenerationalEvolutionEngine<>(factory, pipeline, evaluator, new TournamentSelection(new Probability(0.9)), rng);
	}
}
