package ru.ifmo.ctd.ngp.demo.ffchooser.genetic;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

import ru.ifmo.ctd.ngp.demo.ffchooser.OptimizationAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.Utils;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * Some tests for the {@link EvolutionaryAlgImpl}.
 * The tests mainly focus on its {@link OptimizationAlgorithm}-like 
 * behavior.
 * @author Arina Buzdalova
 *
 */
public class GATests {

	/**
	 * Tests {@link ru.ifmo.ctd.ngp.demo.ffchooser.Utils#makeStableGA(List)} with {@link #testSampleGA}
	 */
	@Test
	public void testStableGA() {
		List<FitnessEvaluator<? super BitString>> evaluators = new ArrayList<>();
		testSampleGA(Utils.makeStableGA(evaluators), evaluators);
	}
	
	/**
	 * Tests {@link ru.ifmo.ctd.ngp.demo.ffchooser.Utils#makeUnstableGA(List)} with {@link #testSampleGA}
	 */
	@Test
	public void testUnstableGA() {
		List<FitnessEvaluator<? super BitString>> evaluators = new ArrayList<>();
		testSampleGA(Utils.makeUnstableGA(evaluators), evaluators);
	}
	
	/**
     * Tests progress and correctness of statistics returned by the sample genetic algorithm solving the xVector problem
     * in the situation of changing optimization criteria. The criteria is changed manually.
     *
     * @param ga the genetic algorithm to be tested
     * @param evaluators list of fitness functions used in {@code ga}
     */
	private void testSampleGA(EvolutionaryAlgImpl<? extends BitString> ga, List<FitnessEvaluator<? super BitString>> evaluators) {
		int steps = 20;		
		//ga.setPrinter(new ConsoleGAPrinter());
		for (int i = 0, len = evaluators.size(); i < len; i++) {
			ga.refresh();
			ga.changeCriterion(i);
			Assert.assertEquals(i, ga.getCurrentCriterion());
			checkProgressAndStatistics(ga, evaluators, steps);
		}	
	}
	/**
	 * Checks whether the specified GA progresses with time basing on the current fitness evaluator
	 * and whether the statistics returned by the GA is in accord with the list of the evaluators
	 * @param ga the specified GA
	 * @param evaluators the specified list of evaluators
	 * @param steps number of times the {@link EvolutionaryAlgImpl#computeValues()} method is called
	 */
	private void checkProgressAndStatistics(
			EvolutionaryAlgImpl<? extends BitString> ga,
			List<FitnessEvaluator<? super BitString>> evaluators,
			int steps) {
		
		double oldFitness = 0;
		for (int i = 0; i < steps; i++) {
			List<Double> values = ga.computeValues();
			BitString best = ga.getBestIndividual();
			/*
			 * Checks whether the GA progresses with time
			 */
			Assert.assertTrue(oldFitness <= evaluators.get(ga.getCurrentCriterion()).getFitness(best, null));
		
			/*
			 * Checks whether the GA returns statistics according to the list of evaluators
			 */
			int len = evaluators.size();
			for (int j = 0; j < len; j++) {
				if (j == 3) {
					/*
					 * The third evaluator is "x + random"
					 */
					Assert.assertTrue(Math.abs(evaluators.get(len - 1).getFitness(best, null) - values.get(len - 1)) < 1);
				} else {
					Assert.assertEquals(evaluators.get(j).getFitness(best, null), values.get(j), 1e-9);
				}
			}
		}
	}
}
