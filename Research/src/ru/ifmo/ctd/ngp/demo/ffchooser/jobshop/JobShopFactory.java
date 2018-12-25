package ru.ifmo.ctd.ngp.demo.ffchooser.jobshop;

import org.uncommons.watchmaker.framework.factories.*;

import java.util.*;

public class JobShopFactory extends AbstractCandidateFactory<List<Integer>> {
	private final int jobs;
	private final int machines;
	
	public JobShopFactory(int jobs, int machines) {
		this.jobs = jobs;
		this.machines = machines;
	}

	@Override
	public List<List<Integer>> generateInitialPopulation(int size, Random rng) {
		return JobShopUtils.generateRandomPopulation(size, jobs, machines, rng);
	}

	@Override
	public List<List<Integer>> generateInitialPopulation(int size,
			Collection<List<Integer>> population, Random rng) {
		List<List<Integer>> newPop = new ArrayList<>(population);
		newPop.addAll(JobShopUtils.generateRandomPopulation(size - newPop.size(), jobs, machines, rng));
		return newPop;
	}

	@Override
	public List<Integer> generateRandomCandidate(Random rng) {
		return JobShopUtils.generateRandomIndividual(jobs, machines, rng);
	}

}
