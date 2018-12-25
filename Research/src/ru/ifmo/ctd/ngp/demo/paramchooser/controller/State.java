package ru.ifmo.ctd.ngp.demo.paramchooser.controller;

/**
 * @author Arkadii Rost
 */
public interface State {
    double[] getObservables();
    double[] getParameters();
}
