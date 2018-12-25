package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.agent;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MulticriteriaOptimizationAlgorithm;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.q.QAgent;
import ru.ifmo.ctd.ngp.learning.util.Map2;

/**
 * @author Irene Petrova
 */
public class EqualProbabilityAgent<S, A> extends QAgent<S, A, EqualProbabilityAgent<S, A>> {
    private final int evalPerIter;
    private final MulticriteriaOptimizationAlgorithm algo;
    private final int[] evalPerHelper;

    public EqualProbabilityAgent(int actionsCount, MulticriteriaOptimizationAlgorithm algo) {
        this.evalPerIter = actionsCount * 2;
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
            }
            if (reward[i] / evalPerHelper[i] > maxReward) {
                maxReward = reward[i];
                bestHelper = i;
            }
        }
        return actions.get(bestHelper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "EqualProbAgent" + evalPerIter;
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
    protected EqualProbabilityAgent<S, A> self() {
        return this;
    }
}
