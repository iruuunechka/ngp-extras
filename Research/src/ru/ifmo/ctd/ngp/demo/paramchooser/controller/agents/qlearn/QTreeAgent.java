package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.State;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Transition;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.utree.UTreeAgent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * @author Arkadii Rost
 */
public abstract class QTreeAgent<S extends State, T extends Transition<QAction, S>,
        U extends QState<S, T>> extends UTreeAgent<QAction, S, T, U, QLogState>
{
	protected final double eps;
	protected final double alpha;
	protected final List<QLogState> log;
	private final double actionScale;

	protected QTreeAgent(double eps, double alpha, double gamma, double actionScale, double obsResolution, double minSplitPercent, int splitLimit) {
        super(gamma, obsResolution, minSplitPercent, splitLimit);
	    this.eps = eps;
	    this.alpha = alpha;
		this.actionScale = actionScale;
		log = new ArrayList<>();
    }

    @Override
    protected void updateTransitions(Random rand, double reward, S lastState, QAction lastAction, S state, U uState) {
	    log.add(new QLogState(lastAction));
        super.updateTransitions(rand, reward, lastState, lastAction, state, uState);
	    if (lastAction != null) {
		    double actionDelta = actionDelta(reward, uState, lastAction, getUState(state));
		    updateQ(uState, lastAction, actionDelta);
	    }
    }

    protected void updateQ(U lastUState, QAction lastAction, double actionDelta) {
        lastUState.updateQ(lastAction, actionDelta);
    }

    protected double actionDelta(double reward, QState<S, T> lastUState, QAction lastAction, QState<S, T> uState) {
        double nextQ = uState.getEffectiveQ();
        double Q = lastUState.getQ(lastAction);
	    double c = reward == 0 ? actionScale : 1;
        return c * (reward + gamma * nextQ - Q);
    }

	@Override
	public Collection<QLogState> getLog() {
		return log;
	}
}
