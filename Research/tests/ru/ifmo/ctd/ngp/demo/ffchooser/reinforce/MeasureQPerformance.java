package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce;

import ru.ifmo.ctd.ngp.demo.ffchooser.StepsCondition;
import ru.ifmo.ctd.ngp.demo.ffchooser.Utils;
import ru.ifmo.ctd.ngp.demo.ffchooser.ValueCondition;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.EvolutionaryAlgImpl;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.ConsoleGAPrinter;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.FixedBestReward;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.GString;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.q.ClassicQAgent;
import ru.ifmo.ctd.ngp.learning.reinforce.q.EGreedyAgent;

/**
 * Measures percent of runs in which {@link ClassicQAgent} switches to the
 * most appropriate criteria (fitness function that equals with x)
 * in the first problem ("x-vector" problem).
 * 
 * @author Arina Buzdalova
 */
public class MeasureQPerformance {

	public static void main(String[] args) {
		int times = 100;
				
		EvolutionaryAlgImpl<? extends GString<Boolean>> ga = Utils.makeStableGA();
		ga.addPrinter(new ConsoleGAPrinter());
		
		int guessed = 0;
		int parCount = ga.parametersCount();
		int best = ga.getTargetParameter();		
		
		for (int i = 0; i < times; i++) {
			for (int current = 0; current < parCount; current++) {
				System.out.println("\nTesting Q-learning on the following functions:");
				System.out.println("#0: [x / 100], #1: x, #2: 65530, #3: x + 100 * random(0, 1), #4: 10^5 * sin(x / 7000)");
				System.out.println("Starting with function #" + current + "\n");
				
				ga.refresh(current);
				OptAlgEnvironment<String, Integer> env = new OAEnvironment(ga, new FixedBestReward(-10, -5, 10));
				Agent<String, Integer> agent = new EGreedyAgent<>(0.1, 1.5, 0.5, 0.8);
				env.setTargetCondition(new ValueCondition(65535), new StepsCondition(100));
				agent.learn(env);
				
				if (best == ga.getCurrentCriterion()) {
					guessed++;
				}
			}
		}
		double percent = ((double)guessed / (parCount*times)) * 100;
		System.out.println("\nSwitched to the best criteria in " + percent + "% of " + times*parCount +  " runs");
	}
}
