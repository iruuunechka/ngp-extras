package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.agent;

import java.util.Arrays;
import java.util.Random;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MulticriteriaOptimizationAlgorithm;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.q.QAgent;
import ru.ifmo.ctd.ngp.learning.util.Map2;
import ru.ifmo.ctd.ngp.learning.util.Maps;

/**
 * @author Irene Petrova
 */
public class MyEGreedyAgent<S, A> extends QAgent<S, A, MyEGreedyAgent<S, A>> {
    private final int evalPerIter;
    private final double alpha;
    private final double gamma;
    private final MulticriteriaOptimizationAlgorithm algo;
    private final int[] evalPerHelper;
    private final double eps;

    public MyEGreedyAgent(int actionsCount, double alpha, double gamma, double eps, MulticriteriaOptimizationAlgorithm algo) {
        this.evalPerIter = actionsCount * 2;
        this.alpha = alpha;
        this.gamma = gamma;
        this.algo = algo;
        this.eps = eps;
        evalPerHelper = new int[actionsCount];
        assert (evalPerIter >= actionsCount);
        for (int i = 0; i < evalPerHelper.length - 1; ++i) {
            evalPerHelper[i] = this.evalPerIter / evalPerHelper.length;
        }
        evalPerHelper[evalPerHelper.length - 1] = evalPerIter - (evalPerHelper.length - 1) * evalPerHelper[0];
    }

    @Override
    protected double makeStep(A action) {
        double reward = environment.applyAction(action);
        algo.setPopulation(algo.getCurrentCriterion());
        return reward;
    }

    @Override
    protected A chooseAction(S state) {
        double[] reward = new double[actions.size()];
        int bestHelper = 0;
        double maxReward = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < actions.size(); ++i) {
            algo.genGeneration(i);
            for (int n = 0; n < evalPerHelper[i]; ++n) {
                algo.computeValuesOfGeneration(i);
                reward[i] = environment.applyAction(actions.get(i));
                S newState = environment.getCurrentState();
                Double old = Q.get(state, actions.get(i));
                Q.put(state, actions.get(i), old + alpha * (reward[i] +
                        gamma * Maps.max(Q, newState, environment.getActions()) - old));
            }
            if (reward[i] / evalPerHelper[i] > maxReward) {
                maxReward = reward[i] / evalPerHelper[i];
                bestHelper = i;
            }
        }
        int actionMaxQ = 0;
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < actions.size(); ++i) {
            if (Q.get(state, actions.get(i)) > max) {
                max = Q.get(state, actions.get(i));
                actionMaxQ = i;
            }
        }
        //MersenneTwisterRNG rand = new MersenneTwisterRNG();
        Random rand = new Random();
        Arrays.fill(evalPerHelper, 0);
        for (int i = 0; i < evalPerIter; ++i) {
            if (Math.random() < eps) {
                evalPerHelper[rand.nextInt(actions.size())]++;
            } else {
                evalPerHelper[actionMaxQ]++;
            }
        }

        return actions.get(bestHelper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "EGreedyAgent" + evalPerIter + "gamma" + gamma + "alpha" + alpha + "eps" + eps;
    }

    @Override
    public void refresh() {
        Q = new Map2<>(0.0);
    }

    @Override
    public Agent<S, A> makeClone() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected MyEGreedyAgent<S, A> self() {
        return this;
    }
}