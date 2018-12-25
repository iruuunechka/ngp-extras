package ru.ifmo.ctd.ngp.demo.ffchooser.config.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.DelayedConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.GreedyConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.NoLearnConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.RewardCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.SingleDiffReward;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.SingleState;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.StateCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.evaluators.SwitchPointEvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.TextConstructor;

/**
 * Tests {@link ConfigGenerator} using old generation methods.
 * 
 * @author Arina Buzdalova
 */
public class ConfigGeneratorTest {
	private final static int genCount = 100;
	private final static int elite = 5;
	
	/**
	 * Tests {@link ConfigGenerator#generate(Properties)} by comparing it
	 * with the previously existed method
	 */
	@Test
	public void testGeneration() {		
		Properties collection = genCollectionProps();
		Collection<Configuration> oldColl = oldGenerate(collection);
		Collection<Configuration> newColl = ConfigGenerator.generate(collection);
			
		Assert.assertEquals(oldColl.size(), newColl.size());		
		for (Configuration c : oldColl) {
			Assert.assertTrue(newColl.contains(c));
		}		
	}
	
	private final static String evaluator = SwitchPointEvaluatorsFactory.class.getCanonicalName()
            + "(divisor=10,length=10,switchPoint=2)";
	private final static String state = SingleState.class.getCanonicalName() + "()";
	private final static String reward = SingleDiffReward.class.getCanonicalName() + "()";
	
	private Properties genCollectionProps() {
		Random r = new Random();
		Properties collection = new Properties();
		collection.setProperty("mode", "none greedy delayed");
		collection.setProperty("steps", String.format("%d", r.nextInt(100)));
		collection.setProperty("crossover", randomDoubles(r, 3));
		collection.setProperty("mutation", randomDoubles(r, 3));
		String lengths = randomIntegers(r, 3, 100);
		collection.setProperty("length", lengths);
		collection.setProperty("point", randomPoints(r, lengths, 3));
		collection.setProperty("divider", randomIntegers(r, 2, 10));
		collection.setProperty("epsilon", randomDoubles(r, 4));
		collection.setProperty("alpha", randomDoubles(r, 4));
		collection.setProperty("gamma", randomDoubles(r, 4));
		//Can be greater than length. It's OK for generator testing, but not realistic
		collection.setProperty("period", randomIntegers(r, 2, 100));
		collection.setProperty("bonus", randomDoubles(r, 4));
		collection.setProperty("factor", randomDoubles(r, 4));
		collection.setProperty("evaluators", evaluator);
		collection.setProperty("reward", reward);
		collection.setProperty("state", state);
		collection.setProperty("gensize", String.valueOf(genCount));
		collection.setProperty("elite", String.valueOf(elite));
		return collection;
	}

	private static Collection<Configuration> oldGenerate(Properties properties) {
		PropertiesArrayReader reader = new PropertiesArrayReader(properties);
		
		return oldGenerate(reader.getInt("steps"), reader.getDoubleValues("crossover"), 
				reader.getDoubleValues("mutation"), reader.getIntValues("length"), reader.getIntTwoDimensional("point"), 
				reader.getIntValues("divider"), reader.getDoubleValues("epsilon"), reader.getDoubleValues("alpha"), 
				reader.getDoubleValues("gamma"), reader.getIntValues("period"), reader.getDoubleValues("bonus"), 
				reader.getDoubleValues("factor"));
	}
	
	private String randomPoints(Random r, String lengths, int maxNum) {
		StringBuilder b = new StringBuilder();
		String[] lengthsArr = lengths.split(" ");
        for (String length : lengthsArr) {
            int num = r.nextInt(maxNum) + 1;
            int len = Integer.parseInt(length);
            for (int j = 0; j < num; j++) {
                b.append(r.nextInt(len));
                b.append(" ");
            }
            b.setCharAt(b.length() - 1, ';');
            b.append(" ");
        }
		b.deleteCharAt(b.length() - 1);
		b.deleteCharAt(b.length() - 1);
		return b.toString();		
	}
	
	private String randomIntegers(Random r, int maxNum, int maxValue) {
		int len = r.nextInt(maxNum) + 1;
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < len; i++) {
			b.append(r.nextInt(maxValue) + 1);	
			b.append(" ");
		}
		b.deleteCharAt(b.length() - 1);
		return b.toString();
	}
	
	private String randomDoubles(Random r, int maxNum) {
		int len = r.nextInt(maxNum) + 1;
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < len; i++) {
			b.append(r.nextDouble());
			b.append(" ");
		}
		b.deleteCharAt(b.length() - 1);
		return b.toString();
	}
	
	private static Collection<Configuration> oldGenerate(int stepsLimit,
														 double[] crossovers, double[] mutations, int[] lengths,
														 int[][] points, int[] dividers, double[] epsilons,
														 double[] alphas, double[] gammas,
														 int[] periods, double[] bonuses, double[] discounts) {
		
		EvaluatorsFactory e = TextConstructor.constructFromString(EvaluatorsFactory.class, evaluator);
		RewardCalculator r = TextConstructor.constructFromString(RewardCalculator.class, reward);
        //noinspection unchecked
        StateCalculator<String, Integer> s = TextConstructor.constructFromString(StateCalculator.class, state);
		
		List<Configuration> configs = new ArrayList<>();
		
    	for (double crossover : crossovers) {
	    	for (double mutation : mutations) {
	    		
	    		for (int lenIndex = 0; lenIndex < lengths.length; lenIndex++) {
	    			int length = lengths[lenIndex];
	    				    			
	    			for (int divider : dividers) {    				
	    				
	    				configs.add(new NoLearnConfiguration(stepsLimit, crossover, mutation, 
	    						length, divider, genCount, elite, null));
	    				
	    				for (int switchPoint : points[lenIndex]) {
		    				for(double epsilon : epsilons) {
		    					for (double alpha : alphas) {
		    						for (double gamma : gammas) {
		    							configs.add(new GreedyConfiguration(stepsLimit, crossover, mutation, 
		    									length, switchPoint, divider, epsilon, alpha, gamma, e, r, s,
		    									genCount, elite));
		    						}
		    					}
		    				}
		    				
		    				for (int period : periods) {
		    					for (double bonus : bonuses) {
		    						for (double discount : discounts) {
		    							configs.add(new DelayedConfiguration(stepsLimit, crossover, mutation, 
		    									length, switchPoint, divider, period, bonus, discount, e, r, s,
		    									genCount, elite));
		    						}
		    					}
		    				}
	    				}	    				
	    			}
	    		}
	    	}
    	}    	
		return configs;
	}
}