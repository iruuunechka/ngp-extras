package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.distbase;

import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Agent;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Bounded;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.State;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.QAction;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.QLearner;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.RandomWrapper;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.SimpleBounded;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.splitter.SplitResult;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.splitter.Splittable;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.splitter.Splittables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Arkadii Rost
 */
public class DistAgent<S extends State> implements Agent<QAction, S, DistLogState> {
	private final Bounded[] bounds;
	private final double minSplitPercent;
	private final double splitResolution;
	private final double eps;
	private final double alpha;
	private final double gamma;
	private final double actionScale;
	private final List<WeightedAction<QAction>> actionList;
	private final List<DistLogState> log;

	private SimplePartition[] partition;
	private QLearner learner;

	public DistAgent(Bounded[] bounds, double minSplitPercent, double splitResolution,
		double eps, double alpha, double gamma, double actionScale)
	{
		this.minSplitPercent = minSplitPercent;
		this.splitResolution = splitResolution;
		this.alpha = alpha;
		this.eps = eps;
		this.gamma = gamma;
		this.bounds = bounds;
		this.actionScale = actionScale;
		actionList = new ArrayList<>();
		log = new ArrayList<>();
	}

	@Override
	public void init(Random rand) {
		partition = new SimplePartition[bounds.length];
		for (int i = 0; i < partition.length; i++)
			partition[i] = new SimplePartition(bounds[i]);
		learner = new QLearner(alpha, eps, partition);
	}

	@Override
	public void update(Random rand, double reward, S lastState, QAction lastAction, S state) {
		log.add(new DistLogState(partition, lastAction));
		actionList.add(new WeightedAction<>(lastAction, reward));
		learner.updateQ(lastAction, actionDelta(reward, lastAction));
		if (learner.getMaxQ() < 1e-3 && rand.nextDouble() < 0.05) {
			KolmogorovSmirnovTest splitter = new KolmogorovSmirnovTest(new RandomWrapper(rand));
			partition = createPartitions(splitter, actionList);
			learner = new QLearner(alpha, eps, partition);
		}
	}

	private double actionDelta(double reward, QAction lastAction) {
		double c = reward == 0 ? actionScale : 1;
		return c * (reward + gamma * learner.getMaxQ() - learner.getQ(lastAction));
	}

	private SimplePartition[] createPartitions(KolmogorovSmirnovTest splitter, List<WeightedAction<QAction>> actionList) {
		SimplePartition[] partition = new SimplePartition[bounds.length];
		for (int param = 0; param < partition.length; param++) {
			configureForParameter(actionList, param);
			partition[param] = createPartition(splitter, actionList, param);
		}
		return partition;
	}

	private SimplePartition createPartition(KolmogorovSmirnovTest splitter, List<? extends Splittable> actionList, int param) {
		int offset = (int)(actionList.size() * minSplitPercent);
		if (offset < 2)
			return new SimplePartition(bounds[param]);
		SplitResult splitRes = Splittables.split(splitter, actionList, offset, splitResolution);
		if (splitRes == null)
			return new SimplePartition(bounds[param]);
		List<Splittable> left = select(actionList, s -> s.getX() < splitRes.getSplitPoint());
		List<Splittable> right = select(actionList, s -> s.getX() > splitRes.getSplitPoint());
		int leftOffset = Math.max((int)(left.size() * minSplitPercent), 2);
		SplitResult leftSplitRes = Splittables.split(splitter, left, leftOffset, eps);
		int rightOffset = Math.max((int) (right.size() * minSplitPercent), 2);
		SplitResult rightSplitRes = Splittables.split(splitter, right, rightOffset, eps);
		if (leftSplitRes == null && rightSplitRes == null) {
			return new SimplePartition(new SimpleBounded(bounds[param].getLowerBound(), splitRes.getSplitPoint()),
				  new SimpleBounded(splitRes.getSplitPoint(), bounds[param].getUpperBound()));
		} else if (rightSplitRes == null) {
			return new SimplePartition(new SimpleBounded(bounds[param].getLowerBound(), leftSplitRes.getSplitPoint()),
				  new SimpleBounded(leftSplitRes.getSplitPoint(), splitRes.getSplitPoint()),
				  new SimpleBounded(splitRes.getSplitPoint(),bounds[param].getUpperBound()));
		} else if (leftSplitRes == null) {
			return new SimplePartition(new SimpleBounded(bounds[param].getLowerBound(), splitRes.getSplitPoint()),
				  new SimpleBounded(splitRes.getSplitPoint(), rightSplitRes.getSplitPoint()),
				  new SimpleBounded(rightSplitRes.getSplitPoint(),bounds[param].getUpperBound()));
		} else {
			return new SimplePartition(new SimpleBounded(bounds[param].getLowerBound(), leftSplitRes.getSplitPoint()),
				new SimpleBounded(leftSplitRes.getSplitPoint(), splitRes.getSplitPoint()),
				new SimpleBounded(splitRes.getSplitPoint(), rightSplitRes.getSplitPoint()),
				new SimpleBounded(rightSplitRes.getSplitPoint(),bounds[param].getUpperBound()));
		}
	}

	@Override
	public QAction getParameterValues(Random rand, S state) {
		return learner.get(rand);
	}

	@Override
	public Collection<DistLogState> getLog() {
		return log;
	}

	private static void configureForParameter(List<WeightedAction<QAction>> actionList, int param) {
		for (WeightedAction<QAction> act : actionList)
			act.setSplitIndex(param);
	}

	private List<Splittable> select(List<? extends Splittable> splittables, Predicate<Splittable> pred) {
		return splittables.stream().filter(pred).collect(Collectors.toList());
	}
}
