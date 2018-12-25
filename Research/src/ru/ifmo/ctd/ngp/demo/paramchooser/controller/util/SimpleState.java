package ru.ifmo.ctd.ngp.demo.paramchooser.controller.util;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.State;

/**
 * @author Arkadii Rost
 */
public class SimpleState implements State {
    private final double[] observables;
    private final double[] parameters;

    public SimpleState(double[] observables, double[] parameters) {
        this.observables = observables;
        this.parameters = parameters;
    }

    @Override
    public double[] getObservables() {
        return observables;
    }

    @Override
    public double[] getParameters() {
        return parameters;
    }
}
