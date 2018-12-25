package ru.ifmo.ctd.ngp.demo.ffchooser.gradient;

import java.util.Arrays;

/**
 * A vector of real numbers.
 * 
 * @author Arina Buzdalova
 */
class Vector {
	private final double[] data;
	
	/**
	 * Constructs this vector with the specified elements
	 * @param data the specified elements of the vector to be constructed
	 */
	Vector(double[] data) {
		this.data = data;
	}
	
	/**
	 * Gets the number of elements in this vector
	 * @return the number of elements in this vector
	 */
	int length() {
		return data.length;
	}
	
	/**
	 * Calculates the norm of this vector like <code> max|x<sub>i</sub>|, i=0:length</code>
	 * @return the norm of this vector
	 */
	double maxNorm() {
		double max = 0;
		for (double d : data) {
			if (Math.abs(d) > max) {
				max = Math.abs(d);
			}
		}
		return max;
	}

	/**
	 * Returns difference between this vector and the specified vector.
	 * The vectors should be of equal length.
	 * @param v the specified vector
	 * @return this vector <code> - v</code>
	 */
	Vector minus(Vector v) {
		int len = length();
		if (len != v.length()) {
			throw new IllegalArgumentException("The vectors should be of equal length.");
		}
		double[] sum = new double[len];
		for (int i = 0; i < len; i++) {
			sum[i] = get(i) - v.get(i);
		}
		return new Vector(sum);
	}

	/**
	 * Multiplies each element of this vector with the specified real value
	 * @param value the specified value
	 * @return (this vector) * <code> value</code>
	 */
	Vector mult(double value) {
		int len = length();
		double[] rv = new double[len];
		for (int i = 0; i < len; i++) {
			rv[i] = value * data[i];
		}
		return new Vector(rv);
	}

    /**
	 * Adds the specified real value to the specified element of this vector
	 * @param value the specified value
	 * @param index the index of the element to be increased by the <code>value</code>
	 * @return (this vector) + <code> value</code>
	 */
	Vector iPlus(double value, int index) {
		double[] rv = Arrays.copyOf(data, length());
		rv[index] += value;
		return new Vector(rv);
	}
	
	/**
	 * Gets the element of this vector at the specified index
	 * @param index the specified index
	 * @return the element at the <code>index</code>
	 */
	double get(int index) {
		if (index >= length()) {
			throw new IllegalArgumentException("Index must be less than the vector's length.");
		}
		return data[index];
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (double i : data) {
			builder.append(String.format("%.16f ", i));
		}
		return builder.toString();
	}
}
