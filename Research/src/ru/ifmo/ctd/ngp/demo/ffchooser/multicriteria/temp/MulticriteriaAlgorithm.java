package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp;

import org.uncommons.watchmaker.framework.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.*;

import java.util.*;

/**
 * Interface for multicriteria evolutionary algorithms
 *
 * The individuals being evolved are expected to be string-based.
 *
 * @author Irene Petrova
 *
 * @param <I> the type of an individual
 *
 * @see MulticriteriaOptimizationAlgorithm
 */

public interface MulticriteriaAlgorithm<I> extends MulticriteriaOptimizationAlgorithm{
    /**
     * Makes this algorithm forgetting about already evolved individuals.
     * The algorithm will start from scratch after the next attempt to evolve individuals.
     */
    void refresh();

    /**
     * Adds the {@link ru.ifmo.ctd.ngp.demo.ffchooser.genetic.Printer} used to observe this algorithm running
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

    /**
     * Gets the name of algorithm
     * @return the name of algorithm
     */
    String getName();
}
