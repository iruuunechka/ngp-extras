package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.nonStationary;

import ru.ifmo.ctd.ngp.learning.reinforce.AbstractAgent;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.Environment;
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
public class DynaAgentStates<S, A> extends AbstractAgent<S, A, DynaAgentStates<S, A>> {
    private final double probability;
    private final double discount;
    private final int k;
    private final int stateCount;

    private Map3<S, A, S, Double> T;
    private Map2<S, A, Double> R;
    private Map2<S, A, Double> Q;
    private Map2<S, A, Integer> n1;
    private Map3<S, A, S, Integer> n2;

    /**
     * Constructs {@link DynaAgentStates} with the specified parameters
     * @param probability the probability of exploration
     * @param discount the discount factor
     * @param k the number of random state-action updates
     * @param stateCount the number of possible states
     */
    public DynaAgentStates(double probability, double discount, int k, int stateCount) {
        this.probability = probability;
        this.discount = discount;
        this.k = k;
        this.stateCount = stateCount;
        init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int learn(Environment<S, A> environment) {
        List<A> actions = environment.getActions();

        int steps = 0;
        A a = environment.firstAction();

        while (!environment.isInTerminalState()) {
            S s = environment.getCurrentState();
            double r = environment.applyAction(a);
            S ss = environment.getCurrentState();

            n1.put(s, a, 1 + n1.get(s, a));
            n2.put(s, a, ss, n2.get(s, a, ss) + 1);

            double rsa = R.get(s, a);
            R.put(s, a, R.get(s, a) + (r - rsa) / n1.get(s, a));

            for (S u : T.projection(s, a).keySet()) {
                double tsau = T.get(s, a, u);
                T.put(s, a, u, tsau - tsau / n2.get(s, a, u));
            }

            T.put(s, a, ss, T.get(s, a, ss) + 1.0 / n2.get(s, a, ss));

            updateStrategy(s, a, actions);

            Random rand = FastRandom.threadLocal();
            List<S> states = CollectionsEx.listFrom(Q.keySet1());
            for (int i = 0; i < k; i++) {
                updateStrategy(states.get(rand.nextInt(states.size())),
                        actions.get(rand.nextInt(actions.size())), actions);
            }

            updatePrinters(environment);
            a = chooseAction(actions, ss, rand);
            steps++;
        }

        return steps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh() {
        if (T != null) {
            T.clear();
            R.clear();
            Q.clear();
            n1.clear();
            n2.clear();
        }
    }

    @Override
    public Agent<S, A> makeClone() {
        return new DynaAgentStates<>(probability, discount, k, stateCount);
    }

    /**
     * @return the estimated transition probabilities
     */
    public Map3<S, A, S, Double> getT() {
        return T;
    }

    /**
     * @return the estimated reward function
     */
    public Map2<S, A, Double> getR() {
        return R;
    }

    /**
     * @return the estimated quality of state-action pairs
     */
    public Map2<S, A, Double> getQ() {
        return Q;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("dyna-p%sd%sk%d", probability, discount, k);
    }

    private void init() {
        T = new Map3<>(0.0);
        R = new Map2<>(0.0);
        Q = new Map2<>(0.0);
        n1 = new Map2<>(0);
        n2 = new Map3<>(0);
    }

    private void updateStrategy(S s, A a, List<A> actions) {
        Q.put(s, a, R.get(s, a));
        for (S ssum : T.projection(s, a).keySet()) {
            double max = Maps.max(Q, ssum, actions);
            Q.put(s, a, Q.get(s, a) + discount * T.get(s, a, ssum) * max);
        }
    }

    private A chooseAction(List<A> actions, S state, Random rand) {
        if (rand.nextDouble() < probability) {
            return actions.get(rand.nextInt(actions.size()));
        } else {
            return Maps.argMax(Q, state, actions);
        }
    }

    @Override
    protected DynaAgentStates<S, A> self() {
        return this;
    }
}

