package ru.ifmo.ctd.ngp.demo.paramchooser.controller;

/**
 * @author Arkadii Rost
 */
public interface Environment<A extends Action, S extends State> {
    Parameter[] getParameters();
    double getReward();
    S getState();

    default void apply(A action) {
        Parameter[] params = getParameters();
        double[] values = action.getParameterValues();
        assert  values.length == params.length;
        for (int i = 0; i < params.length; i++)
            params[i].setValue(values[i]);
    }
}
