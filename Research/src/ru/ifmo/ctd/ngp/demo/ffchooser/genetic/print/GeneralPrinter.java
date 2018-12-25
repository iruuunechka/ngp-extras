package ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print;

import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.EvolutionaryAlgImpl;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.Printer;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
/**
 * Printer, which allows to observe {@link EvolutionaryAlgImpl} running.
 * 
 * @author Arina Buzdalova
 * @param <T> the type of an individual
 */
public class GeneralPrinter<T> implements Printer<T> {
	private final Writer writer;

    /**
	 * Constructs {@link GeneralPrinter} with the specified writer
	 * @param writer the specified writer
	 */
	public GeneralPrinter(Writer writer) {
		this.writer = writer;
	}
	
	/**
	 * {@inheritDoc}
	 * Prints information to the file. The format is:
	 * <pre>[generation number] [best fitness 1] [best fitness 2] ... [chosen fitness function]
	 * </pre>
	 */
	@Override
	public void print(List<Double> values, T bestIndividual,
			int iterations, int curEvaluatorIndex) {
		
		try {
            writer.append(String.valueOf(iterations)).append(" ");
			
			for (Double d : values) {
                writer.append(String.valueOf(d)).append(" ");
			}

            writer.append(String.valueOf(curEvaluatorIndex)).append("\n");
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Prints the specified string to the log file used by this printer
	 * @param info the specified string
	 * @throws IOException if an I/O error occurs
	 */
	public void println(String info) throws IOException {
        writer.append(info).append("\n");
	}
	
	/**
	 * Closes the stream related to this printer
	 * @throws IOException if an I/O error occurs
	 */
	public void close() throws IOException {
		writer.close();
	}
}
