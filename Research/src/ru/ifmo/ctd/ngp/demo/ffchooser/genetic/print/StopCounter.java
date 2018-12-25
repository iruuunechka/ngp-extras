package ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print;

import java.util.Arrays;

/**
 * {@link StopHandler} that counts how many times the best FF-value
 * was reached on the certain interval of generation numbers
 * 
 * @author Arina Buzdalova
 */
public class StopCounter implements StopHandler {
	private final double idealValue;
	private final double[] data;
	private final int interval;
	
	/**
	 * Constructs {@link StopCounter} with the specified parameters
	 * @param idealValue the best FF-value
	 * @param stepsLimit the number of individuals in a generation
	 * @param interval the interval of generation numbers
	 */
	public StopCounter(double idealValue, int stepsLimit, int interval) {
		this.idealValue = idealValue;
		this.data = new double[stepsLimit / interval + 1];
		Arrays.fill(data, 0);
		this.interval = interval;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handle(int generation, double value) {
		if (value == idealValue) {
			data[generation / interval]++;
		}
	}
	
	/**
	 * Gets array that stores how many times the best FF-value
	 * was reached on the certain interval of generation numbers
	 * @return array that stores how many times the best FF-value
	 * was reached on the certain interval of generation numbers
	 */
	public double[] getData() {
		return data.clone();
	}
}
