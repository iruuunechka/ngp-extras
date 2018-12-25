package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import ru.ifmo.ctd.ngp.demo.ffchooser.StepsCondition;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.EvolutionaryAlgImpl;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.StringGeneticAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.CompactPrinter;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.MultiDiffReward;
import ru.ifmo.ctd.ngp.demo.ffchooser.xvector.BitCountFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.xvector.ExpFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.xvector.IntFitness;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.GString;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.q.SoftmaxAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to see how Q-learning works
 * on the third problem, where the best fitness function
 * changes with time and x equals to the number of true-bits in the string.
 * 
 * @author Arina Buzdalova
 */
public class BitCountSolver {
	public static void main(String[] args) {
		double cross = 80.0;
		double k = 10000;
		List<FitnessEvaluator<? super BitString>> evaluators = new ArrayList<>();
		evaluators.add(new IntFitness(new BitCountFitness(1.0 / 5, 0)));
		//evaluators.add(new IntFitness(new PowerFitness(k * k * (1.0 / cross), 1.0 / 2, 0)));
		evaluators.add(new IntFitness(new ExpFitness(k, 2.0, 1.0 / cross, -k))); 
		
		int genSize = 100;
		int size = 100;
		
		EvolutionaryAlgImpl<? extends GString<Boolean>> ga = StringGeneticAlgorithm.newStringGA(size, 0, 0, evaluators);
		ga.setGenerationSize(genSize);
		ga.addPrinter(new CompactPrinter<GString<Boolean>>());
		//ga.setSeedPopulation(GeneticUtils.zeroPopulation(size, genSize));
		
		OAEnvironment env = new OAEnvironment(ga, new MultiDiffReward(3));
		//Agent<String, Integer> agent = new EGreedyAgent<String, Integer>("EGreedyTest.learn", 0.1, 0.1, 1.0, 0.8);	
		Agent<String, Integer> agent = new SoftmaxAgent<>(4.0, 1.01, 0.05, 0.8);
		env.setTargetCondition(new StepsCondition(500));
		agent.learn(env);
	}
}
