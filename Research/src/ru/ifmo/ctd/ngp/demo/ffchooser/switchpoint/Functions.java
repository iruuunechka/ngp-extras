package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint;

import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f.Const;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f.DownConvexFunction;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f.RootLog;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f.SecondPower;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f.UpConvexFunction;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f.X;

/**
 * Set of methods for creating functions of different types.
 * 
 * @author Arina Buzdalova
 */
public class Functions {
    private Functions() {}

    /**
	 * Returns some upwards convex real function {@link UpConvexFunction}
	 * @return upwards convex real function
	 */
	public static UpConvexFunction upConvex() {
		return new RootLog(6.0);
	}
	
	/**
	 * Returns some downwards convex real function {@link DownConvexFunction}
	 * @return convex real function
	 */
	public static DownConvexFunction downConvex() {
		return new SecondPower();
	}
	
	/**
	 * Returns some upwards convex {@link IntegerFunction}
	 * with the specified domain
	 * @param lowerBound the lower bound of the range of definition
	 * @param upperBound the upper bound of the range of definition
	 * @return upwards convex integer function
	 */
	public static IntegerFunction intUpConvex(double lowerBound, double upperBound) {
		return new IntegerFunctionImpl(upConvex(), lowerBound, upperBound);
	}
	
	/**
	 * Returns some downwards convex {@link IntegerFunction}
	 * with the specified domain
	 * @param lowerBound the lower bound of the range of definition
	 * @param upperBound the upper bound of the range of definition
	 * @return convex integer function
	 */
	public static IntegerFunction intDownConvex(double lowerBound, double upperBound) {
		return new IntegerFunctionImpl(downConvex(), lowerBound, upperBound);
	}
	
	/**
	 * Returns {@link IntegerFunction} <code>(int) (1/divider) * x </code>
	 * with the specified divider and domain
	 * @param divider the specified divider
	 * @param lowerBound the lower bound of the range of definition
	 * @param upperBound the upper bound of the range of definition
	 * @return {@link IntegerFunction} <code>(int) (1/divider) * x </code>
	 * with the specified divider and domain
	 */
	public static IntegerFunction intDivX(double divider, double lowerBound, double upperBound) {
		return new IntegerFunctionImpl(new X(), 1.0 / divider, lowerBound, upperBound);
	}
	
	/**
	 * Returns {@link PiecewiseFunction} that has non-zero differences
	 * before the specified switch point and is constant after it
	 * @param switchPoint the specified switch point
	 * @return 	piecewise function, that is good before <code>switchPoint</code>
	 * 			and constant after it
	 */
	public static PiecewiseFunction xConst(double switchPoint) {
		return new PiecewiseFunction(new X(), switchPoint, new Const(switchPoint));
	}
	
	/**
	 * Returns {@link PiecewiseFunction} that has non-zero differences
	 * after the specified switch point and is constant before it
	 * @param switchPoint the specified switch point
	 * @return 	piecewise function, that is good after <code>switchPoint</code>
	 * 			and constant before it
	 */
	public static PiecewiseFunction constX(double switchPoint) {
		return new PiecewiseFunction(new Const(switchPoint), switchPoint, new X());
	}
}
