package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Action;

/**
 * @author Arkadii Rost
 */
public class BaseAction implements Action {
    private final double[] parameterValues;

    public BaseAction(double[] parameterValues) {
        this.parameterValues = parameterValues;
    }

    @Override
    public double[] getParameterValues() {
        return parameterValues;
    }
}
