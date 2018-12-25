package ru.ifmo.ctd.ngp.demo.ffchooser;

import java.util.List;

/**
 * Interface for an iterative optimization algorithm, 
 * which can be adjusted by changing its optimization criteria.
 * 
 * It also provides with current values of its parameters and
 * the index of the target parameter.
 * 
 * Parameters are encoded by their indexes, which are positive integers.
 * 
 * @author Arina Buzdalova
 */
public interface OptimizationAlgorithm {
	
	/**
	 * Changes current optimization criterion to the parameter
	 * associated with the specified index
	 * 
	 * @param index the index of the new optimization criterion
	 */
    void changeCriterion(int index);
	
	/**
	 * Iterates optimization and returns list of current parameters' values
	 * @return list of current parameters' values
	 */
    List<Double> computeValues();
	
	/**
	 * Gets the index of the parameter, whose value is the most important
	 * @return the index of the target parameter
	 */
    int getTargetParameter();
	
	/**
	 * Gets the index of the current optimization criterion
	 * @return the index of the current optimization criteria
	 */
    int getCurrentCriterion();
	
	/**
	 * Return number of parameters
	 * @return number of parameters
	 */
    int parametersCount();

    /**
     * Returns the best value of the target criterion in population
     * @return the best value of the target criterion
     */
    double getBestTargetValue();

    /**
	 * Returns the number of iterations performed by this algorithm
	 * @return the number of iterations performed by this algorithm
	 */

    int getIterationsNumber();
	
	/**
	 * Gets all the parameters values of the best (in terms of the current criterion) point
	 * @return all the parameters values of the best point
	 */
    List<Double> getCurrentBest();
	
	/**
	 * Gets all the parameters values of each point
	 * @return all the parameters values of each point
	 */
    List<List<Double>> getCurrentPoints();
}
