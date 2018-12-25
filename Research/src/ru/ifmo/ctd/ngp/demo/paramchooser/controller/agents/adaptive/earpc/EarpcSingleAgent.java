package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.earpc;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Bounded;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.State;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.BaseTransition;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.AdaptiveAction;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.utree.SingleNodeAgent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * @author Arkadii Rost
 */
public class EarpcSingleAgent<S extends State> extends SingleNodeAgent<AdaptiveAction, S,
	  BaseTransition<AdaptiveAction, S>, EarpcState<S, BaseTransition<AdaptiveAction, S>>, EarpcLogState>
{
	private final List<EarpcLogState> log;
	private final Bounded[] bounds;
	private final double eps;

	public EarpcSingleAgent(Bounded[] bounds, double eps) {
		this.bounds = bounds;
		this.eps = eps;
		log = new ArrayList<>();
	}

	@Override
	protected EarpcState<S, BaseTransition<AdaptiveAction, S>> createUState(Random rand) {
		return new EarpcState<>(bounds, eps);
	}

	@Override
	public void update(Random rand, double reward, S lastState, AdaptiveAction lastAction, S state) {
		EarpcState<S, BaseTransition<AdaptiveAction, S>> uState = getUState();
		log.add(new EarpcLogState(lastAction.getParameterValues(), uState.getSplitPoints()));
		uState.updateTransitions(rand, new BaseTransition<>(reward, lastState, lastAction, state));
	}

	@Override
	public Collection<EarpcLogState> getLog() {
		return log;
	}
}
