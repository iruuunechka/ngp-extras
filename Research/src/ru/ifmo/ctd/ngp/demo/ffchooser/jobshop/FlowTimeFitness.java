package ru.ifmo.ctd.ngp.demo.ffchooser.jobshop;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 * Fitness evaluator for the Job Shop Scheduling Problem.
 * It evaluates the total flow time.
 * 
 * @author Arina Buzdalova
 */
public class FlowTimeFitness implements FitnessEvaluator<List<Integer>> {
	private final int[][] times;
	private final int[][] machines;
	private final int max;
	
	/**
	 * Constructs the {@link FlowTimeFitness} with the specified parameters.
	 * 
	 * @param max the maximal flow time
	 * @param times the processing times for each operation
	 * @param machines the machines corresponding to each operation
	 */
	public FlowTimeFitness(int max, int[][] times, int[][] machines) {
		this.times = times;
		this.machines = machines;
		this.max = max;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFitness(List<Integer> individual, List<? extends List<Integer>> population) {
		int sum = 0;
		int[] flowTimes = JobShopUtils.evalFlowTimes(individual, JobShopUtils.createJobsList(times, machines));
		for (int i = 0; i < times.length; i++) {
			sum += flowTimes[i];
		}
		return max - sum;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNatural() {
		return true;
	}

}
