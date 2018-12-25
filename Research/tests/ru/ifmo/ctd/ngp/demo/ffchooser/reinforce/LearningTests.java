package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

import junit.framework.Assert;
import ru.ifmo.ctd.ngp.demo.ffchooser.OptimizationAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.StepsCondition;
import ru.ifmo.ctd.ngp.demo.ffchooser.TargetCondition;
import ru.ifmo.ctd.ngp.demo.ffchooser.ValueCondition;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.EvolutionaryAlgImpl;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.StringGeneticAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.ComplexFixedReward;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.MultiDiffReward;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.RewardCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalGeneticAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.IntegerFunctionImpl;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.InvertedFunctionalFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f.X;
import ru.ifmo.ctd.ngp.demo.ffchooser.utils.GeneticUtils;
import ru.ifmo.ctd.ngp.demo.ffchooser.xvector.BitCountFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.xvector.ExpFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.xvector.IntFitness;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.DynaAgent;
import ru.ifmo.ctd.ngp.learning.reinforce.RAgent;
import ru.ifmo.ctd.ngp.learning.reinforce.q.EGreedyAgent;

/**
 * Some tests that checks that once adjusted 
 * reinforcement learning algorithms still working.
 * 
 * @author Arina Buzdalova
 */
public class LearningTests {
	
	/**
	 * Creates environment for basic tests. 
	 * Fitness functions are {@code x} and {@code -x}.
	 * @return the generated environment
	 */
	private OAEnvironment getTestEnvironment() {
		int length = 5;
		RewardCalculator reward = new ComplexFixedReward(0, 0, 1, 0);
		
		List<FunctionalFitness> evaluators = new ArrayList<>();
		FunctionalFitness x = new FunctionalFitness(new IntegerFunctionImpl(new X(), 1.0, 0, length));
		FunctionalFitness bad = new InvertedFunctionalFitness(x, length);
		evaluators.add(x);
		evaluators.add(bad);
		
		FunctionalGeneticAlgorithm alg =
				FunctionalGeneticAlgorithm.newFGA(length, 0, 0, evaluators, 0.7, 0.03);
//		alg.addPrinter(new CompactPrinter<BitString>());
		
		int size = 100;
		alg.setGenerationSize(size);
		alg.setStartPopulation(GeneticUtils.zeroPopulation(length, size));
		alg.setEliteCount(5);
		
		OAEnvironment env = new OAEnvironment(alg, reward);
		env.setTargetCondition(new ValueCondition(length));
		
		return env;
	}
	
	/**
	 * Tests {@link RAgent}. Agent should choose between {@code x} and {@code -x},
	 * where {@code x} is the target function.
	 */
	@Test
	public void testR() {
		Agent<String,Integer> agent = new RAgent<>(0.5, 0.5, 0.3);
		int steps = agent.learn(getTestEnvironment());
		Assert.assertTrue(steps < 50);
	}
	
	/**
	 * Tests {@link DynaAgent}. Agent should choose between {@code x} and {@code -x},
	 * where {@code x} is the target function.
	 */
    @Test(timeout = 2000)
	public void testDyna() {
		Agent<String,Integer> agent = new DynaAgent<>(0.3, 0.1, 10);
		int steps = agent.learn(getTestEnvironment());
		Assert.assertTrue(steps < 50);
	}
	
	/**
	 * Tests Q-learning.
	 * Checks whether switching from [x / k] to exponential function works.
	 */
	@Test
	public void XdivKvsExp() {
		int optimalSteps = 500;
		
		int divider = 10;
		int length = 50;
		
		double k = 10000;
		double cross = 80;
		
		double epsilon = 0.3;
		
		List<FitnessEvaluator<? super BitString>> evaluators = new ArrayList<>();
		evaluators.add(new IntFitness(new BitCountFitness(1.0 / divider, 0)));		
		evaluators.add(new IntFitness(new ExpFitness(k, 2.0, 1.0 / cross, -k)));
		
		EvolutionaryAlgImpl<BitString> ga = StringGeneticAlgorithm.newStringGA(length, 0, 0, evaluators);
		Agent<String, Integer> agent = new EGreedyAgent<>(epsilon, 1.0, 0.6, 0.01);

		OptAlgEnvironment<String, Integer> env = new OAEnvironment(ga, new MultiDiffReward(1));
		//noinspection IntegerDivisionInFloatingPointContext: length % divider === 0
		TargetCondition<OptimizationAlgorithm> reachedBest = new ValueCondition(length / divider);
		//noinspection unchecked (could not use @SafeVarargs)
		env.setTargetCondition(reachedBest, new StepsCondition(optimalSteps));
		
		agent.learn(env);
		
		Assert.assertTrue(env.checkTargetCondition(reachedBest));		
	}

}
