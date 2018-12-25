package ru.ifmo.ctd.ngp.demo.ffchooser.genetic;

import org.uncommons.watchmaker.framework.FitnessEvaluator;
import ru.ifmo.ctd.ngp.demo.ffchooser.OptimizationAlgorithm;

import java.util.List;

/**
 * Interface for evolutionary algorithms, such as
 * genetic algorithms or evolutionary strategies. 
 * 
 * The individuals being evolved are expected to be string-based.
 * 
 * @author Arina Buzdalova
 * 
 * @param <I> the type of an individual
 * 
 * @see OptimizationAlgorithm
 */
public interface EvolutionaryAlgorithm<I> extends OptimizationAlgorithm {
	
	/**
	 * Makes this algorithm forgetting about already evolved individuals.
	 * The algorithm will start from scratch after the next attempt to evolve individuals.
	 */
	void refresh();

	/**
	 * Adds the {@link Printer} used to observe this algorithm running
	 * @param printer the printer used to observe this algorithm running
	 */
	void addPrinter(Printer<? super I> printer);
	
	/**
	 * Stops retrieving information to the specified printer
	 * @param printer the printer to be removed
	 */
	void removePrinter(Printer<? super I> printer);
	
	/**
	 * Sets the population, which will be used to generate individuals 
	 * at the next iteration of the algorithm
     * @param seedPopulation the start population
     */
	void setStartPopulation(List<I> seedPopulation);

    /**
     * Returns the current value of the target criterion of the best individual by extra-objective
     * @return the current value of the target criterion
     */
    double getTargetValueInCurrentBest();

    /**
     * Returns the best value of the target criterion from first to current iteration
     * @return the best value of the target criterion
     */
    double getFinalBestTargetValue();

	/**
	 * Sets the length of an individual and refreshes the algorithm
	 * using the {@link #refresh} method
	 * @param length the length of an individual
	 */
	void setLength(int length);
	
	/**
	 * Sets the specified fitness evaluator to the specified index. 
	 * Values of this evaluator will be returned by the {@link #computeValues()} method
	 * under the corresponding index.
	 * @param index the specified index
	 * @param evaluator the evaluator to be associated with the <code>index</code>
	 */
	void setEvaluator(int index, FitnessEvaluator<? super I> evaluator);
}
