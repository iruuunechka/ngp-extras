package ru.ifmo.ctd.ngp.demo.ffchooser.jobshop;

import org.uncommons.maths.random.*;
import org.uncommons.maths.statistics.*;
import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.factories.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.jobshop.DataFileReader.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.*;
import ru.ifmo.ctd.ngp.learning.reinforce.*;

import java.io.*;
import java.util.*;

public class SimpleRunner {	
	public static void main(String[] args) throws IOException {		
		Properties p = new Properties();
        try (FileReader in = new FileReader("./misc/jobshop.properties")) {
            p.load(in);
        }
		DataFileReader reader = new DataFileReader(new File("./misc/jobshop.txt"));
		StringTokenizer sets = new StringTokenizer(p.getProperty("datasets.test"), ", ");
		while (sets.hasMoreTokens()) {
			String instance = sets.nextToken();
            int result = Integer.parseInt(sets.nextToken());
			System.out.println(instance);
			run(reader.get(instance), 20, result);
		}
	}
	 
	private static void run(InputDataSet inputData, int runs, int result) {
		final int evals = 200 * 10;
		final int population = 100;
		final int generations = evals / population;
		
		int[][] times = inputData.getTimes();
		int[][] machines = inputData.getMachines();
		int jobs = times.length;
		int max = jobs * JobShopUtils.sumTimes(times);

		DataSet dataSet = new DataSet();
		
		for (int i = 0; i < runs; i++) {
			List<FitnessEvaluator<List<Integer>>> evaluators = new ArrayList<>();
			for (int j = 0; j < jobs; j++) {
				evaluators.add(new SingleFlowTimeFitness(j, max, times, machines));
			}	
			
			int step = 10;
			double[] grid = new double[]{step, step};
			
			AbstractCandidateFactory<List<Integer>> factory = new JobShopFactory(jobs, times[0].length);
			FitnessEvaluator<List<Integer>> targetFitness = new FlowTimeFitness(max, times, machines);
			EvolutionaryOperator<List<Integer>> mutation = new PositionBasedMutation();
			
			EvolutionaryAlgorithm<List<Integer>> pesaWithHelpers = 
					new PesaIIWithHelpers<>(targetFitness, evaluators,
							mutation, new GeneralizedOrderCrossover(new Probability(0.8), jobs), 0.8, factory, 10, 100, grid, new Random());
			
			pesaWithHelpers.addPrinter(new CompactPrinter<>());
			
			double[] optima = new double[jobs + 1];
			int sum = 0;
			for (int j = 0; j < jobs; j++) {
				for (int k = 0; k < times[j].length; k++) {
					optima[j + 1] += times[j][k];
					sum += times[j][k];
				}
			}
			optima[0] = sum;
			for (int j = 0; j < jobs + 1; j++) {
				optima[j] = max - optima[j];
			}
			StateCalculator<String, Integer> monitorState = new OptimizationMonitorState(optima);
			
			OptAlgEnvironment<String, Integer> env = 
					new OAEnvironment(pesaWithHelpers, new SingleDiffReward(), monitorState);
			
			//Agent<String, Integer> agent = new EGreedyAgent<String, Integer>(0.25, 1.0, 0.6, 0.01);
			//Agent<String, Integer> agent = new RandomAgent<String, Integer>();
			Agent<String, Integer> agent = new RAgent<>(0.2, 0.3, 0.01);
			//Agent<String, Integer> agent = new DelayedAgent<String, Integer>(200, 0.1, 5, 0.001);
			
			env.setTargetCondition(new ValueCondition(max), new StepsCondition(generations));
			System.out.println("Steps: " + agent.learn(env));
			dataSet.addValue(max - env.getLastValues().get(0));
			for (double d : env.getLastValues()) {
				System.out.print((max - d) + " ");             
			}
			System.out.println();
		}

        double mean = dataSet.getArithmeticMean();
        double percent = (mean - result) / result * 100;
        System.out.println(String.format("percent: %f average: %f min: %f max: %f dev: %f",
                percent, dataSet.getArithmeticMean(), dataSet.getMinimum(), dataSet.getMaximum(), dataSet.getStandardDeviation()));
	}
}
