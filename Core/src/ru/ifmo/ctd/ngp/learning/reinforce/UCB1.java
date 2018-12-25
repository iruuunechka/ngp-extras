package ru.ifmo.ctd.ngp.learning.reinforce;

import java.util.List;

public class UCB1<S, A> extends AbstractAgent<S, A, UCB1<S, A>> {
    @Override
    protected UCB1<S, A> self() {
        return this;
    }

    @Override
    public int learn(Environment<S, A> environment) {
        List<A> actions = environment.getActions();

        long[] times = new long[actions.size()];
        double[] sumRewards = new double[actions.size()];

        for (int i = 0; !environment.isInTerminalState() && i < times.length; ++i) {
            sumRewards[i] = environment.applyAction(actions.get(i));
            times[i] = 1;
        }

        long callCount = times.length;
        while (!environment.isInTerminalState()) {
            double bestHoeffding = Double.NEGATIVE_INFINITY;
            int bestIndex = -1;
            for (int i = 0; i < times.length; ++i) {
                double currHoeffding = sumRewards[i] / times[i] + Math.sqrt(2 * Math.log(callCount) / times[i]);
                if (bestHoeffding < currHoeffding) {
                    bestHoeffding = currHoeffding;
                    bestIndex = i;
                }
            }
            sumRewards[bestIndex] += environment.applyAction(actions.get(bestIndex));
            ++times[bestIndex];
            ++callCount;
        }
        return (int) callCount;
    }

    @Override
    public void refresh() {

    }

    @Override
    public Agent<S, A> makeClone() {
        return this;
    }
}
