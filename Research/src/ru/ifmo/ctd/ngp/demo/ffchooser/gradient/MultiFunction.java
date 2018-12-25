package ru.ifmo.ctd.ngp.demo.ffchooser.gradient;

/**
 * Interface for real valued multivariable function.
 *  
 * @author Arina Buzdalova
 */
public interface MultiFunction {
	/**
	 * Calculates the value of this function at the specified point.
	 * @param v the specified point
	 * @return the value of this function at the point <code>v</code>
	 */
    double value(Vector v);
	
	/**
	 * Calculates gradient of this function at the specified point.
	 * @param v the specified point
	 * @return gradient of this function at the point <code>v</code>
	 */
    Vector gradient(Vector v);

}
