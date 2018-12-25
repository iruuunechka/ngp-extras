package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f;

import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.RealFunctionImpl;

/**
 * Function <code>x</code>.
 * 
 * @author Arina Buzdalova
 */
public class X extends RealFunctionImpl {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getValue(double x) {
		return x;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConvex() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDownConvex() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "x";
	}
}
