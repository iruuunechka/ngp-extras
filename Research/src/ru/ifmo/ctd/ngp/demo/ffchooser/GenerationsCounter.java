package ru.ifmo.ctd.ngp.demo.ffchooser;

import java.util.List;

/**
 * <p>
 * Interface for entity, which can count number of generations
 * needed for an evolutionary-like algorithm to get some ideal fitness value.
 * </p><p>
 * The algorithm is expected to use string-based individuals.
 * </p><p>
 * The counter allows to set such parameters as 
 * the ideal fitness value to be obtained, 
 * the fitness evaluator and the start population.
 * </p>
 * 
 * @author Arina Buzdalova
 * @param <I> the type of an individual
 * @param <E> the type of the fitness evaluator
 */
public interface GenerationsCounter<E, I> {
	
	/**
	 * <p>
	 * Counts number of generations to be evolved in order to reach the current ideal fitness 
	 * using the current fitness evaluator.
	 * </p><p>
	 * Fitness evaluator and ideal fitness are set by {@link #setEvaluator} method.
	 * The length of each individual is set by {@link #setLength} method.
	 * These two methods should be called at least once before trying to count number of generations.
	 * </p>
	 * @return number of generations to be evolved in order to reach the ideal fitness, or <code>-1</code>
	 * if the ideal fitness wasn't reached after maximal number of generations
	 */
	int countGenerations();
	
	/**
	 * Sets the fitness evaluator and the corresponding ideal fitness value
	 * 
	 * @param evaluator the fitness evaluator
	 * @param idealFitness the ideal fitness value corresponding to <code>evaluator</code>
	 */
	void setEvaluator(E evaluator, double idealFitness);
	
	/**
	 * Sets the length of an individual
	 * @param length the length of an individual
	 */
	void setLength(int length);
	
	/**
	 * Sets the population, from which the algorithm starts. 
	 * If it is empty, then random individuals will be used.
	 * 
	 * @param population the start population
	 */
	void setStartPopulation(List<I> population);
	
	/**
	 * Gets the number of evaluators this logger uses to print different values
	 * @return the number of evaluators this logger uses to print different values
	 */
	int getEvaluatorsCount();
	
	/**
	 * Gets the index of the target evaluator
	 * @return the index of the target evaluator
	 */
	int getTargetIndex();
}
