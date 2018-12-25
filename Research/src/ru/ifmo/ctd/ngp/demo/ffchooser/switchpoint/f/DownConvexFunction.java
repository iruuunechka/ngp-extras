package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f;

import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.RealFunction;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.RealFunctionImpl;

/**
 * Basic implementation for downwards convex <code>{@link RealFunction}s</code>.
 * 
 * @author Arina Buzdalova
 * @see SecondPower
 */
public abstract class DownConvexFunction extends RealFunctionImpl {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConvex() {
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDownConvex() {
		return true;
	}
}
