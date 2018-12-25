package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.agentStrategy;

import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.agentStrategy.AgentStrategy;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.model.Model;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.strategy.Strategy;
import ru.ifmo.ctd.ngp.learning.util.Map2;
import ru.ifmo.ctd.ngp.learning.util.Map3;
import ru.ifmo.ctd.ngp.learning.util.Maps;
import ru.ifmo.ctd.ngp.util.CollectionsEx;
import ru.ifmo.ctd.ngp.util.FastRandom;

import java.util.List;
import java.util.Random;

/**
 * @author Irene Petrova
 */
public class DynaStrategy<S, A> implements AgentStrategy<S, A> {

    private final Map2<S, A, Double> R;
    private final Map3<S, A, S, Double> T;
    private final Map2<S, A, Double> Q;
    private final double probability;
    private double curProbability;
    private final double discount;
    private final int k;
    private final Strategy strategy;
    private Strategy curStrategy;
    private int steps;
    private final Random rand;

    public DynaStrategy(Model<S, A> model, double probability, double discount, int k, Strategy strategy) {
        this.probability = probability;
        this.discount = discount;
        this.k = k;
        this.strategy = strategy;
        this.curStrategy = strategy.make_clone();
        this.steps = 0;
        Q = new Map2<>(0.0);
        T = model.getT();
        R = model.getR();
        rand = FastRandom.threadLocal();
        curProbability = probability;
    }

    private void updatePair(S s, A a, List<A> actions) {
        Q.put(s, a, R.get(s, a));
        for (S ssum : T.projection(s, a).keySet()) {
            double max = Maps.max(Q, ssum, actions);
            Q.put(s, a, Q.get(s, a) + discount * T.get(s, a, ssum) * max);
        }
    }

    @Override
    public void updateStrategy(S s, A a, List<A> actions) {
        updatePair(s, a, actions);
        List<S> states = CollectionsEx.listFrom(Q.keySet1());
        for (int i = 0; i < k; i++) {
            updatePair(states.get(rand.nextInt(states.size())),
                    actions.get(rand.nextInt(actions.size())), actions);
        }
        curProbability = curStrategy.changeRandProbability(curProbability, steps);
        steps++;
    }

    @Override
    public A chooseAction(List<A> actions, S state) {
        if (rand.nextDouble() < curProbability) {
            return actions.get(rand.nextInt(actions.size()));
        } else {
            return Maps.argMax(Q, state, actions);
        }
    }

    @Override
    public int getSteps() {
        return steps;
    }

    @Override
    public void refresh() {
        steps = 0;
        Q.clear();
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
