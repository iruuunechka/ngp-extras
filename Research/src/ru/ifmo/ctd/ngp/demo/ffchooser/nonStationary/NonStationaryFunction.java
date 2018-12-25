package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary;

import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.RealFunction;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.RealFunctionImpl;

/**
 * @author Irene Petrova
 */
public class NonStationaryFunction extends RealFunctionImpl {
    private final int[] switchPoints;
    private final RealFunction[] functions;

    /**
     * Constructs {@link NonStationaryFunction} of the form
     * <code>f(x) = {function1(x), x < switchPoint1;
     * function2(x), switchPoint2 > x >= switchPoint1; ...
     * functionN(x), x >= switchPoint(N-1)}</code>
     *
     * @param functions 	the array of functions
     *
     * @param switchPoints 	the array of switch points (in increasing order)
     */
    public NonStationaryFunction(int[] switchPoints, RealFunction[] functions) {
        if ((switchPoints.length + 1) != functions.length) {
            throw new IllegalArgumentException("The number of functions must be one more than the number of switch points");
        }
        this.functions = functions;
        this.switchPoints = switchPoints;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getValue(double x) {
        if (switchPoints.length == 0) {
            return functions[0].getValue(x);
        }
        for (int i = 0; i < switchPoints.length; ++i) {
            if (x < switchPoints[i]) {
                return functions[i].getValue(x);
            }
        }
        return functions[switchPoints.length].getValue(x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConvex() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDownConvex() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "nonstationary function: functions " + functions.length + "points: " + switchPoints.length;
    }
}