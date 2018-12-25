package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint;

/**
 * <p>
 * Interface for a integer valued function of the form 
 * <code>f(x) = (int) (factor * g(x))</code>,
 * where <code>g(x)</code> is some real-valued function.
 * </p><p>
 * The range of definition is set manually. Empty range of definition is not allowed.
 * </p>
 * @author Arina Buzdalova
 * @see RealFunction
 */
public interface IntegerFunction extends RealFunction {
	
	/**
	 * <p>
	 * Finds the switch point of the specified function.
	 * </p><p>
	 * Switch point is a point, which separates arguments mainly corresponding to the
	 * zero differences of the function from the arguments corresponding to the
	 * non-zero differences.
	 * </p>
	 * @return 	the switch point of this function;
	 * 			or <code>-1</code> if there are no switch point inside the range of definition
	 */
	int findSwitchPoint();
	
	/**
	 * <p>
	 * Changes the switch point of this function to the point near the specified one 
	 * by adjusting the factor of this function. 
	 * </p><p>
	 * The allowable difference between the expected switch point and the real one and
	 * the range of factor search are specified.
	 * </p>
	 * @param expectedSwitch the expected switch point
	 * @param tolerance the allowable difference between the <code>expectedSwitch</code> and the eventually set one
	 * @param lowFactor the lower bound of the factor search range
	 * @param highFactor the upper bound of the factor search range
	 *
	 * @return the switch point eventually set by adjusting the factor
	 * 
	 * @see #findSwitchPoint()
	 * @see #getFactor()
	 */
	int changeSwitchPoint(int expectedSwitch, int tolerance, double lowFactor, double highFactor);
	
	/**
	 * Gets the factor of this function.
	 * This function has form <code>f(x) = (int) (factor * g(x))</code>,
	 * where <code>g(x)</code> is some real-valued function.
	 * 
	 * @return the factor of this function
	 */
	double getFactor();
	
	/**
	 * Sets the factor of this function.
	 * This function has form <code>f(x) = (int) (factor * g(x))</code>,
	 * where <code>g(x)</code> is some real-valued function.
	 * 
	 * @param factor the factor to be set
	 */
	void setFactor(double factor);
	
	/**
	 * Gets the lower edge of the range of definition (inclusive)
	 * @return lowerBound the lower domain bound (inclusive)
	 */
	double getLowerDomainBound();
	
	/**
	 * Gets the upper edge of the range of definition (inclusive)
	 * @return upperBound the upper domain bound (inclusive)
	 */
	double getUpperDomainBound();
	
	/**
	 * Sets the range of definition of this function
	 * @param lowerBound the lower edge of the range of definition (inclusive)
	 * @param upperBound the upper edge of the range of definition (inclusive)
	 * @throws IllegalArgumentException if <code>lowerDomainBound</code> is greater than <code>upperDomainBound</code>
	 */
	void setDomain(double lowerBound, double upperBound);
}
