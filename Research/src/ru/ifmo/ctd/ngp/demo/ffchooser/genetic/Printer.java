package ru.ifmo.ctd.ngp.demo.ffchooser.genetic;

import java.io.IOException;
import java.util.List;

/**
 * Interface for printers used to observe {@link EvolutionaryAlgImpl} running.
 * 
 * @author Arina Buzdalova
 * @param <T> the type of an individual
 */
public interface Printer<T> {

	/**
	 * Prints information about current state of evolution in the {@link EvolutionaryAlgImpl}
	 * 
	 * @param values 	the values to be printed,
	 * 					typically list of best individual's fitness calculated by 
	 * 					different evaluators
	 * @param bestIndividual the best individual in the last evolved population
	 * @param iterations of the last iteration of genetic algorithm
	 * @param curEvaluatorIndex index of the current fitness function
	 */
    void print(
            List<Double> values,
            T bestIndividual,
            int iterations,
            int curEvaluatorIndex);
	
	/**
	 * Prints the specified information and a newline
	 * @param info the specified information
	 * @throws IOException if an I/O exception occurs
	 */
    void println(String info) throws IOException;
}
