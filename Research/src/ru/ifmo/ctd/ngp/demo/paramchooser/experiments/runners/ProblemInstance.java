package ru.ifmo.ctd.ngp.demo.paramchooser.experiments.runners;

import org.uncommons.watchmaker.framework.EvolutionEngine;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.watchmaker.SplittedParameter;

import java.util.List;

/**
 * @author Arkadii Rost
 */
public class ProblemInstance {
	private final SplittedParameter[] params;
	private final EvolutionEngine<List<Double>> es;

	public ProblemInstance(SplittedParameter[] params, EvolutionEngine<List<Double>> es) {
		this.params = params;
		this.es = es;
	}

	public SplittedParameter[] getParams() {
		return params;
	}

	public EvolutionEngine<List<Double>> getEs() {
		return es;
	}
}
