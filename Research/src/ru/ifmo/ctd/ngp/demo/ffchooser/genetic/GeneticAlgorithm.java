package ru.ifmo.ctd.ngp.demo.ffchooser.genetic;

import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import java.util.List;

/**
 * Extended implementation of {@link EvolutionaryAlgorithm} 
 * that represents incremental genetic algorithm.
 * 
 * @author Arina Buzdalova
 *
 * @param <T> the type of an individual
 */
public abstract class GeneticAlgorithm<T> extends EvolutionaryAlgImpl<T> {

    protected boolean singleThreaded = false;

    public void setSingleThreaded(boolean singleThreaded) {
        this.singleThreaded = singleThreaded;
    }

	/**
	 * Constructs {@link GeneticAlgorithm} with the specified parameters
	 * @param factory the factory of individuals
	 * @param targetCriterion the target fitness evaluator
	 * @param curEvaluator the current fitness evaluator
	 * @param evaluators the supporting fitness evaluators (target and current included)
	 * @param operators the genetic operators (such as crossover and mutation)
	 */
	protected GeneticAlgorithm(CandidateFactory<T> factory, int targetCriterion,
			int curEvaluator, List<? extends FitnessEvaluator<? super T>> evaluators,
			List<? extends EvolutionaryOperator<T>> operators) {
		super(factory, targetCriterion, curEvaluator, evaluators, operators);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EvaluatedCandidate<T> iterate() {
		iterations++;
		
		GenerationalEvolutionEngine<T> engine = 
			new GenerationalEvolutionEngine<>(
				factory,
        		pipeline,
        		currentEvaluator,
        		selectionStrategy,
        		rng);	

        engine.addEvolutionObserver(observer);
        engine.setSingleThreaded(singleThreaded);

        seedPopulation = 
        	getPopulation(engine.evolvePopulation(generationSize, eliteCount, seedPopulation, new GenerationCount(2)));
        
        bestIndividual = observer.getLastBestCandidate().getCandidate();
        return observer.getLastBestCandidate();
	}
}
