package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint;

import java.util.ArrayList;
import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.CompactPrinter;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.CountPrinter;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OAEnvironment;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.ReinforcementCounter;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.ComplexFixedReward;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.RewardCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f.Root;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f.SecondPower;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f.X;
import ru.ifmo.ctd.ngp.demo.ffchooser.utils.GeneticUtils;
import ru.ifmo.ctd.ngp.demo.ffchooser.xvector.BitCountFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.xvector.IntFitness;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;
import ru.ifmo.ctd.ngp.learning.reinforce.q.*;

/**
 * Class for viewing learning on the genetic algorithm with 
 * three fitness functions, two of which are profitable
 * on different parts of the real axis separated 
 * by the specified switch point.
 * 
 * @author Arina Buzdalova
 */
public class ViewLearning {
	
	public static void main(String[] args) {
		runDelayed();
		//runGreedy();
	}

	private static void runDelayed() {
		/*
		 * Task parameters
		 */
		int stepsLimit = 4000;
		
		int length = 600;
		int divider = 10;
		int switchPoint = 400;
		
		/*
		 * Evolution parameters
		 */
		int generationSize = 100;
		int eliteCount = 5;
		double crossover = 0.7;
		double mutation = 0.1;
		
		//RewardCalculator reward = new MultiDiffReward(1);
		//RewardCalculator reward = new SingleDiffReward();
		//RewardCalculator reward = new FixedReward(0, 0.5, 1);
		RewardCalculator reward = new ComplexFixedReward(0, 0.5, 1, 0.5);
		
		System.out.println("length = " + length + "; divider = " + divider);
		System.out.println("switchPoint = " + switchPoint);
		
		FunctionalFitness xDivK = new FunctionalFitness(new IntegerFunctionImpl(new X(), 1.0 / divider, 0, length));
		
		List<FunctionalFitness> evaluators
			//= genSqrtAndPower(switchPoint, length);	
			= genPiecewise(switchPoint);
		
		evaluators.add(xDivK);
		
		FunctionalGeneticAlgorithm alg = FunctionalGeneticAlgorithm.newFGA(0, 2, 2, evaluators, crossover, mutation);
		
		alg.setGenerationSize(generationSize);
		alg.setStartPopulation(GeneticUtils.zeroPopulation(length, generationSize));
		alg.setEliteCount(eliteCount);
		
		DelayedParameters param = new DelayedParameters(2, 3);
		param.setDelta(0.001);
		param.setGamma(0.001);
		param.setEps(0.001);
		
		System.out.println("Update period: " + param.calcM() + " steps");
		System.out.println("Maximum updates per state-action pair: " + param.calcK());
		System.out.println("Sample complexity estimation: " + param.learningEstimate());
		
		DelayedAgent<String, Integer> agent = new DelayedAgent<>(stepsLimit, 0.001, 100, 0.001);
		
		System.out.println("m: " + agent.getM());
		System.out.println("eps: " + agent.getEps());
		System.out.println("gamma: " + agent.getGamma());

		OAEnvironment env = new OAEnvironment(alg, reward);
		
		ReinforcementCounter<BitString> counter = new ReinforcementCounter<>(stepsLimit, agent, alg, env);
		
		counter.setLength(length);
		//noinspection IntegerDivisionInFloatingPointContext: length % divider === 0
		counter.setEvaluator(new IntFitness(new BitCountFitness(1.0 / divider, 0)), length / divider);
		
		System.out.println("\n[0] upwards convex; [1] downwards convex; [2] x / " + divider + "\n");
		
		CountPrinter<BitString> countPrinter = new CountPrinter<>(new BitCountFitness(1.0, 0), evaluators.size());
		alg.addPrinter(countPrinter);
		
		DelayedConsolePrinter.addTo(agent);
		alg.addPrinter(new CompactPrinter<>());
		
		double zero = 0;
		double one = 0;
		double times = 1;
		int zeroDelta = 25;
		
		for (int i = 0; i < times; i++) {
			System.out.println("Run #" + i);			
			countPrinter.refresh();
			counter.countGenerations();	
			printStatistics(countPrinter, length, evaluators.size(), switchPoint, switchPoint);
			double[][] result = countPercents(countPrinter, switchPoint, length, zeroDelta);
			
			for (int k = 0; k < 2; k++) {
				for (int j = 0; j < evaluators.size(); j++) {
					System.out.print(result[k][j] + "% ");
				}
				System.out.println();
			}
			
			if (result[0][0] >= result[0][1] && result[0][0] >= result[0][2]) {
				zero++;
			}
			
			if (result[1][1] >= result[1][0] && result[1][1] >= result[1][2]) {
				one++;
			}
			
			System.out.println("Currently switched to #0 before switch point in " + zero + " runs");
			System.out.println("Currently switched to #1 after switch point in " + one + " runs");
		}
		
		System.out.println("Switched to #0 before switch point in " + (zero / times) * 100 + "% of runs");
		System.out.println("Switched to #1 after switch point in " + (one / times) * 100 + "% of runs");
		
	}
	
	public static void runGreedy() {
		/*
		 * Task parameters
		 */
		int stepsLimit = 3000;
		
		int length = 600;
		int divider = 10;
		int switchPoint = 430;
		
		/*
		 * Evolution parameters
		 */
		int generationSize = 100;
		int eliteCount = 5;
		double crossover = 0.7;
		double mutation = 0.1;
		
		/*
		 * Q-learning parameters
		 */
		double epsilon = 0.2; //0.4
		double alpha = 0.2;
		double gamma = 0.1;
		//RewardCalculator reward = new MultiDiffReward(1);
		//RewardCalculator reward = new SingleDiffReward();
		//RewardCalculator reward = new FixedReward(0, 0.5, 1);
		RewardCalculator reward = new ComplexFixedReward(0, 0.5, 1, 0.5);
		
		System.out.println("length = " + length + "; divider = " + divider);
		System.out.println("switchPoint = " + switchPoint);
		
		FunctionalFitness xDivK = new FunctionalFitness(new IntegerFunctionImpl(new X(), 1.0 / divider, 0, length));
		
		List<FunctionalFitness> evaluators
			//= genSqrtAndPower(switchPoint, length);	
			= genPiecewise(switchPoint);
		
		evaluators.add(xDivK);
		
		FunctionalGeneticAlgorithm alg = FunctionalGeneticAlgorithm.newFGA(0, 2, 2, evaluators, crossover, mutation);
		
		alg.setGenerationSize(generationSize);
		alg.setStartPopulation(GeneticUtils.zeroPopulation(length, generationSize));
		alg.setEliteCount(eliteCount);
		
		EGreedyAgent<String,Integer> agent = new EGreedyAgent<>(epsilon, 1.0, alpha, gamma);

		OAEnvironment env = new OAEnvironment(alg, reward);
		
		ReinforcementCounter<BitString> counter = new ReinforcementCounter<>(stepsLimit, agent, alg, env);
		
		counter.setLength(length);
		//noinspection IntegerDivisionInFloatingPointContext: length % divider === 0
		counter.setEvaluator(new IntFitness(new BitCountFitness(1.0 / divider, 0)), length / divider);
		
		System.out.println("\n[0] upwards convex; [1] downwards convex; [2] x / " + divider + "\n");
		
		CountPrinter<BitString> countPrinter = new CountPrinter<>(new BitCountFitness(1.0, 0), evaluators.size());
		alg.addPrinter(countPrinter);
		
		ClassicConsolePrinter.addTo(agent);
		alg.addPrinter(new CompactPrinter<>());
		
		double zero = 0;
		double one = 0;
		double times = 1;

        for (int i = 0; i < times; i++) {
			System.out.println("Run #" + i);			
			countPrinter.refresh();
			counter.countGenerations();	
			printStatistics(countPrinter, length, evaluators.size(), switchPoint, switchPoint);
			double[][] result = countPercents(countPrinter, switchPoint, length, switchPoint);
			
			for (int k = 0; k < 2; k++) {
				for (int j = 0; j < evaluators.size(); j++) {
					System.out.print(result[k][j] + "% ");
				}
				System.out.println();
			}
			
			if (result[0][0] >= result[0][1] && result[0][0] >= result[0][2]) {
				zero++;
			}
			
			if (result[1][1] >= result[1][0] && result[1][1] >= result[1][2]) {
				one++;
			}
			
			System.out.println("Currently switched to #0 before switch point in " + zero + " runs");
			System.out.println("Currently switched to #1 after switch point in " + one + " runs");
		}
		
		System.out.println("Switched to #0 before switch point in " + (zero / times) * 100 + "% of runs");
		System.out.println("Switched to #1 after switch point in " + (one / times) * 100 + "% of runs");
		
	}
	
	private static double[][] countPercents(
			CountPrinter<?> countPrinter,
			int switchPoint, 
			int length, 
			int delta) {
		int size = countPrinter.getEvaluatorsCount();
		
		double[] before = new double[size];
		double total = 0;
		
		for (int i = switchPoint - delta; i < switchPoint; i++) {
			for (int j = 0; j < size; j++) {
				int stat = countPrinter.getStatistics(i)[j];
				before[j] += stat;
				total += stat;				
			}
		}
		
		for (int i = 0; i < size; i++) {
			before[i] /= total;
			before[i] *= 100;
		}
		
		double[] after = new double[size];
		total = 0;
		
		for (int i = switchPoint; i <= length; i++) {
			for (int j = 0; j < size; j++) {
				int stat = countPrinter.getStatistics(i)[j];
				after[j] += stat;
				total += stat;				
			}
		}
		
		for (int i = 0; i < size; i++) {
			after[i] /= total;
			after[i] *= 100;
		}
		
		return new double[][] {before, after};
	}
	
	/**
     * Returns list of two integer fitness evaluators based on {@link SecondPower} and {@link SecondPower}
     * with the same specified approximate switch point.
     * @param switchPoint     the approximate value of the point
     * 						that separates the axis into parts where different fitness evaluators are profitable
     * @param length the length of individuals
     * @return list of two integer fitness evaluators based on {@link Root} and {@link SecondPower}
     * 			with the same {@code switchPoint}
     */
	public static List<FunctionalFitness> genSqrtAndPower(int switchPoint, int length) {
		List<FunctionalFitness> evaluators = new ArrayList<>();
		
		int lowerDomainBound = 0;
        int sqrtTolerance = 10;
		int powTolerance = 10;
		
		IntegerFunction intConvex = Functions.intDownConvex(lowerDomainBound, length);
		IntegerFunction intUpConvex = Functions.intUpConvex(lowerDomainBound, length);
		
		int upcSwitchPoint = intUpConvex.changeSwitchPoint(switchPoint, sqrtTolerance, 0, 100000);			
		int cSwitchPoint = intConvex.changeSwitchPoint(upcSwitchPoint, powTolerance, 0, 1000);
		
		System.out.println("\nupwards convex: " + intUpConvex + " switch point:" + upcSwitchPoint);
		System.out.println("downwards convex: " + intConvex + " switch point:" + cSwitchPoint + "\n");
		
		evaluators.add(new FunctionalFitness(intUpConvex));
		evaluators.add(new FunctionalFitness(intConvex));
		
		return evaluators;
	}
	
	/**
	 * Returns the list of two fitness evaluators based on piecewise functions.
	 * The first evaluator is profitable before the specified switch point,
	 * the second one is profitable after it.
	 * @param switchPoint the specified switch point
	 * @return list of fitness evaluators based on piecewise functions
	 */
	private static List<FunctionalFitness> genPiecewise(int switchPoint) {
		List<FunctionalFitness> evaluators = new ArrayList<>();
		
		evaluators.add(new FunctionalFitness(Functions.xConst(switchPoint)));
		evaluators.add(new FunctionalFitness(Functions.constX(switchPoint)));
		
		return evaluators;
	}
	
	private static void printStatistics(
			CountPrinter<?> countPrinter, 
			int length, 
			int evaluatorsCount, 
			int transitStart,
			int transitFinish) {
		
		String[] names = {"before", "transit", "after"};
		int segments = names.length;
		
		List<int[]> sums = new ArrayList<>();
        for (String ignored : names) {
            sums.add(new int[evaluatorsCount]);
        }
		
		System.out.println();
		for (int i = 0; i <= length; i++) {
			boolean empty = true;
			
			for (int times : countPrinter.getStatistics(i)) {
				if (times != 0) {
					empty = false;
				}
			}
			
			if (empty) {
				continue;
			}
			
			System.out.print(i + " ");
			
			int[] statistics = countPrinter.getStatistics(i);
			
			for (int j = 0; j < statistics.length; j++) {
				if (i < transitStart) {
					sums.get(0)[j] += statistics[j];
				}				
				if (i >= transitStart && i < transitFinish) {
					sums.get(1)[j] += statistics[j];
				}				
				if (i >= transitFinish) {
					sums.get(2)[j] += statistics[j];
				}	
				System.out.print(statistics[j] + " ");
			}
			System.out.println();
		}
		
		int[] total = new int[segments];
		
		System.out.println();
		
		for (int i = 0; i < evaluatorsCount; i++) {
			System.out.print("evaluator #" + i + " ");
			for (int j = 0; j < segments; j++) {
				System.out.print(names[j] + " " + sums.get(j)[i] + " ");
				total[j] += sums.get(j)[i];
			}
			System.out.println();
		}
		
		System.out.println();
		
		for (int i = 0; i < segments; i++) {
			System.out.print(names[i] + " ");
			for (int j = 0; j < evaluatorsCount; j++) {
				System.out.print("#" + j + " " + ((double) sums.get(i)[j] / total[i]) * 100 + "% ");
			}
			System.out.println();
		}
	}	
}
