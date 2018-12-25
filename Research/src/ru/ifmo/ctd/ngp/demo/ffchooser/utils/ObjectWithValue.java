package ru.ifmo.ctd.ngp.demo.ffchooser.utils;

import org.jetbrains.annotations.NotNull;

/**
 * Comparable pair of double value and some object.
 * 
 * It is mainly used to sort objects according to 
 * the corresponding values.
 * 
 * @author Arina Buzdalova
 *
 * @param <T> the type of the object
 */
public class ObjectWithValue<T> implements Comparable<ObjectWithValue<T>>{
	private final double value;
	private final T object;		
	
	/**
	 * Creates (value, object) pair with the specified value
	 * and object
	 * @param value the specified value
	 * @param object the specified object
	 */
	public ObjectWithValue(double value, T object) {
		this.value = value;
		this.object = object;
	}	

	/**
	 * Gets value associated with the object in this pair
	 * @return value associated with the object in this pair
	 */
	public double getValue() {
		return value;
	}
	
	/**
	 * Gets object of this pair
	 * @return object of this pair
	 */
	public T getObject() {
		return object;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(@NotNull ObjectWithValue<T> pair) {
		return Double.compare(value, pair.getValue());
	}	
}
