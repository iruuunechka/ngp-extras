package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary;

import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.RealFunctionImpl;

/**
 * Function -x
 * @author Irene Petrova
 */
public class MinusX extends RealFunctionImpl{
    private final int add;
    private final int nextPoint; //на случай если уходит в отрицательность, для кусочного сдвига

    public MinusX(int add, int nextPoint) {
        this.add = add;
        this.nextPoint = nextPoint;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public double getValue(double x) {
        double res = -x + add;
        if (res >= 0)
            return res;
        return res + nextPoint - add;
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
            return "-x";
        }
}
