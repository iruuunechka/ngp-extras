package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.nonStationary;

import ru.ifmo.ctd.ngp.learning.reinforce.AbstractAgent;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.Environment;
import ru.ifmo.ctd.ngp.learning.util.Map2;
import ru.ifmo.ctd.ngp.learning.util.Map3;

import java.util.ArrayList;
import java.util.List;

/**
 @author Irene Petrova
 */

public class RLCD<S, A> extends AbstractAgent<S, A, RLCD<S, A>> {


    private class Model {

        private double E;
        private final Map3<S, A, S, Double> T;
        private final Map2<S, A, Double> R;
        private final Map2<S, A, Double> N;
        private final Map2<S, A, Double> Q;

        Model(int stateCount) {
            E = 0;
            T = new Map3<>(1.0/stateCount);
            R = new Map2<>(0.0);
            N = new Map2<>(0.0);
            Q = new Map2<>(0.0);
        }

        private double deltaR(S s, A a, double r) {
            return (r - R.get(s, a)) / (N.get(s, a) + 1);
        }

        private double deltaT(S s, A a, S k, S s1) {
            if (k == s1) {
                return (1 - T.get(s, a, k)) / (N.get(s, a) + 1);
            } else return (0 - T.get(s, a, k)) / (N.get(s, a) + 1);
        }

        private void updateModel(S s, A a, S s1, double r) {
            for (S k : T.projection(s, a).keySet()) {
                T.put(s, a, k, T.get(s, a, k) + deltaT(s, a, k, s1));
            }
            R.put(s, a, deltaR(s, a, r));
            N.put(s, a, Math.min(N.get(s, a), M));
        }
        /**
         *
         * @return 1 / (Rmax - Rmin)
         */
        private double countZr() {
            double Rmin = Double.POSITIVE_INFINITY;
            double Rmax = Double.NEGATIVE_INFINITY;
            for (S s : R.keySet1()) {
                for (A a : R.keySet2()) {
                    if (R.get(s, a) < Rmin) {
                        Rmin = R.get(s, a);
                    }
                    if (R.get(s, a) > Rmax) {
                        Rmax = R.get(s, a);
                    }
                }
            }
            return 1.0 / (Rmax - Rmin);
        }


        public void updateE(S s, A a, double r, S s1) {
            double dr = deltaR(s, a, r);
            double eR = 1 - 2 * countZr() * (dr * dr);

            double Zt = (N.get(s, a) + 1) * (N.get(s, a) + 1) / 2;
            double sumdT = 0;
            for (S k : T.projection(s, a).keySet()) {
                sumdT += deltaT(s, a, k, s1);
            }
            double eT = 1 - 2 * Zt * sumdT;

            double c = N.get(s, a) / M;
            double e = c * (omega * eR + (1 - omega) * eT);

            E = E + rho * (e - E);
        }
    }

    private Model active;
    private final List<Model> availiableModels;
    private final double omega;
    private final double Emin;
    private final double rho;
    private final int stateCount;
    private final int M;


    public RLCD(double omega, double emin, double rho, int m, int stateCount) {
        this.omega = omega;
        Emin = emin;
        this.M = m;
        this.rho = rho;
        this.stateCount = stateCount;
        active = new Model(stateCount);
        availiableModels = new ArrayList<>();
        availiableModels.add(active);
    }

    private void updateActive() {
        for (Model m : availiableModels) {
            if (m.E > active.E) {
                active = m;
            }
        }
        if (active.E < Emin) {
            active = new Model(stateCount);
            availiableModels.add(active);
        }
    }
    @Override
    public int learn(Environment<S, A> environment) {
        A a = environment.firstAction();
        int steps = 0;
        while (!environment.isInTerminalState()) {
            S s = environment.getCurrentState();
            double r = environment.applyAction(a);
            S s1 = environment.getCurrentState();
            active.updateE(s, a, r, s1);
            updateActive();
            active.updateModel(s, a, s1, r);
            steps++;
            updatePrinters(environment);
            a = chooseAction(environment.getActions(), s1);
        }
        return steps;
    }

    private A chooseAction(List<A> actions, S s1) {
        return null;
        //TODO
    }

    @Override
    public void refresh() {
    }

    @Override
    protected RLCD<S, A> self() {
        return this;
    }

    @Override
    public Agent<S, A> makeClone() {
        throw new UnsupportedOperationException();
    }

}
