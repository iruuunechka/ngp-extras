package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.agent;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MulticriteriaOptimizationAlgorithm;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.q.QAgent;
import ru.ifmo.ctd.ngp.learning.util.Map2;
import ru.ifmo.ctd.ngp.learning.util.Maps;

/**
 * @author Irene Petrova
 */
public class SoftMaxAgent<S, A> extends QAgent<S, A, SoftMaxAgent<S, A>> {
    private final int evalPerIter;
    private final double alpha;
    private final double gamma;
    private final MulticriteriaOptimizationAlgorithm algo;
    private final int[] evalPerHelper;

    public SoftMaxAgent(int actionsCount, double alpha, double gamma, MulticriteriaOptimizationAlgorithm algo) {
        this.evalPerIter = actionsCount * 2;
        this.alpha = alpha;
        this.gamma = gamma;
        this.algo = algo;
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
                maxReward = reward[i];
                bestHelper = i;
            }
        }
        double Qsum = 0;
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        double tau = 10;
        double[] newQ = new double[actions.size()];
        for (int i = 0; i < actions.size(); ++i) {
            double val = Math.exp(Q.get(state, actions.get(i)) / tau);
            if (val > max) {
                max = val;
            }
            if (val < min) {
                min = val;
            }
            newQ[i] = val;
        }
        for (int i = 0; i < actions.size(); ++i) {
            newQ[i] = (newQ[i] - min) / (max - min);
            Qsum += newQ[i];
        }
        int iterSum = 0;
        for (int i = 1; i < actions.size(); ++i) {
            evalPerHelper[i] = 1 + (int)Math.floor(newQ[i] / Qsum * (evalPerIter - evalPerHelper.length));
            iterSum += evalPerHelper[i];
        }
        evalPerHelper[0] = evalPerIter - iterSum;

        return actions.get(bestHelper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "SoftMaxAgent" + evalPerIter + "gamma" + gamma + "alpha" + alpha;
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
    protected SoftMaxAgent<S, A> self() {
        return this;
    }
}