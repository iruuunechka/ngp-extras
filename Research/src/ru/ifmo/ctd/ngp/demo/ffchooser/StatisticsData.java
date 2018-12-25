package ru.ifmo.ctd.ngp.demo.ffchooser;

import java.util.HashMap;
import java.util.Map;

import org.uncommons.maths.statistics.DataSet;

/**
 * Class for calculating statistics for a finite data set.
 *  
 * @author Arina Buzdalova
 */
public class StatisticsData {
	/**
	 * (value; number of occurrences)
	 */
	private final Map<Double, Integer> map;
	private final DataSet dataSet;
	private int size;
	
	/**
	 * Constructs the {@link StatisticsData}
	 */
	public StatisticsData() {
		dataSet = new DataSet();
		map = new HashMap<>();
		size = 0;
	}
	
	/**
	 * Constructs the {@link StatisticsData}
	 * with the specified initial capacity
	 * @param capacity the specified initial capacity
	 */
	public StatisticsData(int capacity) {
		dataSet = new DataSet(capacity);
		map = new HashMap<>(capacity);
		size = 0;
	}
	
	/**
	 * Adds value to the current data set
	 * @param data the value to be added
	 */
	public void addValue(double data) {
		dataSet.addValue(data);
		
		size++;
		
		if (map.containsKey(data)) {
			map.put(data, map.get(data) + 1);
		} else {
			map.put(data, 1);
		}
	}
	
	/**
	 * Gets the expectation value of the data set
	 * @return the expectation value
	 */
	public double getExpectation() {
		double sum = 0;
		for (Map.Entry<Double, Integer> e : map.entrySet()) {
			sum += e.getKey() * (e.getValue() / (double) size);
		}
		return sum;
	}
	
	/**
	 * Gets maximal element in the data set
	 * @return maximal element in the data set
	 */
	public double getMax() {
		return dataSet.getMaximum();
	}
	
	/**
	 * Gets minimal element in the data set
	 * @return minimal element in the data set
	 */
	public double getMin() {
		return dataSet.getMinimum();
	}
	
	/**
	 * If the number of elements is odd, gets the middle element in the sorted array.
	 * If the number of elements is even, gets the midpoint of the two middle elements.
	 * @return the median
	 */
	public double getMedian() {
		return dataSet.getMedian();
	}	
	
	/**
	 * Gets the arithmetic mean of the data 
	 * @return the arithmetic mean
	 */
	public double getAverage() {
		return dataSet.getArithmeticMean();
	}
}
