package ru.ifmo.ctd.ngp.demo.paramchooser.experiments.runners;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Agent;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.QAction;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.QLogState;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.simple.SimpleAgent;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.SimpleState;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.watchmaker.SplittedParameter;

/**
 * @author Arkadii Rost
 */
public class SimpleRunner extends Runner<QAction, QLogState> {
	public SimpleRunner(int mu, RunConfig runConfig) {
	  super(mu, runConfig);
	  }

	@Override
	protected Agent<QAction, SimpleState, QLogState> createAgent(SplittedParameter[] params, RunConfig runConfig) {
		return new SimpleAgent<>(params, runConfig.getEps(), runConfig.getAlpha(),
			  runConfig.getGamma(), runConfig.getActionScale(), runConfig.getObservableSplitResolution(), runConfig.getMinSplitPercent(),
			  runConfig.getSplitLimit());
	}
}
