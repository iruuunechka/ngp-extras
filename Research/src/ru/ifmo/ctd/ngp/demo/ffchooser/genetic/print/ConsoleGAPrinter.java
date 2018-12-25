package ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print;

import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.EvolutionaryAlgImpl;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.Printer;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.GString;

/**
 * Console printer, which allows to observe {@link EvolutionaryAlgImpl} running.
 * 
 * @author Arina Buzdalova
 */
public class ConsoleGAPrinter implements Printer<GString<Boolean>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(List<Double> values, GString<Boolean> bestIndividual,
			int iterations, int curEvaluatorIndex) {
		String num = iterations < 10 ? "0" + iterations : "" + iterations;
		System.out.print(num + " ");
        System.out.print(bestIndividual.toString() + " ");
		for (Double v : values) {
			System.out.print(String.format("%.2f ", v));
		}
		System.out.println(" main criteria index: " + curEvaluatorIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void println(String info) {
		System.out.println(info);
	}

}
