package ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print;

import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.EvolutionaryAlgImpl;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.Printer;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
/**
 * File printer, which allows to observe {@link EvolutionaryAlgImpl} running.
 * It "compresses" information and prints it to the specified writer.
 * 
 * @author Arina Buzdalova
 * @param <T> the type of an individual
 */
public class CompressingPrinter<T> implements Printer<T> {
	private final int[] stop;
	private final double[] data;
	private final StringBuilder[] buffers;
	private final Writer writer;

    /**
	 * Constructs {@link CompressingPrinter} that prints information to 
	 * the specified writer
	 *   
	 * @param writer the specified writer
	 * @param evaluators number of optimization parameters (fitness functions) 
	 * used by the algorithm being observed
	 */
	public CompressingPrinter(Writer writer, int evaluators) {	
		this.writer = writer;
		
		int size = evaluators + 1;
		
		this.stop = new int[size];
		this.data = new double[size];
		this.buffers = new StringBuilder[size];
		
		Arrays.fill(stop, -1);
		Arrays.fill(data, -1.0);
		for (int i = 0; i < size; i++) {
			buffers[i] = new StringBuilder();
		}
		
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
		int size = buffers.length;
		
		if (values.size() + 1 != size) {
			throw new IllegalArgumentException("Size of values list must be equal to size of buffers list - 1.");
		}
		
		for (int i = 0; i < size - 1; i++) {
			printToBuffer(iterations, i, values.get(i));
		}
		
		printToBuffer(iterations, size - 1, curEvaluatorIndex);		
	}
	
	private void printToBuffer(int iterations, int id, double value) {
		double old = data[id];
		
		if (value != old) {			
			if (old >= 0) {
				dump(id);
			}					
			data[id] = value;
		}		
		stop[id] = iterations;
	}
	
	private void dump(int id) {
        buffers[id].append(stop[id]).append(" ").append(data[id]).append("\n");
		data[id] = -1.0;
	}
	
	/**
	 * Prints the specified string to all the log files used by this printer
	 * @param info the specified string
	 * @throws IOException if an I/O error occurs 
	 */
	public void println(String info) throws IOException {
        writer.append(info).append("\n");
	}
	
	/**
	 * Dumps all the current information to the writer
	 * @throws IOException if an I/O error occurs 
	 */
	public void dumpAll() throws IOException {
		for (int i = 0; i < buffers.length; i++) {
			dump(i);
		}
		
		for (StringBuilder s : buffers) {
			writer.append(s);
			writer.append("\n");
			s.delete(0, s.length());
		}
		writer.flush();
	}
	
	/**
	 * Closes this writer
	 * @throws IOException if an I/O exception
	 */
	public void close() throws IOException {
		writer.close();
	}
}
