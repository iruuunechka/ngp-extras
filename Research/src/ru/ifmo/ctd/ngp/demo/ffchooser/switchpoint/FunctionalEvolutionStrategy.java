package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint;

import java.util.List;

import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.EvolutionStrategyEngine;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * Evolution strategy that can be used with {@link FunctionalFitness}.
 * @author Arina Buzdalova
 */
public class FunctionalEvolutionStrategy extends FunctionalGeneticAlgorithm {
	private final boolean plus;
	private final int multiplier;
	
	/**
	 * Creates {@link FunctionalEvolutionStrategy} with the specified parameters
	 * 
	 * @param length the length of an individual
	 * @param targetCriterion the index of target optimization criteria in the evaluators list
	 * @param curEvaluator the index of current optimization criteria in the evaluators list
	 * @param evaluators the list of possible fitness evaluators
	 * @param mutation the probability of mutation
	 * @param crossover the probability of crossover
	 * @param plus the type of evolution strategy
	 * @param multiplier the multiplier used in this evolution strategy
	 */
	public FunctionalEvolutionStrategy(int length, int targetCriterion,
			int curEvaluator, List<FunctionalFitness> evaluators,
			double mutation, double crossover, boolean plus, int multiplier) {
		super(length, targetCriterion, curEvaluator, evaluators, FunctionalEvolutionStrategy.getDefaultOperators(crossover, mutation));
		this.plus = plus;
		this.multiplier = multiplier;
	}
	
	/**
	 * Creates {@link FunctionalEvolutionStrategy} with the specified parameters
	 *
     * @param length the length of an individual
     * @param targetCriterion the index of target optimization criteria in the evaluators list
     * @param curEvaluator the index of current optimization criteria in the evaluators list
     * @param evaluators the list of possible fitness evaluators
     * @param plus the type of evolution strategy
     * @param multiplier the multiplier used in this evolution strategy
     */
	public FunctionalEvolutionStrategy(int length, int targetCriterion,
                                       int curEvaluator, List<FunctionalFitness> evaluators,
                                       boolean plus, int multiplier) {
		super(length, targetCriterion, curEvaluator, evaluators, FunctionalEvolutionStrategy.getDefaultMutation());
		this.plus = plus;
		this.multiplier = multiplier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EvaluatedCandidate<BitString> iterate() {
		iterations++;
		
		EvolutionStrategyEngine<BitString> engine = new EvolutionStrategyEngine<>(
				factory, pipeline, currentEvaluator, plus, multiplier, rng);
        
        engine.addEvolutionObserver(observer);
        
        seedPopulation = 
        	getPopulation(engine.evolvePopulation(generationSize, eliteCount, seedPopulation, new GenerationCount(2)));
        
        bestIndividual = observer.getLastBestCandidate().getCandidate();
        return observer.getLastBestCandidate();
	}
}
