package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.model;

import ru.ifmo.ctd.ngp.learning.util.Map2;
import ru.ifmo.ctd.ngp.learning.util.Map3;

/**
 * @author Irene Petrova
 */
public class PSModel<S, A> implements Model<S, A> {
    @Override
    public void updateModel(S s, A a, S ss, double r) {

    }

    @Override
    public Map3<S, A, S, Double> getT() {
        return null;
    }

    @Override
    public Map2<S, A, Double> getR() {
        return null;
    }

    @Override
    public void refresh() {

    }
}
