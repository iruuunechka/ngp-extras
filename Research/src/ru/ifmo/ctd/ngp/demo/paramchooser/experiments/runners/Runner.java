package ru.ifmo.ctd.ngp.demo.paramchooser.experiments.runners;

import org.uncommons.watchmaker.framework.EvolutionEngine;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Action;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Agent;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Parameter;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.SimpleState;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.watchmaker.SplittedParameter;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.watchmaker.WatchmakerController;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.watchmaker.WatchmakerEnvironment;
import ru.ifmo.ctd.ngp.util.FastRandom;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

/**
 * @author Arkadii Rost
 */
public abstract class Runner<A extends Action, L> {
	private final int mu;
	protected final RunConfig runConfig;

	protected Runner(int mu, RunConfig runConfig) {
		this.mu = mu;
		this.runConfig = runConfig;
	}

	public void runMany(Path directory, RunnerProblem runnerProblem, int times) {
		Random random = FastRandom.threadLocal();
		for (int i = 0; i < times; i++) {
			System.out.println("Run " + i);
			ProblemInstance pi = runnerProblem.getProblemInstance(random);
			SplittedParameter[] params = pi.getParams();
			Agent<A, SimpleState, L> agent = createAgent(params, runConfig);
			EvolutionEngine<List<Double>> es = pi.getEs();
			es.addEvolutionObserver(createController(random, agent, params, runnerProblem.isNatural()));
			es.evolve(getMu(), 0, runnerProblem.getTerminationCondition());

			try (PrintWriter printer = new PrintWriter(directory.resolve("run" + i).toFile())) {
				int ei = 0;
				for (L entry : agent.getLog())
					printer.println(String.format("%d\t%s", ei++, entry.toString()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected abstract Agent<A, SimpleState, L> createAgent(SplittedParameter[] params, RunConfig runConfig);

	protected WatchmakerController<List<Double>, A, L> createController(Random rand, Agent<A, SimpleState, L> agent,
        Parameter[] parameters, boolean isNatural)
	{
		WatchmakerEnvironment<List<Double>, A> environment = new WatchmakerEnvironment<>(parameters, 100, isNatural);
		return new WatchmakerController<>(rand, environment, agent);
	}

	public int getMu() {
		return mu;
	}
}
