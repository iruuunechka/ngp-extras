package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn;

import java.util.Arrays;
import java.util.Random;

/**
 * @author Arkadii Rost
 */
public class QLearner {
	protected final double alpha;
	protected final Partition[] splits;
	protected double maxQ;
	protected final double[] q;
	protected final double eps;

	public QLearner(double alpha, double eps, Partition[] splits) {
		this.alpha = alpha;
		this.eps = eps;
		this.splits = splits;
		int stateCount = Arrays.stream(splits)
			  .mapToInt(Partition::getSplitCount)
			  .reduce(1, (a, b) -> a * b);
		q = new double[stateCount];
	}

	public double getMaxQ() {
		return maxQ;
	}

	public double getQ(QAction a) {
		return q[a.getActionId()];
	}

	public void updateQ(QAction lastAction, double actionDelta) {
		int actionId = lastAction.getActionId();
		q[actionId] += alpha * actionDelta;
		maxQ = Arrays.stream(q).max().orElse(0);
	}

	public QAction get(Random rand) {
		int action = selectAction(rand);
		int[] splits = getSplits(action);
		double[] values = new double[this.splits.length];
		for (int i = 0; i < values.length; i++)
			values[i] = this.splits[i].chooseParameterValue(rand, splits[i]);
		return new QAction(action, values);
	}

	protected int[] getSplits(int action) {
		int[] ranges = new int[splits.length];
		for (int i = 0; i < splits.length; i++) {
			int count = splits[i].getSplitCount();
			ranges[i] = action % count;
			action /= count;
		}
		return ranges;
	}

	protected int selectAction(Random rand) {
		double maxQ = Double.NEGATIVE_INFINITY;
		if (rand.nextDouble() < eps)
			return rand.nextInt(q.length);
		int bestId = -1;
		for (int i = 0; i < q.length; i++) {
			if (q[i] > maxQ || (q[i] == maxQ && rand.nextBoolean())) {
				maxQ = q[i];
				bestId = i;
			}
		}
		return bestId;
	}
}
