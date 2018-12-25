package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.model;

import ru.ifmo.ctd.ngp.learning.util.Map2;
import ru.ifmo.ctd.ngp.learning.util.Map3;

/**
 * Interface of model for model-based algorithms
 * @author Irene Petrova
 */
public interface Model<S, A> {
    /**
     * Update model parameters
     * @param s the previous state
     * @param a the action in state s
     * @param ss the current state
     * @param r the reward for action a
     */
    void updateModel(S s, A a, S ss, double r);
    Map3<S, A, S, Double> getT();
    Map2<S, A, Double> getR();
    void refresh();
}
