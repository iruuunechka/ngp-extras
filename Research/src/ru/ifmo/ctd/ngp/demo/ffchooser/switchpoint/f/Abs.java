package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f;

import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.RealFunction;

/**
 * Function <code>a + b *|x + c|</code>.
 * 
 * @author Arina Buzdalova
 * 
 * @see DownConvexFunction
 * @see RealFunction
 */
public class Abs extends DownConvexFunction {
	private final double a;
	private final double b;
	private final double c;	
	
	/**
	 * Constructs <code>a + b * |x + c|</code> function 
	 * with the specified parameters
	 * @param a the specified parameter
	 * @param b the specified parameter
	 * @param c the specified parameter
	 */
	public Abs(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getValue(double x) {
		return a + b * Math.abs(x + c);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return a + " + " + b + " * |x + " + c + "|";
	}
}
