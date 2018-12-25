package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

import junit.framework.Assert;
import ru.ifmo.ctd.ngp.demo.ffchooser.Utils;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.EvolutionaryAlgImpl;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.MultiDiffReward;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.RewardCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.SingleDiffReward;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * Some tests for {@link OAEnvironment} and {@link RewardCalculator}.
 * 
 * @author Arina Buzdalova
 */
public class OAEnvironmentTests {
	
	/**
	 * Checks whether the specified state corresponds to the specified list of parameters' values.
	 * State should correspond to the parameters' order in the list, which is sorted by their values.
     *
     * TODO: documentation is inconsistent
     *
	 * @param state the specified state
     * @param prevValues the specified list of parameters' previous values
     * @param newValues the specified list of parameters' new values
	 */
	private void testState(String state, List<Double> prevValues, List<Double> newValues) {
		for (int j = 0, len = state.length(); j < len - 1; j++) {
			Assert.assertTrue((newValues.get(state.charAt(j) - '0') - prevValues.get(state.charAt(j) - '0')) / 
							prevValues.get(state.charAt(j) - '0')
					<= (newValues.get(state.charAt(j + 1) - '0') - prevValues.get(state.charAt(j + 1) - '0')) /
							prevValues.get(state.charAt(j + 1) - '0'));
		}
	}
	
	@Test
	public void applyAction() {
		int gaSteps = 5;
		List<FitnessEvaluator<? super BitString>> evaluators = new ArrayList<>();
		
		EvolutionaryAlgImpl<? extends BitString> alg = Utils.makeStableGA(evaluators);
		
		OAEnvironment env = new OAEnvironment(alg, new SingleDiffReward());		
					
		for (int action = 0, size = evaluators.size(); action < size; action++) {
			env.applyAction(action);
			Assert.assertEquals(action, alg.getCurrentCriterion());
			for (int j = 0; j < gaSteps; j++) {
				alg.computeValues();
			}
			Assert.assertEquals(action, alg.getCurrentCriterion());
		}
	}
	
	@Test
	public void checkRewardCalculator() {
		int times = 10;
		
		List<FitnessEvaluator<? super BitString>> evaluators = new ArrayList<>();
		
		EvolutionaryAlgImpl<? extends BitString> alg = Utils.makeStableGA(evaluators);
		alg.changeCriterion(2);
		
		RewardCalculator calc = new MultiDiffReward(1);
		OAEnvironment env = new OAEnvironment(alg, calc);
		int t = alg.getTargetParameter();
		List<Double> prevValues = alg.getCurrentBest();
		for (int i = 0; i < times; i++) {			
			double reward = calc.calculate(env);		
			Assert.assertEquals(alg.getCurrentBest().get(t) - prevValues.get(t), reward);
			prevValues = alg.getCurrentBest();
		}				
	}
	
	/**
	 * Tests {@link OAEnvironment} over genetic algorithm using {@link MultiDiffReward}.
	 */
	@Test
	public void testOverGA() {
		int times = 20;
	
		List<FitnessEvaluator<? super BitString>> evaluators = new ArrayList<>();
		
		EvolutionaryAlgImpl<? extends BitString> alg = Utils.makeStableGA(evaluators);
		
		OAEnvironment env = new OAEnvironment(alg, new MultiDiffReward(1));
		
		Random rand = new Random();
		int target = alg.getTargetParameter();
		
		for (int i = 0; i < times; i++) {
			int action = rand.nextInt(evaluators.size());
			
			List<Double> prevValues = alg.getCurrentBest();
			Double reward = env.applyAction(action);
			List<Double> newValues = alg.getCurrentBest();
			
			testState(env.getCurrentState(), prevValues, newValues);
			
			Assert.assertEquals(newValues.get(target) - prevValues.get(target), reward);
			Assert.assertEquals(alg.getCurrentCriterion(), action);
		}
	}
}
