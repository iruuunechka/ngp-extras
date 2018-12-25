package ru.ifmo.ctd.ngp.demo.paramchooser.controller;

import java.util.Collection;
import java.util.Random;

/**
 * @author Arkadii Rost
 */
public interface Agent<A extends Action, S extends State, L> {
	void init(Random rand);
    void update(Random rand, double reward, S lastState, A lastAction, S state);
    A getParameterValues(Random rand, S state);
	Collection<L> getLog();
}
