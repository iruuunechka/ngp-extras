package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint;

/**
 * Implementation of {@link IntegerFunction} 
 * of the form <code>f(x) = (int) (factor * g(x))</code>, 
 * where <code>g(x)</code> is some real-valued function.
 * 
 * @author Arina Buzdalova
 */
public class IntegerFunctionImpl extends RealFunctionImpl implements IntegerFunction {
	
	private double factor;
	private double lowerDomainBound;
	private double upperDomainBound;
	private final RealFunction g;
	
	/**
	 * Constructs {@link IntegerFunction} with the specified inner {@link RealFunction},
	 * the specified factor and the specified range of definition. Empty domain is not allowed.
	 * @param realFunction the specified real valued function with constant convexity
	 * @param factor the specified factor
	 * @param lowerDomainBound lower edge of the range of definition (inclusive)
	 * @param upperDomainBound upper edge of the range of definition (inclusive)
	 * @throws IllegalArgumentException if <code>lowerDomainBound</code> is greater than <code>upperDomainBound</code>
	 */
	public IntegerFunctionImpl(RealFunction realFunction, double factor, double lowerDomainBound, double upperDomainBound) {
		
		checkBounds(lowerDomainBound, upperDomainBound);
		
		this.g = realFunction;
		this.factor = factor;
		this.lowerDomainBound = lowerDomainBound;
		this.upperDomainBound = upperDomainBound;
	}
	
	/**
	 * Constructs {@link IntegerFunction} with the specified inner {@link RealFunction}
	 * and the specified range of definition. The factor is initially set to <code>1.0</code>.
	 * Empty domain is not allowed.
	 * @param realFunction the specified real valued function with constant convexity
	 * @param lowerDomainBound lower edge of the range of definition (inclusive)
	 * @param upperDomainBound upper edge of the range of definition (inclusive)
	 * @throws IllegalArgumentException if <code>lowerDomainBound</code> is greater than <code>upperDomainBound</code>
	 */
	public IntegerFunctionImpl(RealFunction realFunction, double lowerDomainBound, double upperDomainBound) {
		
		checkBounds(lowerDomainBound, upperDomainBound);
		
		this.g = realFunction;
		this.factor = 1.0;
		this.lowerDomainBound = lowerDomainBound;
		this.upperDomainBound = upperDomainBound;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int changeSwitchPoint(int expectedSwitch, int tolerance, double lowFactor, double highFactor) {
		
		if (!isConvex()) {
			throw new UnsupportedOperationException("Operation is not supported, because the function is not convex.");
		}
		
		return searchFactor(expectedSwitch, tolerance, lowFactor, highFactor);
	}
	
	private int searchFactor(int expectedSwitch, int tolerance, double lowFactor, double highFactor) {
		double factor = lowFactor + (highFactor - lowFactor) / 2;
		setFactor(factor);
		
		int realSwitch = findSwitchPoint();
		
		if (realSwitch == -1) {
			return	changeSwitchPoint(expectedSwitch, tolerance, lowFactor, factor);
		}
		
		if (Math.abs(realSwitch - expectedSwitch) <= tolerance) {
			return realSwitch;
		}
		
		if (realSwitch < expectedSwitch) {
			return	isDownConvex()
					? changeSwitchPoint(expectedSwitch, tolerance, lowFactor, factor)
					: changeSwitchPoint(expectedSwitch, tolerance, factor, highFactor);
		}
		
		if (realSwitch > expectedSwitch) {			
			return	isDownConvex()
					? changeSwitchPoint(expectedSwitch, tolerance, factor, highFactor)
					: changeSwitchPoint(expectedSwitch, tolerance, lowFactor, factor);
		}
		
		return realSwitch;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int findSwitchPoint() {
		
		if (isDownConvex()) {
			for (int x = (int)upperDomainBound; x >= lowerDomainBound; x--) {
				if (deltaRatio(x, 1) == 0) {
					return x;
				}
			}
		} 
		
		if (!isDownConvex()) {
			for (int x = (int)lowerDomainBound; x <= upperDomainBound; x++) {
				if (deltaRatio(x, 1) == 0) {
					return x;
				}
			}
		}
		
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFactor() {
		return factor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getValue(double x) {
		return (int) (factor * g.getValue(x));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFactor(double factor) {
		this.factor = factor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConvex() {
		return g.isConvex();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDownConvex() {
		return g.isDownConvex();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getLowerDomainBound() {
		return lowerDomainBound;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getUpperDomainBound() {
		return upperDomainBound;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDomain(double lowerBound, double upperBound) {
		checkBounds(lowerBound, upperBound);
		this.lowerDomainBound = lowerBound;
		this.upperDomainBound = upperBound;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "(int) " + factor + " * " + g;
	}
	
	private void checkBounds(double lowerBound, double upperBound) {
		if (lowerBound > upperBound) {
			throw new IllegalArgumentException(
					"Lower domain bound = " + lowerBound + 
					" > upper domain bound = " + upperBound);
		}
	}
}
