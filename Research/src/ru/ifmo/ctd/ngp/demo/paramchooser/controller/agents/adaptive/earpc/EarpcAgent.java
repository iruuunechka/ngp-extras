package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.earpc;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Bounded;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.State;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.BaseTransition;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.AdaptiveAction;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.utree.UTreeAgent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * @author Arkadii Rost
 */
public class EarpcAgent<S extends State> extends UTreeAgent<AdaptiveAction, S,
	  BaseTransition<AdaptiveAction, S>, EarpcState<S, BaseTransition<AdaptiveAction, S>>, EarpcLogState>
{
    private final Bounded[] bounds;
	private final double eps;
	private final List<EarpcLogState> log;

	public EarpcAgent(Bounded[] bounds, double eps, double gamma, double obsResolution, double minSplitPercent, int splitLimit) {
        super(gamma, obsResolution, minSplitPercent, splitLimit);
        this.bounds = bounds;
		this.eps = eps;
		log = new ArrayList<>();
	}

	@Override
	protected BaseTransition<AdaptiveAction, S> createTransition(double reward, S lastState, AdaptiveAction lastAction, S state) {
		log.add(new EarpcLogState(lastAction.getParameterValues(), getUState(lastState).getSplitPoints()));
		return new BaseTransition<>(reward, lastState, lastAction, state);
	}

	@Override
	protected EarpcState<S, BaseTransition<AdaptiveAction, S>> createUState(Random rand, EarpcState<S, BaseTransition<AdaptiveAction, S>> prev, List<BaseTransition<AdaptiveAction, S>> transitions) {
		return new EarpcState<>(bounds, eps);
	}

	@Override
	protected void registerState(EarpcState<S, BaseTransition<AdaptiveAction, S>> state) {

	}

	@Override
	protected void deregisterState(EarpcState<S, BaseTransition<AdaptiveAction, S>> state) {

	}

	@Override
	public Collection<EarpcLogState> getLog() {
		return log;
	}
}
