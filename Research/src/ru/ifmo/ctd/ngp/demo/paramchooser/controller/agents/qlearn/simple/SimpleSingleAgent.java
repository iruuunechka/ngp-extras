package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.simple;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.State;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.BaseTransition;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.Partition;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.QAction;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.QLogState;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.QState;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.utree.SingleNodeAgent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * @author Arkadii Rost
 */
public class SimpleSingleAgent<S extends State> extends SingleNodeAgent<QAction, S, BaseTransition<QAction, S>,
	  QState<S, BaseTransition<QAction, S>>, QLogState>
{
	private final Partition[] splits;
	private final double eps;
	private final double alpha;
	private final double gamma;
	private final List<QLogState> log;
	private final double actionScale;

	public SimpleSingleAgent(Partition[] splits, double eps, double alpha, double gamma, double actionScale) {
		this.splits = splits;
		this.eps = eps;
		this.alpha = alpha;
		this.gamma = gamma;
		this.actionScale = actionScale;
		log = new ArrayList<>();
	}

	@Override
	protected QState<S, BaseTransition<QAction, S>> createUState(Random rand) {
		return new QState<>(alpha, eps, splits);
	}

	@Override
	public void update(Random rand, double reward, S lastState, QAction lastAction, S state) {
		log.add(new QLogState(lastAction));
		double actionDelta = actionDelta(reward, lastAction);
		getUState().updateQ(lastAction, actionDelta);
	}

	@Override
	public Collection<QLogState> getLog() {
		return log;
	}

	protected double actionDelta(double reward, QAction lastAction) {
		QState<S, BaseTransition<QAction, S>> uState = getUState();
		double nextQ = uState.getEffectiveQ();
		double Q = uState.getQ(lastAction);
		double c = reward == 0 ? actionScale : 1;
		return c * (reward + gamma * nextQ - Q);
	}
}
