package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 * Some utilities for multicriteria optimization with evolutionary algorithms.
 * 
 * @author Arina Buzdalova
 */
public class Utils {
	
	/**
	 * Checks whether the specified parameter is not dominated by any other parameter
	 * from the specified collection.
	 * @param parameter the specified parameter
	 * @param collection the specified collection
	 * @return {@code true} if {@code parameter} is not dominated by any parameter from {@code collection},
	 * {@code false} otherwise
	 */
	public static<T> boolean isNotDominated(Parameter parameter, Collection<EvaluatedIndividual<T>> collection) {
		for (EvaluatedIndividual<T> ind : collection) {
			if (ind.par().dominates(parameter)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Gets all non-dominated {@link EvaluatedIndividual}s from the specified collection
	 * @param collection the specified collection
	 * @return all non-dominated {@link EvaluatedIndividual}s from the {@code collection}
	 */
	public static<T> Collection<EvaluatedIndividual<T>> getNotDominated(Collection<EvaluatedIndividual<T>> collection) {
		Collection<EvaluatedIndividual<T>> rv = new ArrayList<>();
		for (EvaluatedIndividual<T> candidate : collection) {
			if (isNotDominated(candidate.par(), collection)) {
				rv.add(candidate);
			}
		}
		return rv;
	}
	
	/**
	 * Evaluates the search point corresponding to the specified individual.
	 * @param individual the specified individual
	 * @param population the population that contains {@code individual}
	 * @param criteria the criteria used to evaluate {@code individual}
	 * @return the result of evaluation {@code individual} with {@code criteria}
	 */
	public static<T> Parameter evaluate(T individual, List<T> population, List<FitnessEvaluator<? super T>> criteria) {
		double[] values = new double[criteria.size()];
		for (int i = 0; i < criteria.size(); i++) {
			values[i] = criteria.get(i).getFitness(individual, population);
		}
		return new Parameter(values);
	}
	
	/**
	 * Evaluates all the individuals in the specified population using the specified criteria.
	 * @param population the specified population
	 * @param criteria the specified criteria used to evaluate individuals in the {@code population}
	 * @return evaluated individuals
	 */
	public static<T> Collection<EvaluatedIndividual<T>> evaluateAll(List<T> population, List<FitnessEvaluator<? super T>> criteria) {
		Collection<EvaluatedIndividual<T>> rv = new ArrayList<>();
		for (T ind : population) {
			rv.add(new EvaluatedIndividual<>(ind, evaluate(ind, population, criteria)));
		}		
		return rv;
	}
	
	/**
	 * Reduces the specified population if its size is greater than the limit.
	 * Random individuals with the maximal squeeze factor are removed.
	 * @param population the specified population
	 * @param maxSize the size limit
	 * @param steps the hyper-grid steps, the array length must be equal to the number of criteria
	 * @param rng the source of randomness
	 * @return the normalized population of {@code maxSize} individuals
	 */
	public static<T> Set<EvaluatedIndividual<T>> normalize(Set<EvaluatedIndividual<T>> population, int maxSize, double[] steps, Random rng) {
		List<HyperBox<T>> hyperGrid = getHyperGrid(population, steps);		
		Collections.sort(hyperGrid);
		HyperBox<T> maxBox = hyperGrid.get(hyperGrid.size() - 1);		
		while (population.size() > maxSize) {
			if (maxBox.isEmpty()) {
				hyperGrid.remove(hyperGrid.size() - 1);
				maxBox = hyperGrid.get(hyperGrid.size() - 1);
			}
			population.remove(maxBox.extractRandom(rng));
		}		
		return population;
	}
	
	/**
	 * Splits the specified population into a hyper-grid with the specified grid step.
	 * 
	 * @param population the population to be split into a hyper-grid
	 * @param steps the hyper-grid steps, the array length must be equal to the number of criteria
	 * @return {@code population} split into the hyper-boxes
	 */
	public static<T> List<HyperBox<T>> getHyperGrid(Collection<EvaluatedIndividual<T>> population, double[] steps) {
		checkGridSteps(population, steps);		
		Map<Integer, HyperBox<T>> map = new HashMap<>();
		
		for (EvaluatedIndividual<T> ind : population) {
			int key = generateKey(ind, steps);
			if (!map.containsKey(key)) {
				map.put(key, new HyperBox<>());
			}
			map.get(key).add(ind);
		}	
		return new ArrayList<>(map.values());
	}
	
	private static<T> void checkGridSteps(Collection<EvaluatedIndividual<T>> population, double[] steps) {
		if (population.iterator().next().par().getCriteria().length != steps.length) {
			throw new IllegalArgumentException("The number of grid steps and the number of criteria do not agree.");
		}
	}
	
	private static<T> int generateKey(EvaluatedIndividual<T> individual, double[] steps) {
		int rv = 0;
		for (int i = 0; i < steps.length; i++) {
			rv += (Math.pow(10, i) * (int) (individual.par().getCriteria()[i] / steps[i]));
		}
		return rv;
	}
}
