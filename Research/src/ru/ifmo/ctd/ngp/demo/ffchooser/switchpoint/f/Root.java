package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f;

import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.RealFunction;

/**
 * Function <code>x<sup>1/k</sup></code>,
 * where <code>k</code> is specified degree.
 * 
 * @author Arina Buzdalova
 * 
 * @see UpConvexFunction
 * @see RealFunction
 */
public class Root extends UpConvexFunction {
	private final double degree;
	
	/**
	 * Constructs the root function <code>x<sup>1/degree</sup></code>
	 * with the specified degree
	 * @param degree the specified degree of this root
	 */
	public Root(double degree) {
		this.degree = degree;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getValue(double x) {
		return Math.pow(x, 1.0 / degree);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "x ^ (1 / " + degree + ")";
	}
}
