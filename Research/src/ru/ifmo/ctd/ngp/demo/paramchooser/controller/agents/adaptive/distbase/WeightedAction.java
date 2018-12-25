package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.distbase;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Action;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.splitter.Splittable;

/**
 * @author Arkadii Rost
 */
public class WeightedAction<A extends Action> implements Splittable {
	private final A action;
	private final double reward;

	private int splitIndex;

	public WeightedAction(A action, double reward) {
		this.action = action;
		this.reward = reward;
	}

	public A getAction() {
		return action;
	}

	public double getReward() {
		return reward;
	}

	public void setSplitIndex(int index) {
		splitIndex = index;
	}

	@Override
	public double getX() {
		return action.getParameterValues()[splitIndex];
	}

	@Override
	public double getY() {
		return getReward();
	}
}
