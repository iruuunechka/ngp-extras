package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint;

/**
 * Basic implementation for all real valued functions.
 * 
 * @author Arina Buzdalova
 */
public abstract class RealFunctionImpl implements RealFunction {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double deltaRatio(double x, double delta) {		
		return (getValue(x + delta) - getValue(x)) / delta;
	}
}
