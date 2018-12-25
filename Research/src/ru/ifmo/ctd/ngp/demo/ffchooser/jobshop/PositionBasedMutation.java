package ru.ifmo.ctd.ngp.demo.ffchooser.jobshop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Position Based Mutation (PBM) operator for the Job Shop Problem. 
 * It moves a randomly chosen element to a random position.
 * 
 * @author Arina Buzdalova
 */
public class PositionBasedMutation implements EvolutionaryOperator<List<Integer>> {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<List<Integer>> apply(List<List<Integer>> population, Random rng) {
		List<List<Integer>> mutated = new ArrayList<>();
		for (List<Integer> individual : population) {
			mutated.add(mutate(individual, rng));
		}
		return mutated;
	}
	
	private List<Integer> mutate(List<Integer> individual, Random rng) {
		int len = individual.size();
		int pos1 = rng.nextInt(len);
		int pos2 = rng.nextInt(len);
		
		List<Integer> mutated = new ArrayList<>(individual);
		Integer removed = mutated.remove(pos1);
		mutated.add(pos2, removed);
		
		return mutated;
	}	
}
