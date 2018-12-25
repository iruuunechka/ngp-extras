package ru.ifmo.ctd.ngp.demo.paramchooser.experiments.runners;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Agent;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.AdaptiveAction;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.earpc.EarpcLogState;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.earpc.EarpcSingleAgent;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.SimpleState;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.watchmaker.SplittedParameter;

/**
 * @author Arkadii Rost
 */
public class EarpcSingleRunner extends Runner<AdaptiveAction, EarpcLogState> {
	public EarpcSingleRunner(int mu, RunConfig runConfig) {
		super(mu, runConfig);
	}

	@Override
	protected Agent<AdaptiveAction, SimpleState, EarpcLogState> createAgent(SplittedParameter[] params,
        RunConfig runConfig)
	{
		return new EarpcSingleAgent<>(params, runConfig.getEps());
	}

}

