package ru.ifmo.ctd.ngp.theory.moearl;

import ru.ifmo.ctd.ngp.learning.util.Map2;
import ru.ifmo.ctd.ngp.learning.util.Maps;
import ru.ifmo.ctd.ngp.util.FastRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Irene Petrova
 */
public class GreedyQAgent implements Agent {
    private final Map2<Integer, Integer, Double> Q;
    private final List<Integer> actions;
    private final double alpha;
    private final double gamma;
    private final double epsilon;
    private final Random rand;

    public GreedyQAgent(int actionsCount, double alpha, double gamma, double epsilon) {
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        rand = FastRandom.threadLocal();
        Q = new Map2<>(0.0);
        actions = new ArrayList<>();
        for (int i = 0; i < actionsCount; ++i) {
            actions.add(i);
        }
    }

    public int selectAction(int state) {
        if (rand.nextDouble() < epsilon) {
            return actions.get(rand.nextInt(actions.size()));
        } else {
            return Maps.argMax(Q, state, actions);
        }
    }

    public void updateExperience(int state, int newState, int action, int reward) {
        double old = Q.get(state, action);
        Q.put(state, action, old + alpha * (reward +
                gamma * Maps.max(Q, newState, actions) - old));
    }
}
