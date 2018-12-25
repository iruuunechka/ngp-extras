package ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.Printer;

/**
 * Printer that counts how many times different fitness evaluators
 * were applied to the individuals valued by the specified fitness function.
 * 
 * @author Arina Buzdalova
 * @param <T> type of an individual
 */
public class CountPrinter<T> implements Printer<T> {
	
	private final FitnessEvaluator<? super T> evaluator;
	private final int evaluatorsCount;
	private final Map<Double, int[]> map;
	
	/**
	 * Constructs {@link CountPrinter} with the specified evaluator.
	 * This evaluator will be used to differentiate individuals.
	 * 
	 * @param evaluator the evaluator used to differentiate individuals
	 * @param evaluatorsCount the number of evaluators
	 */
	public CountPrinter(FitnessEvaluator<? super T> evaluator, int evaluatorsCount) {
		this.evaluator = evaluator;
		this.evaluatorsCount = evaluatorsCount;
		this.map = new HashMap<>();
	}

	/**
	 * Gets the number of evaluators
	 * @return the number of evaluators
	 */
	public int getEvaluatorsCount() {
		return evaluatorsCount;
	}
	
	/**
	 * <p>
	 * Returns statistics corresponding to the specified value.
	 * </p><p>
	 * The <code>i-th</code> element in the returned array
	 * stores number of times the <code>i-th</code> evaluator
	 * were used to evaluate the individuals with the <code>key</code>
	 * value.
	 * </p><p>
	 * The value of an individual is evaluated by the evaluator
	 * passed to the constructor of this printer.
	 * </p>
	 * @param key the specified value
	 * @return the statistics corresponding to the <code>key</code>
	 */
	public int[] getStatistics(double key) {
		if (map.containsKey(key)) {
			return map.get(key);
		}
		int[] empty = new int[evaluatorsCount];
		Arrays.fill(empty, 0);
		return empty;
	}
	
	/**
	 * Makes this printer forget about the previous experience
	 */
	public void refresh() {
		map.clear();
	}
	
	/**
	 * Adds information about the evaluator choice for the specified value
	 * @param value the specified value
	 * @param curEvaluator the choice
	 */
	public void add(double value, int curEvaluator) {
		checkKey(value);
		map.get(value)[curEvaluator]++;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(List<Double> values, T bestIndividual, int iterations,
			int curEvaluatorIndex) {		
		double key = evaluator.getFitness(bestIndividual, null);
		add(key, curEvaluatorIndex);
	}
	
	private void checkKey(double key) {
		if (!map.containsKey(key)) {
			int[] array = new int[evaluatorsCount];
			Arrays.fill(array, 0);
			map.put(key, array);
		} 
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void println(String info) {
		throw new UnsupportedOperationException();
	}
}
