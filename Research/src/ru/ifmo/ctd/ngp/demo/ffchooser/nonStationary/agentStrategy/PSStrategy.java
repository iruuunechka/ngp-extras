package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.agentStrategy;

import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.model.Model;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.strategy.Strategy;
import ru.ifmo.ctd.ngp.learning.util.Map2;
import ru.ifmo.ctd.ngp.learning.util.Map3;
import ru.ifmo.ctd.ngp.util.FastRandom;

import java.util.*;

/**
 * @author Irene Petrova
 */
public class PSStrategy<S, A> implements AgentStrategy<S, A> {
    private class Tuple3 {
        private Tuple3(S s, A a, double v) {
            this.s = s;
            this.a = a;
            this.v = v;
        }

        private final S s;
        private final A a;
        private final double v;
    }

    private final Map2<S, A, Double> R;
    private final Map3<S, A, S, Double> T;
    private final Map<S, Double> V;
    private final Map2<S, A, Double> pr;

    private final double probability;
    private double curProbability;
    private final double discount;
    private final int k;
    private final Strategy strategy;
    private Strategy curStrategy;

    private int steps;
    private final Random rand;

    public PSStrategy(Model<S, A> model, double probability, double discount, int k, Strategy strategy) {
        this.probability = probability;
        this.discount = discount;
        this.k = k;
        this.strategy = strategy;
        this.steps = 0;
        T = model.getT();
        R = model.getR();
        rand = FastRandom.threadLocal();
        pr  = new Map2<>(0.0);
        V = new HashMap<>();
        curProbability = probability;
        curStrategy = strategy.make_clone();
    }

    @Override
    public void updateStrategy(S s, A a, List<A> actions) {
        TreeSet<Tuple3> kmax = new TreeSet<>(Comparator.comparingDouble(tuple3 -> tuple3.v));

        for (S si: pr.keySet1()) {
            for (A ai: pr.keySet2()) {
                double cur = pr.get(si, ai);
                if (kmax.size() < k) {
                    kmax.add(new Tuple3(si, ai, cur));
                } else {
                    if (kmax.first().v < cur) {
                        kmax.pollFirst();
                        kmax.add(new Tuple3(si, ai, cur));
                    }
                }
            }
        }

        for (Tuple3 pair : kmax) {
            S si = pair.s;
            double vold = V.containsKey(si) ? V.get(si) : 0;

            double max = Double.NEGATIVE_INFINITY;
            for (A acur: R.keySet2()) {
                double maxArg = 0;
                for (S scur : T.projection(si, acur).keySet()) {
                    maxArg += T.get(si, acur, scur) * (V.containsKey(scur) ? V.get(scur) : 0);
                }
                maxArg = R.get(si, acur) + discount * maxArg;
                if (max < maxArg) {
                    max = maxArg;
                }
            }
            V.put(si, max);

            pr.put(si, pair.a, 0.0);

            for (S scur : T.keySet1()) {
                for (A acur : T.keySet2()) {
                    pr.put(scur, acur, pr.get(scur, acur) + Math.abs(vold - V.get(si)) * T.get(scur, acur, si));
                }
            }
        }
        curProbability = curStrategy.changeRandProbability(curProbability, steps);
        steps++;
    }

    @Override
    public A chooseAction(List<A> actions, S state) {
        if (rand.nextDouble() < curProbability) {
            return actions.get(rand.nextInt(actions.size()));
        } else {
            A argmax = actions.get(0);
            double max = Double.NEGATIVE_INFINITY;
            for (A a: R.keySet2()) {
                double curmax = 0;
                for (S u : T.projection(state, a).keySet()) {
                    curmax += T.get(state, a, u) * (V.containsKey(u) ? V.get(u) : 0);
                }
                curmax += R.get(state, a);
                if (max < curmax) {
                    max = curmax;
                    argmax = a;
                }
            }
            return argmax;
        }
    }

    @Override
    public int getSteps() {
        return steps;
    }

    @Override
    public void refresh() {
        V.clear();
        pr.clear();
        steps = 0;
        curProbability = probability;
        curStrategy = strategy.make_clone();

    }

    public double getProbability() {
        return probability;
    }

    public double getDiscount() {
        return discount;
    }

    public int getK() {
        return k;
    }

    public Strategy getStrategy() {
        return strategy;
    }
}
