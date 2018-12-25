package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f;

import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.RealFunction;

/**
 * Function <code>a + b * (x + c)<sup>2</sup></code>.
 * 
 * @author Arina Buzdalova
 * 
 * @see DownConvexFunction
 * @see RealFunction
 */
public class SecondPower extends DownConvexFunction {
	private final double a;
	private final double b;
	private final double c;
	
	/**
	 * Constructs <code>x<sup>2</sup></code> function with no parameters,
	 */
	public SecondPower() {
		this.a = 0;
		this.b = 1;
		this.c = 0;
	}
	
	/**
	 * Constructs <code>a + b * (x + c)<sup>2</sup></code> function 
	 * with the specified parameters
	 * @param a the specified parameter
	 * @param b the specified parameter
	 * @param c the specified parameter
	 */
	public SecondPower(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getValue(double x) {
		return a + b * (x + c)*(x + c);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return a + " + " + b + " * (x + " + c + ")^ 2";
	}
}
