package ru.ifmo.ctd.ngp.demo.ffchooser.jobshop;

import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.operators.AbstractCrossover;

import ru.ifmo.ctd.ngp.util.CollectionsEx;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * The Generalized Order Crossover (GOX) operator for the Job Shop Problem. 
 * It selects a substring in one parent, 
 * deletes the operations corresponding to the substring in the other parent, 
 * and inserts the substring at the position of the first deleted operation.
 * 
 * @author Arina Buzdalova
 */
public class GeneralizedOrderCrossover extends AbstractCrossover<List<Integer>> {
	private final Probability crossoverProbability;
	private final int jobs;
	
	/**
	 * Constructs the {@link GeneralizedOrderCrossover} operator with the 
	 * specified parameters.
	 * 
	 * @param crossoverProbability the probability of performing crossover
	 * @param jobs the number of jobs in the Job Shop Problem
	 */
	public GeneralizedOrderCrossover(Probability crossoverProbability, int jobs) {
		super(1, crossoverProbability);
		this.crossoverProbability = crossoverProbability;
		this.jobs = jobs;
	}

	@Override
	protected List<List<Integer>> mate(List<Integer> parent1, List<Integer> parent2,
			int points, Random rng) {		
		if (crossoverProbability.nextEvent(rng)) {
			return CollectionsEx.listOf(cross(parent1, parent2, rng), cross(parent2, parent1, rng));
		} else {
			return CollectionsEx.listOf(parent1, parent2);
		}
	}
	
	protected List<Integer> cross(List<Integer> parent1, List<Integer> parent2, Random rng) {
		int len = parent1.size();
		int subLen = len / 3 + rng.nextInt(len / 6);
		int pos = rng.nextInt(len - subLen);
				
		int[] num = new int[jobs];
		Arrays.fill(num, 0);
		@SuppressWarnings("unchecked")
        ArrayDeque<Integer>[] del = new ArrayDeque[jobs];
		List<Integer> subList = new ArrayList<>();
		
		for (int i = 0; i < len; i++) {
			int job = parent1.get(i);
			if (i >= pos || i < pos + subLen) {
				subList.add(job);
				if (del[job] == null) {
					del[job] = new ArrayDeque<>();
				}
				del[job].addLast(num[job]);
			}			
			num[job]++;
		}
		
		Arrays.fill(num, 0);
		List<Integer> child = new ArrayList<>();
		boolean inserted = false;
		
		for (int i = 0; i < len; i++) {
			int job = parent2.get(i);
			int toDelete = del[job].getFirst();
			
			if (toDelete == num[job]) {
				del[job].removeFirst();
				if (!inserted) {
					child.addAll(subList);
					inserted = true;
				}
			} else {
				child.add(job);
			}
			num[job]++;
		}		
		return child;
	}
}
