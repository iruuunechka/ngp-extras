package ru.ifmo.ctd.ngp.demo.ffchooser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.DelayedConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.GreedyConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.NoLearnConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.EvolutionaryAlgImpl;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.StringGeneticAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.ConsoleGAPrinter;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.evaluators.SwitchPointEvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.xvector.*;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * Set of some methods useful for testing.
 * 
 * @author Arina Buzdalova
 */
public class Utils {
		
	/**
	 * Creates sample configuration with no learning
	 * 
	 * @param stepsLimit the maximum number of steps made by GA
	 * @return sample configuration with no learning
	 */
	public static NoLearnConfiguration makeNoLearnConfig(int stepsLimit) {
		double crossover = 0.7;
		double mutation = 0.003;
		
		int length = 600;
		int divider = 10;
		
		return new NoLearnConfiguration(
				stepsLimit, 
				crossover, mutation, 
				length, divider, 100, 5,
				new SwitchPointEvaluatorsFactory(divider, length, 400));
	}
	
	/**
	 * Creates sample configuration for the delayed Q-Learning algorithm
	 * 
	 * @param stepsLimit the maximum number of steps made by GA
	 * @return sample configuration for the delayed Q-Learning algorithm
	 */
	public static DelayedConfiguration makeDelayedConfig(int stepsLimit) {		
		NoLearnConfiguration general = makeNoLearnConfig(stepsLimit);
		
		double crossover = general.getCrossover();
		double mutation = general.getMutation();
		
		int length = general.getLength();
		int switchPoint = 400;
		int divider = general.getDivider();
		
		double period = 100;
		double bonus = 0.001;
		double discount = 0.001;
		
		return new DelayedConfiguration(
				stepsLimit,
				crossover, mutation, 
				length, switchPoint, divider,
				period, bonus, discount, 100, 5);
	}
	
	/**
	 * Creates sample configuration for the epsilon-greedy Q-Learning algorithm
	 * 
	 * @param stepsLimit the maximum number of steps made by GA
	 * @return sample configuration for the delayed Q-Learning algorithm
	 */
	public static GreedyConfiguration makeGreedyConfig(int stepsLimit) {		
		NoLearnConfiguration general = makeNoLearnConfig(stepsLimit);
		
		double crossover = general.getCrossover();
		double mutation = general.getMutation();
		
		int length = general.getLength();
		int switchPoint = 400;
		int divider = general.getDivider();
		
		double epsilon = 0.2; 
		double alpha = 0.2;
		double gamma = 0.1;
		
		return new GreedyConfiguration(
				stepsLimit, 
				crossover, mutation, 
				length, switchPoint, divider,
				epsilon, alpha, gamma, 100, 5);
	}
	
	/**
     * Counts number of steps used by {@link EvolutionaryAlgImpl}
     * to evolve individual with the specified ideal fitness
     *
     * @param alg the specified evolutionary algorithm
     * @param maxFitness the ideal fitness
     * @param maxSteps steps limit
     * @return number of steps taken to evolve individual with {@code maxFiness},
     * 			or {@code -1} if such individual wasn't evolved after {@code maxSteps} iterations
     */
	public static int countSteps(EvolutionaryAlgImpl<BitString> alg, double maxFitness, int maxSteps) {
		int steps = 0;
		
		while (steps < maxSteps) {
			steps++;
			double fitness = alg.iterateEvolution().getFitness();
			System.out.println(steps + ") " + fitness);
			if (fitness >= maxFitness) {
				System.out.println();
				return steps;
			}
		}
		return -1;
	}
	
	/**
     * <p>
     * Creates {@link EvolutionaryAlgImpl} with predefined set of evaluators and
     * fills the specified container with this set.
     * </p><p>
     * Emulates "unstable" genetic algorithm:
     * applicability of fitness functions changes during the algorithm's execution.
     * </p><p>
     * Fitness functions ({@code cross} is the cross point of the #1 and #2 graphs):
     * <pre>
     * #0: [x / 100], #1: [sqrt(cross * x)], #2: [cross * (2 ^ (x / cross) - 1)]
     * </pre>
     * </p><p>
     * Target criteria is #0.
     * Current criteria is set to #0.
     * Best criteria changes from #1 to #2.
     * </p>
     * @param evaluators the specified container
     * @return "unstable" genetic algorithm
     */
	public static EvolutionaryAlgImpl<BitString> makeUnstableGA(List<FitnessEvaluator<? super BitString>> evaluators) {
		double cross = 65000.0;
		evaluators.clear();
		evaluators.add(new IntFitness(new XFitness(1.0 / 100, 0)));
		evaluators.add(new IntFitness(new PowerFitness(cross, 0.5, 0)));
		evaluators.add(new IntFitness(new ExpFitness(cross, 2.0, 1.0 / cross, -cross)));
		
		EvolutionaryAlgImpl<BitString> ga = StringGeneticAlgorithm.newStringGA(16, 0, 0, evaluators);
		ga.setGenerationSize(6);
		return ga;
	}
	
	/**
	 * Prints to the console the results of "unstable" {@link EvolutionaryAlgImpl} running.
	 * The genetic algorithm is got from {@link #makeUnstableGA} method.
	 * @param args are not used
	 */
	public static void main(String[] args) {
		int steps = 100;
		EvolutionaryAlgImpl<BitString> ga = makeUnstableGA(new ArrayList<>());
		ga.addPrinter(new ConsoleGAPrinter());
		for (int i = 0; i < steps; i++) {
			ga.computeValues();
		}
	}
	
	/**
	 * <p>
	 * Creates {@link EvolutionaryAlgImpl} with predefined set of evaluators and 
	 * fills the specified container with this set.
	 * </p><p>
	 * Emulates "stable" genetic algorithm: 
	 * the best suited fitness function is the same during the algorithm's execution.
	 * </p><p>
	 * Following fitness functions are used:
	 * <pre>
	 * #0: [x / 100], #1: x, #2: 65530, #3: x + 100 * random(0, 1), #4: 10^5 * sin(x / 7000)
	 * </pre>
	 * </p><p>
	 * Target criteria is #1.
	 * Current criteria is set to #0.
	 * Best criteria is #1.
	 * </p>
	 * @param evaluators the specified container
	 * @return genetic algorithm for solving simple xVector problem
	 */
	public static EvolutionaryAlgImpl<BitString> makeStableGA(List<FitnessEvaluator<? super BitString>> evaluators) {
		evaluators.clear();
		evaluators.add(new IntFitness(new XFitness(1.0/100, 0))); 
		evaluators.add(new XFitness(1, 0));
		evaluators.add(new ConstFitness(65530));
		evaluators.add(new XPlusRandFitness(1.0, 100.0, new Random()));
		evaluators.add(new SinFitness(100000, 1.0 / 7000, 0));

        return StringGeneticAlgorithm.newStringGA(16, 1, 0, evaluators);
	}
	
	/**
	 * Creates {@link EvolutionaryAlgImpl} with predefined set of evaluators.
	 * This set is the same as in {@link #makeStableGA(List)}.
	 * @return genetic algorithm for solving simple xVector problem
	 */
	public static EvolutionaryAlgImpl<BitString> makeStableGA() {
		return makeStableGA(new ArrayList<>());
	}
}
