package ru.ifmo.ctd.ngp.demo.ffchooser.genetic;

import java.util.List;

import org.uncommons.watchmaker.framework.AbstractEvolutionEngine;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.EvolutionStrategyEngine;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

/**
 * Extended implementation of {@link EvolutionaryAlgorithm} 
 * that represents incremental genetic algorithm.
 * 
 * @author Arina Buzdalova
 *
 * @param <T> the type of an individual
 */
public abstract class EvolutionStrategy<T> extends EvolutionaryAlgImpl<T> {
	private final int multiplier;
	private final boolean plus;
	
	/**
	 * Constructs {@link EvolutionStrategy} with the specified parameters
	 * @param factory the factory of individuals
	 * @param targetCriterion the target fitness evaluator
	 * @param curEvaluator the current fitness evaluator
	 * @param evaluators the supporting fitness evaluators (target and current included)
	 * @param operators the evolutionary operators 
	 * @param plus 	if true this object implements a "plus" evolution strategy rather than "comma" one. 
	 * 				With plus-selection the parents are eligible for survival. 
	 * 				With comma-selection only the offspring survive.
	 * @param multiplier how many offspring to create for each member of the parent population 
	 */
	protected EvolutionStrategy(CandidateFactory<T> factory, int targetCriterion,
			int curEvaluator, List<? extends FitnessEvaluator<? super T>> evaluators,
			List<EvolutionaryOperator<T>> operators, boolean plus, int multiplier) {
		super(factory, targetCriterion, curEvaluator, evaluators, operators);
		this.plus = plus;
		this.multiplier = multiplier;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EvaluatedCandidate<T> iterate() {
		iterations++;
		
		AbstractEvolutionEngine<T> engine = new EvolutionStrategyEngine<>(
				factory, pipeline, currentEvaluator, plus, multiplier, rng);
        
        engine.addEvolutionObserver(observer);
        
        seedPopulation = 
        	getPopulation(engine.evolvePopulation(generationSize, eliteCount, seedPopulation, new GenerationCount(2)));
        
        bestIndividual = observer.getLastBestCandidate().getCandidate();
        return observer.getLastBestCandidate();
	}
}
