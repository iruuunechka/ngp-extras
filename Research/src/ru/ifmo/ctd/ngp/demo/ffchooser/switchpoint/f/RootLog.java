package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f;

import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.RealFunction;

/**
 * Function <code>ln(x + 1)^(1 / degree)</code>,
 * where <code>degree</code> should be specified.
 * 
 * @author Arina Buzdalova
 * @see UpConvexFunction
 * @see RealFunction
 */
public class RootLog extends UpConvexFunction {
	private final double degree;
	
	/**
	 * Constructs the root from logarithm function
	 * @param degree the root degree
	 */
	public RootLog(double degree) {
		this.degree = degree;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getValue(double x) {
		return Math.pow(Math.log(x + 1.0), 1.0 / degree);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return degree == 1.0 ? "ln(x + 1)" : "(ln(x + 1))^(1 / " + degree + ")";
	}

}
