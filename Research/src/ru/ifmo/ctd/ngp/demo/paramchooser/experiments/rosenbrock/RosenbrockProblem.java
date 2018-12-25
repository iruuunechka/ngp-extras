package ru.ifmo.ctd.ngp.demo.paramchooser.experiments.rosenbrock;

import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionStrategyEngine;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.termination.TargetFitness;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.watchmaker.SplittedParameter;
import ru.ifmo.ctd.ngp.demo.paramchooser.experiments.NumericCandidateFactory;
import ru.ifmo.ctd.ngp.demo.paramchooser.experiments.NumericMutation;
import ru.ifmo.ctd.ngp.demo.paramchooser.experiments.runners.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

/**
 * @author Arkadii Rost
 */
public class RosenbrockProblem implements RunnerProblem {
	private final int lambda;
	private final int range;

	public RosenbrockProblem(int lambda, int range) {
		this.lambda = lambda;
		this.range = range;
	}

	public static void main(String[] args) throws IOException {
		int[] ranges = {1, 2, 3};
		int[] mus = {1, 5, 10};
		int[] lambdas = {1, 3, 7};

		int times = 20;
		for (int range : ranges) {
			for (int mu : mus) {
				for (int lambda : lambdas) {
					RosenbrockProblem rosenbrockProblem = new RosenbrockProblem(lambda, range);
					Path problemDir = Paths.get("results/rosenbrock/"
						  + String.format("%d_%d", mu, rosenbrockProblem.getLambda()));
					{
						System.out.println("Dist");
						DistBaseRunner dbRunner = new DistBaseRunner(mu, RunConfig.DEFAULT);
						Path dir = problemDir.resolve("dist_base_" + rosenbrockProblem.getProblemRange());
						Files.createDirectories(dir);
						dbRunner.runMany(dir, rosenbrockProblem, times);
					}

					{
						System.out.println("Simple");
						SimpleRunner simpleRunner = new SimpleRunner(mu, RunConfig.DEFAULT);
						Path dir = problemDir.resolve("simple_" + rosenbrockProblem.getProblemRange());
						Files.createDirectories(dir);
						simpleRunner.runMany(dir, rosenbrockProblem, times);
					}

					{
						System.out.println("Simple single");
						SimpleSingleRunner simpleRunner = new SimpleSingleRunner(mu, RunConfig.DEFAULT);
						Path dir = problemDir.resolve("simple_single_" + rosenbrockProblem.getProblemRange());
						Files.createDirectories(dir);
						simpleRunner.runMany(dir, rosenbrockProblem, times);
					}
				}
			}
		}
		times = 5;
		for (int range : ranges) {
			for (int mu : mus) {
				for (int lambda : lambdas) {
					RosenbrockProblem rosenbrockProblem = new RosenbrockProblem(lambda, range);
					Path problemDir = Paths.get("results/rosenbrock/"
						  + String.format("%d_%d", mu, rosenbrockProblem.getLambda()));
					{
						System.out.println("Earpc");
						EarpcRunner earpcRunner = new EarpcRunner(mu, RunConfig.DEFAULT);
						Path dir = problemDir.resolve("earpc_" + rosenbrockProblem.getProblemRange());
						Files.createDirectories(dir);
						earpcRunner.runMany(dir, rosenbrockProblem, times);
					}

					{
						System.out.println("Earpc single");
						EarpcRunner earpcRunner = new EarpcRunner(mu, RunConfig.DEFAULT);
						Path dir = problemDir.resolve("earpc_single_" + rosenbrockProblem.getProblemRange());
						Files.createDirectories(dir);
						earpcRunner.runMany(dir, rosenbrockProblem, times);
					}
				}
			}
		}
	}
	@Override
	public int getDim() {
		return 2;
	}

	@Override
	public double getLowerBound(int i) {
		return -15;
	}

	@Override
	public double getUpperBound(int i) {
		return 10;
	}

	@Override
	public double getFitness(List<Double> doubles, List<? extends List<Double>> list) {
		double x = doubles.get(0);
		double y = doubles.get(1);
		return square(1 - square(x * x)) + 100 * square(y - square(x));
	}

	@Override
	public boolean isNatural() {
		return false;
	}

	private static double square(double x) {
		return x * x;
	}

	@Override
	public ProblemInstance getProblemInstance(Random rand) {
		int problemRange = getProblemRange();
		SplittedParameter sigmaParam = new SplittedParameter("sigma", 0.0, problemRange, 1, 5);
		EvolutionEngine<List<Double>> es = new EvolutionStrategyEngine<>(
			  new NumericCandidateFactory(this),
			  new NumericMutation(this, sigmaParam),
			  this,
			  true,
			  getLambda(),
			  rand
		);

//		es.addEvolutionObserver(populationData -> {
//			List<Double> best = populationData.getBestCandidate();
//			System.out.printf("%f %f\n", best.get(0), best.get(1));
//			System.out.println(populationData.getBestCandidateFitness());
//		});

		return new ProblemInstance(new SplittedParameter[]{sigmaParam}, es);
	}

	@Override
	public TerminationCondition getTerminationCondition() {
		return new TargetFitness(1e-5, false);
	}

	public int getProblemRange() {
		return range;
	}

	public int getLambda() {
		return lambda;
	}
}
