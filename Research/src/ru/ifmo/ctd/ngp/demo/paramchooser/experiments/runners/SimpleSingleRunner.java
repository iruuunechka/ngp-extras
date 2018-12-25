package ru.ifmo.ctd.ngp.demo.paramchooser.experiments.runners;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Agent;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.QAction;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.QLogState;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.simple.SimpleSingleAgent;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.SimpleState;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.watchmaker.SplittedParameter;

/**
 * @author Arkadii Rost
 */
public class SimpleSingleRunner extends Runner<QAction, QLogState> {
	public SimpleSingleRunner(int mu, RunConfig runConfig) {
		super(mu, runConfig);
	}

	@Override
	protected Agent<QAction, SimpleState, QLogState> createAgent(SplittedParameter[] params, RunConfig runConfig) {
		return new SimpleSingleAgent<>(params, runConfig.getEps(), runConfig.getAlpha(), runConfig.getGamma(),
			  runConfig.getActionScale());
	}
}