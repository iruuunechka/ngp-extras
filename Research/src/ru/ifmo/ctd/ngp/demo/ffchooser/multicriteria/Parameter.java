package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria;

import java.util.Arrays;

/**
 * An unmodifiable parameter (search point) of multicriteria optimization.
 * 
 * @author Arina Buzdalova
 */
public class Parameter {	
	private final double[] values;
	
	/**
	 * Constructs {@link Parameter} with the specified criteria values.
	 * @param criteria the specified criteria values
	 * @throws IllegalArgumentException if the number of criteria values is zero
	 */
	public Parameter(double... criteria) {
		if (criteria.length == 0) {
			throw new IllegalArgumentException("Zero number of parameter elements.");
		}
		this.values = criteria.clone();
	}
	
	/**
	 * Checks whether this parameter dominates the specified one.
	 * @param p the specified parameter
	 * @return {@code true} if this parameter dominates {@code p}, {@code false} otherwise
	 */
	public boolean dominates(Parameter p) {
		checkArgument(p);
		double[] otherValues = p.getCriteria();
		boolean greater = false;
		for (int i = 0; i < values.length; i++) {
			double v = values[i];
			double ov = otherValues[i];
			if (v < ov) {
				return false;
			}
			if (v > ov) {
				greater = true;
			}			
		}		
		return greater;
	}
			
	/**
	 * Checks whether this parameter is not comparable with the specified one.
	 * @param p the specified parameter
	 * @return {@code true} if this parameter is not comparable with {@code p}, {@code false} otherwise
	 */
	public boolean notComparable(Parameter p) {
		return !equals(p) && !dominates(p) && !p.dominates(this);
	}
	
	/**
	 * Returns a copy of criteria values corresponding to this parameter.
	 * @return a copy of criteria values
	 */
	public double[] getCriteria() {
		return values.clone();
	}

    /**
     * Returns value of specified criterion.
     * @param i the specified criterion
     * @return the value of the specified criterion.
     */
    public double getCriteria(int i) {
        return values[i];
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Arrays.toString(values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(values);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Parameter other = (Parameter) obj;
        return Arrays.equals(values, other.values);
    }

	private void checkArgument(Parameter p) {
		if (p.getCriteria().length != values.length) {
			throw new IllegalArgumentException("Criteria numbers don't agree.");
		}
	}
}
