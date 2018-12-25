package ru.ifmo.ctd.ngp.demo.ffchooser.gradient;

import java.util.List;
import org.uncommons.maths.statistics.DataSet;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.Printer;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * {@link Printer} that gathers statistics about the number of generation
 * in which the best individual was evolved. 
 * 
 * If the ideal fitness wasn't reached,
 * the maximal number of generations (steps limit) is used.
 * 
 * @author Arina Buzdalova
 */
public class BestStatisticsPrinter implements Printer<BitString> {
	private final double bestFitness;
	private final int evalIndex;
	private final DataSet dataSet;

	/**
	 * Constructs this printer with the specified best possible fitness value
	 * and the index of the corresponding fitness evaluator
     * @param bestFitness the best possible fitness value that corresponds to <code>evalIndex</code>
     * @param evalIndex the index of the fitness evaluator
     */
	public BestStatisticsPrinter(double bestFitness, int evalIndex) {
		this.bestFitness = bestFitness;
		this.evalIndex = evalIndex;
		this.dataSet = new DataSet(100);
	}
	
	/**
	 * Gets the statistics about the number of generation
	 * in which the best individual was evolved
	 * @return the statistics about the number of generation
	 * in which the best individual was evolved
	 */
	public DataSet getStatistics() {
		return dataSet;
	}
	
	/**
	 * Gathers statistics about the number of generation
	 * in which the best individual was evolved
	 */
	@Override
	public void print(List<Double> values, BitString bestIndividual,
			int iterations, int curEvaluatorIndex) {
		if (values.get(evalIndex).equals(bestFitness)) {
			dataSet.addValue(iterations);
			System.out.print(iterations + " ");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void println(String info) {
		System.out.println(info);
	}
}
