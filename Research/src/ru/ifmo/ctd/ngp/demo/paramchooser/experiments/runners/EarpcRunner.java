package ru.ifmo.ctd.ngp.demo.paramchooser.experiments.runners;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Agent;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.AdaptiveAction;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.earpc.EarpcAgent;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.earpc.EarpcLogState;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.SimpleState;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.watchmaker.SplittedParameter;

/**
 * @author Arkadii Rost
 */
public class EarpcRunner extends Runner<AdaptiveAction, EarpcLogState> {
	public EarpcRunner(int mu, RunConfig runConfig) {
		super(mu, runConfig);
	}

	@Override
	protected Agent<AdaptiveAction, SimpleState, EarpcLogState> createAgent(SplittedParameter[] params,
        RunConfig runConfig)
	{
		return new EarpcAgent<>(params, runConfig.getEps(), runConfig.getGamma(), runConfig.getObservableSplitResolution(),
			  runConfig.getMinSplitPercent(), runConfig.getSplitLimit());
	}

}
