package ru.ifmo.ctd.ngp.demo.paramchooser.experiments.sphere;

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
public class SphereProblem implements RunnerProblem {

	private final int lambda;
	private final int range;

	public SphereProblem(int lambda, int range) {
		this.lambda = lambda;
		this.range = range;
	}

	public static void main(String[] args) throws IOException {
		int[] ranges = {7};
		int[] mus = {1, 5, 10};
		int[] lambdas = {1, 3, 7};

		int times = 20;
		for (int range : ranges) {
			for (int mu : mus) {
				for (int lambda : lambdas) {
					SphereProblem sphereProblem = new SphereProblem(lambda, range);
					Path problemDir = Paths.get("results/sphere/"
						  + String.format("%d_%d", mu, sphereProblem.getLambda()));
					{
						System.out.println("Dist");
						DistBaseRunner dbRunner = new DistBaseRunner(mu, RunConfig.DEFAULT);
						Path dir = problemDir.resolve("dist_base_" + sphereProblem.getProblemRange());
						Files.createDirectories(dir);
						dbRunner.runMany(dir, sphereProblem, times);
					}

					{
						System.out.println("Simple");
						SimpleRunner simpleRunner = new SimpleRunner(mu, RunConfig.DEFAULT);
						Path dir = problemDir.resolve("simple_" + sphereProblem.getProblemRange());
						Files.createDirectories(dir);
						simpleRunner.runMany(dir, sphereProblem, times);
					}

					{
						System.out.println("Simple single");
						SimpleSingleRunner simpleRunner = new SimpleSingleRunner(mu, RunConfig.DEFAULT);
						Path dir = problemDir.resolve("simple_single_" + sphereProblem.getProblemRange());
						Files.createDirectories(dir);
						simpleRunner.runMany(dir, sphereProblem, times);
					}
				}
			}
		}
		times = 5;
		for (int range : ranges) {
			for (int mu : mus) {
				for (int lambda : lambdas) {
					SphereProblem sphereProblem = new SphereProblem(lambda, range);
					Path problemDir = Paths.get("results/sphere/"
						  + String.format("%d_%d", mu, sphereProblem.getLambda()));
					{
						System.out.println("Earpc");
						EarpcRunner earpcRunner = new EarpcRunner(mu, RunConfig.DEFAULT);
						Path dir = problemDir.resolve("earpc_" + sphereProblem.getProblemRange());
						Files.createDirectories(dir);
						earpcRunner.runMany(dir, sphereProblem, times);
					}

					{
						System.out.println("Earpc single");
						EarpcRunner earpcRunner = new EarpcRunner(mu, RunConfig.DEFAULT);
						Path dir = problemDir.resolve("earpc_single_" + sphereProblem.getProblemRange());
						Files.createDirectories(dir);
						earpcRunner.runMany(dir, sphereProblem, times);
					}
				}
			}
		}
	}

	@Override
    public double getFitness(List<Double> individual, List<? extends List<Double>> list) {
        return Math.sqrt(individual.stream().mapToDouble(x -> x * x).sum());
    }

    @Override
    public boolean isNatural() {
        return false;
    }

    @Override
    public int getDim() {
        return 1;
    }

    @Override
    public double getLowerBound(int i) {
        return -10;
    }

    @Override
    public double getUpperBound(int i) {
        return 15;
    }

	public int getProblemRange() {
		return range;
	}

	@Override
	public ProblemInstance getProblemInstance(Random rand) {
		int problemRange = getProblemRange();
		SplittedParameter sigmaParam = new SplittedParameter("sigma", 0.0, problemRange, problemRange * rand.nextDouble(), 5);
		EvolutionEngine<List<Double>> es = new EvolutionStrategyEngine<>(
			  new NumericCandidateFactory(this),
			  new NumericMutation(this, sigmaParam),
			  this,
			  true,
			  getLambda(),
			  rand
		);

		return new ProblemInstance(new SplittedParameter[]{sigmaParam}, es);
	}

	@Override
	public TerminationCondition getTerminationCondition() {
		return new TargetFitness(1e-5, false);
	}

	public int getLambda() {
		return lambda;
	}
}
