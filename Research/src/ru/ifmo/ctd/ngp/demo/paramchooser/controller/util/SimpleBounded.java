package ru.ifmo.ctd.ngp.demo.paramchooser.controller.util;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Bounded;

/**
 * @author Arkadii Rost
 */
public class SimpleBounded implements Bounded {
    private final double from;
    private final double to;

    public SimpleBounded(double from, double to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public double getLowerBound() {
        return from;
    }

    @Override
    public double getUpperBound() {
        return to;
    }

	@Override
	public String toString() {
		return String.format("(%.3f, %.3f)", from, to);
	}
}
