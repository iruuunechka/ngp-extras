package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.model;

import ru.ifmo.ctd.ngp.learning.util.Map2;
import ru.ifmo.ctd.ngp.learning.util.Map3;

/**
 * Model for algorithm Dyna
 * @author Irene Petrova
 */
public class DynaModel<S, A> implements Model<S, A> {
    private final Map2<S, A, Integer> n1;
    private final Map3<S, A, S, Integer> n2;
    private final Map3<S, A, S, Double> T;
    private final Map2<S, A, Double> R;

    public DynaModel() {
        T = new Map3<>(1.0);
        R = new Map2<>(0.0);
        n1 = new Map2<>(0);
        n2 = new Map3<>(0);
    }
    @Override
    public void updateModel(S s, A a, S ss, double r) {
        n1.put(s, a, 1 + n1.get(s, a));
        n2.put(s, a, ss, n2.get(s, a, ss) + 1);

        double rsa = R.get(s, a);
        R.put(s, a, R.get(s, a) + (r - rsa) / n1.get(s, a));

        for (S u : T.projection(s, a).keySet()) {
            double tsau = T.get(s, a, u);
            T.put(s, a, u, tsau - tsau / n2.get(s, a, u));
        }

        T.put(s, a, ss, T.get(s, a, ss) + 1.0 / n2.get(s, a, ss));
    }

    @Override
    public Map3<S, A, S, Double> getT() {
        return T;
    }

    @Override
    public Map2<S, A, Double> getR() {
        return R;
    }

    @Override
    public void refresh() {
        T.clear();
        R.clear();
        n1.clear();
        n2.clear();
    }
}
