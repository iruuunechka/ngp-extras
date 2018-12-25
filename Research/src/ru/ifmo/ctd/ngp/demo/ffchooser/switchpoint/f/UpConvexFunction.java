package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f;

import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.RealFunction;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.RealFunctionImpl;

/**
 * Basic implementation for upwards convex <code>{@link RealFunction}s</code>.
 * 
 * @author Arina Buzdalova
 * @see RootLog
 * @see Root
 */
public abstract class UpConvexFunction extends RealFunctionImpl {

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
		return false;
	}
	
}
