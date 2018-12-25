package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f;

import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.RealFunctionImpl;

/**
 * Constant function.
 * 
 * @author Arina Buzdalova
 */
public class Const extends RealFunctionImpl {
	
	private final double constant;
	
	/**
	 * Constructs the {@link Const} function with
	 * the specified constant
	 * @param constant the specified constant
	 */
	public Const(double constant) {
		this.constant = constant;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getValue(double x) {
		return constant;
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
		return constant + "";
	}

}
