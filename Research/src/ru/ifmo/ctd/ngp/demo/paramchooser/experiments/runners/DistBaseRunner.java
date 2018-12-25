package ru.ifmo.ctd.ngp.demo.paramchooser.experiments.runners;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Agent;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.distbase.DistAgent;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.distbase.DistLogState;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.QAction;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.SimpleState;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.watchmaker.SplittedParameter;

/**
 * @author Arkadii Rost
 */
public class DistBaseRunner extends Runner<QAction, DistLogState> {
	public DistBaseRunner(int mu, RunConfig runConfig) {
		super(mu, runConfig);
	}

	@Override
	protected Agent<QAction, SimpleState, DistLogState> createAgent(SplittedParameter[] params, RunConfig runConfig) {
		return new DistAgent<>(params, runConfig.getMinSplitPercent(), runConfig.getParameterSplitResolution(),
			  runConfig.getEps(), runConfig.getAlpha(), runConfig.getGamma(), runConfig.getActionScale());
	}
}
