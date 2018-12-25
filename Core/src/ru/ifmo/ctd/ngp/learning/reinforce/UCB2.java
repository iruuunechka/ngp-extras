package ru.ifmo.ctd.ngp.learning.reinforce;

import java.util.List;

public class UCB2<S, A> extends AbstractAgent<S, A, UCB2<S, A>> {
    private final double alpha;

    public UCB2(double alpha) {
        this.alpha = alpha;
    }

    @Override
    protected UCB2<S, A> self() {
        return this;
    }

    @Override
    public int learn(Environment<S, A> environment) {
        List<A> actions = environment.getActions();

        long[] times = new long[actions.size()];
        double[] sumRewards = new double[actions.size()];
        double[] rPows = new double[actions.size()];

        for (int i = 0; !environment.isInTerminalState() && i < times.length; ++i) {
            sumRewards[i] = environment.applyAction(actions.get(i));
            times[i] = 1;
            rPows[i] = 1;
        }

        long callCount = times.length;
        while (!environment.isInTerminalState()) {
            double bestHoeffding = Double.NEGATIVE_INFINITY;
            int bestIndex = -1;
            for (int i = 0; i < times.length; ++i) {
                double tau = Math.ceil(rPows[i]);
                double a = Math.sqrt((1 + alpha) * Math.log(Math.E * callCount / tau) / (2 * tau));
                double currHoeffding = sumRewards[i] / times[i] + a;
                if (bestHoeffding < currHoeffding) {
                    bestHoeffding = currHoeffding;
                    bestIndex = i;
                }
            }
            int prevRPow = (int) Math.ceil(rPows[bestIndex]);
            rPows[bestIndex] *= 1 + alpha;
            int nextRPow = (int) Math.ceil(rPows[bestIndex]);

            for (int i = prevRPow; i < nextRPow; ++i) {
                sumRewards[bestIndex] += environment.applyAction(actions.get(bestIndex));
                ++times[bestIndex];
                ++callCount;
            }
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
