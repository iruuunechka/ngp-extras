package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint;

/**
 * Function that is differently defined on two parts of
 * the real axis. The parts are separated by some specified point.
 * 
 * @author Arina Buzdalova
 */
public class PiecewiseFunction extends RealFunctionImpl {
	private final double switchPoint;
	private final RealFunction leftFunction;
	private final RealFunction rightFunction;
	
	/**
	 * Constructs {@link PiecewiseFunction} of the form
	 * <code>f(x) = {leftFunction(x), x < switchPoint; 
	 * rightFunction(x), x >= switchPoint}</code> 
	 * 
	 * @param leftFunction 	the function for the arguments 
	 * 						which are less than <code>switchPoint</code>
	 * @param switchPoint 	the switch point
	 * @param rightFunction	the function for the arguments 
	 * 						which are equal to or greater than <code>switchPoint</code>
	 */
	public PiecewiseFunction(
			RealFunction leftFunction, 
			double switchPoint, 
			RealFunction rightFunction) {
		this.leftFunction = leftFunction;
		this.switchPoint = switchPoint;
		this.rightFunction = rightFunction;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getValue(double x) {
		return x < switchPoint ? leftFunction.getValue(x) : rightFunction.getValue(x);
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
		return "{" + leftFunction + ", x < " + switchPoint + "; " + 
				rightFunction +", x >= " + switchPoint + "}";
	}

}
