package ru.ifmo.ctd.ngp.demo.paramchooser.controller;

/**
 * @author Arkadii Rost
 */
public interface Transition<A extends Action, S extends State> {
    S getFromState();
    A getAction();
    S getToState();
    double getReward();
}
