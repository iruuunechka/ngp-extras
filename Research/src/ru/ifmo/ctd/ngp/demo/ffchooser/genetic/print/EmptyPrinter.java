package ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print;

import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.Printer;

/**
 * Printer that does nothing.
 * 
 * @author Arina Buzdalova
 * @param <T> the type of an individual
 */
@SuppressWarnings("UnusedDeclaration")
public class EmptyPrinter<T> implements Printer<T> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(List<Double> values, T bestIndividual,
			int iterations, int curEvaluatorIndex) {}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void println(String info) {}

}
