package ru.ifmo.ctd.ngp.demo.paramchooser.experiments.rastrigin;

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
public class RastriginProblem implements RunnerProblem {
	private static final double ALPHA = 10;
	private final int lambda;
	private final int range;
	private final int dim;

	public RastriginProblem(int lambda, int range, int dim) {
		this.lambda = lambda;
		this.range = range;
		this.dim = dim;
	}

	public static void main(String[] args) throws IOException {
		int[] ranges = {1, 2, 3};
		int[] mus = {1, 5, 10};
		int[] lambdas = {1, 3, 7};

		int times = 20;
		for (int dim = 1; dim < 3; dim ++) {
			for (int range : ranges) {
				for (int mu : mus) {
					for (int lambda : lambdas) {
						RastriginProblem rastriginProblem = new RastriginProblem(lambda, range, dim);
						Path problemDir = Paths.get("results/rastrigin_" + rastriginProblem.getDim()
							  + String.format("/%d_%d", mu, rastriginProblem.getLambda()));
						{
							System.out.println("Dist");
							DistBaseRunner dbRunner = new DistBaseRunner(mu, RunConfig.DEFAULT);
							Path dir = problemDir.resolve("dist_base_" + rastriginProblem.getProblemRange());
							Files.createDirectories(dir);
							dbRunner.runMany(dir, rastriginProblem, times);
						}

						{
							System.out.println("Simple");
							SimpleRunner simpleRunner = new SimpleRunner(mu, RunConfig.DEFAULT);
							Path dir = problemDir.resolve("simple_" + rastriginProblem.getProblemRange());
							Files.createDirectories(dir);
							simpleRunner.runMany(dir, rastriginProblem, times);
						}

						{
							System.out.println("Simple single");
							SimpleSingleRunner simpleRunner = new SimpleSingleRunner(mu, RunConfig.DEFAULT);
							Path dir = problemDir.resolve("simple_single_" + rastriginProblem.getProblemRange());
							Files.createDirectories(dir);
							simpleRunner.runMany(dir, rastriginProblem, times);
						}
					}
				}
			}
		}

		times = 5;
		for (int dim = 1; dim < 3; dim ++) {
			for (int range : ranges) {
				for (int mu : mus) {
					for (int lambda : lambdas) {
						RastriginProblem rastriginProblem = new RastriginProblem(lambda, range, dim);
						Path problemDir = Paths.get("results/rastrigin_" + rastriginProblem.getDim()
							  + String.format("/%d_%d", mu, rastriginProblem.getLambda()));
						{
							System.out.println("Earpc");
							EarpcRunner earpcRunner = new EarpcRunner(mu, RunConfig.DEFAULT);
							Path dir = problemDir.resolve("earpc_" + rastriginProblem.getProblemRange());
							Files.createDirectories(dir);
							earpcRunner.runMany(dir, rastriginProblem, times);
						}

						{
							System.out.println("Earpc single");
							EarpcRunner earpcRunner = new EarpcRunner(mu, RunConfig.DEFAULT);
							Path dir = problemDir.resolve("earpc_single_" + rastriginProblem.getProblemRange());
							Files.createDirectories(dir);
							earpcRunner.runMany(dir, rastriginProblem, times);
						}
					}
				}
			}
		}
	}
	@Override
	public int getDim() {
		return dim;
	}

	@Override
	public double getLowerBound(int i) {
		return -5;
	}

	@Override
	public double getUpperBound(int i) {
		return 5;
	}

	@Override
	public double getFitness(List<Double> doubles, List<? extends List<Double>> list) {
		double res = ALPHA * doubles.size();
		for (double x : doubles)
			res += square(x) - ALPHA * Math.cos(2 * Math.PI * x);
		return res;
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
