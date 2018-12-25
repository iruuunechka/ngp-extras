package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.model;

import ru.ifmo.ctd.ngp.learning.util.Map2;
import ru.ifmo.ctd.ngp.learning.util.Map3;

/**
 * @author Irene Petrova
 */
public class RLCDModel<S, A> implements Model<S, A> {

    private double E;
    private final Map3<S, A, S, Double> T;
    private final Map2<S, A, Double> R;
    private final Map2<S, A, Double> N;
    private final double omega;
    private final double rho;
    private final int M;
    private final int modelNumber;


    /**
     * Constructs the {@link Model}
     * @param omega specifies the relative importance of rewards and transitions predictions(eR eT)
     * @param rho  is the adjustment coefficient for the quality
     * @param m is the number of stored experiments
     */
    public RLCDModel(double omega, double rho, int m, int modelNumber) {
        this(1, omega, rho, m, modelNumber);
    }

    /**
     * Constructs the {@link Model}
     * @param stateCount the number of possible states
     * @param omega specifies the relative importance of rewards and transitions predictions(eR eT)
     * @param rho  is the adjustment coefficient for the quality
     * @param m is the number of stored experiments
     */
    public RLCDModel(int stateCount, double omega, double rho, int m, int modelNumber) {
        this.omega = omega;
        this.rho = rho;
        M = m;
        E = 0.0;
        T = new Map3<>(1.0/stateCount);
        R = new Map2<>(0.0);
        N = new Map2<>(0.0);
        this.modelNumber = modelNumber;
    }

    private double deltaR(S s, A a, double r) {
        return (r - R.get(s, a)) / (N.get(s, a) + 1);
    }

    private double deltaT(S s, A a, S k, S s1) {
        if (k == s1) {
            return (1 - T.get(s, a, k)) / (N.get(s, a) + 1);
        } else return (0 - T.get(s, a, k)) / (N.get(s, a) + 1);
    }
    @Override
    public void updateModel(S s, A a, S s1, double r) {
        for (S k : T.projection(s, a).keySet()) {
            T.put(s, a, k, T.get(s, a, k) + deltaT(s, a, k, s1));
        }
        T.put(s, a, s1, T.get(s, a, s1) + deltaT(s, a, s1, s1));
        R.put(s, a, deltaR(s, a, r));
        N.put(s, a, Math.min(N.get(s, a) + 1, M));
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
        if (Rmax <= 0) {
            return 0;
        }
        //return 1.0 / (Rmax - Rmin);
        return 0.2;
    }


    public void updateE(S s, A a, double r, S s1) {
        double dr = deltaR(s, a, r);

        double Zt = (N.get(s, a) + 1) * (N.get(s, a) + 1) / 2;
        double sumdT = 0;
        for (S k : T.projection(s, a).keySet()) {
            double dt = deltaT(s, a, k, s1);
            sumdT += dt * dt;
        }
        double eT = 1 - 2 * Zt * sumdT;
        double eR = 1 - 2 * countZr() * (Zt * 2) * (dr * dr);

        double c = 1;//N.get(s, a) / M;
        double e = c * (omega * eR + (1 - omega) * eT);

        if (e - E < 0) {
            E = E + rho * (e - E);
        } else {
            E = E + rho * (e - E);
        }
    }


    public double getE() {
        return E;
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
    public String toString() {
        return String.valueOf(modelNumber);
    }

    @Override
    public void refresh() {
        T.clear();
        R.clear();
        N.clear();
        E = 0;
    }

}
