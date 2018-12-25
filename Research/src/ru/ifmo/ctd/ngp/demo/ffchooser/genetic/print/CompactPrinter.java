package ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.EvolutionaryAlgImpl;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.Printer;

/**
 * Console printer, which allows to observe {@link EvolutionaryAlgImpl} running.
 * Does not print best individual.
 * 
 * @author Arina Buzdalova
 * @param <T> the type of an individual
 */
public class CompactPrinter<T> implements Printer<T> {
	
	private final Writer w;
	
	public CompactPrinter() {
		w = new PrintWriter(System.out);
	}

	public CompactPrinter(Writer w) {
		this.w = w;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(List<Double> values, T bestIndividual,
			int iterations, int curEvaluatorIndex) {
		String num = iterations < 10 ? "0" + iterations : "" + iterations;
		try {
			w.append("\n").append(num).append(")\t");
		
			for (Double v : values) {
				w.append(String.format("%.2f\t", v));
			}
			w.append("\tmain criteria: ").append(String.valueOf(curEvaluatorIndex)).append("\t");
			w.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void println(String info) {
		try {
			w.append(info).append("\n");
			w.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
