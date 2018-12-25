package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint;

import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f.DownConvexFunction;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f.UpConvexFunction;

/**
 * Interface for a real valued function.
 *  
 * @author Arina Buzdalova
 * @see DownConvexFunction
 * @see UpConvexFunction
 * @see IntegerFunction
 */
public interface RealFunction {

	/**
	 * Gets the value of this function at the specified point
	 * @param x the specified point
	 * @return the value of this function at the point <code>x</code>
	 */
    double getValue(double x);
	
	/**
	 * Calculates the difference ratio of this function's values and its arguments
	 * <code>(f(x + delta) - f(x)) / delta</code> with the 
	 * specified <code>delta</code>
	 *  
	 * @param x the argument
	 * @param delta the specified delta
	 * @return difference ratio of this function's values and its arguments
	 */
    double deltaRatio(double x, double delta);
	
	/**
	 * Returns <code>true</code> if this function is constantly downwards or upwards convex
	 * on its range of definition,
	 * otherwise returns <code>false</code>
	 * 
	 * @return 	<code>true</code> if this function is constantly downwards or upwards convex,
	 * 			otherwise <code>false</code>
	 */
    boolean isConvex();
	
	/**
	 * Returns <code>true</code> if this function is downwards convex, such as exponent;
	 * <code>false</code> if this function is upwards convex, such as square root
	 * 
	 * @return <code>true</code> if this function is convex; 
	 * <code>false</code> if this function is upwards convex
	 */
    boolean isDownConvex();
	
}
