package ru.ifmo.ctd.ngp.demo.paramchooser.controller.watchmaker;

import org.uncommons.maths.number.NumberGenerator;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Bounded;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Parameter;

/**
 * @author Arkadii Rost
 */
public class ParameterNumberGenerator implements Parameter, Bounded, NumberGenerator<Double> {
    private final String description;
    private final double lowerBound;
    private final double upperBound;
    private double value;

    public ParameterNumberGenerator(String description, double lowerBound, double upperBound,
        double initialValue)
    {
        this.description = description;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.value = initialValue;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public double getLowerBound() {
        return lowerBound;
    }

    @Override
    public double getUpperBound() {
        return upperBound;
    }

    @Override
    public Double nextValue() {
        return getValue();
    }

    @Override
    public String toString() {
        return String.format("%s: %f", description, getValue());
    }
}
