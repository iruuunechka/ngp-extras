package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f;

import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.RealFunction;

public class NullFunction implements RealFunction {

	@Override
	public double getValue(double x) {
		throw new UnsupportedOperationException("Getting value of NullFunction");
	}

	@Override
	public double deltaRatio(double x, double delta) {
		throw new UnsupportedOperationException("Calculating dealtaRatio of NullFunction");
	}

	@Override
	public boolean isConvex() {
		throw new UnsupportedOperationException("Is convex query for NullFunction");
	}

	@Override
	public boolean isDownConvex() {
		throw new UnsupportedOperationException("Is downconvex query for NullFunction");
	}

}
