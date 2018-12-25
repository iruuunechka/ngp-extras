package ru.ifmo.ctd.ngp.demo.ffchooser.jobshop;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 * Supporting fitness evaluator for the Job Shop Scheduling Problem.
 * It evaluates the flow time of the specified job.
 * 
 * @author Arina Buzdalova
 */
public class SingleFlowTimeFitness implements FitnessEvaluator<List<Integer>> {
	private final int[][] times;
	private final int[][] machines;
	private final int max;
	private final int job;
	
	/**
	 * Constructs {@link SingleFlowTimeFitness} with the specified parameters.
	 * 
	 * @param job the job whose flow time will be evaluated as fitness
	 * @param max the maximal flow time of the {@code job}
	 * @param times the processing times for each operation
	 * @param machines the machines corresponding to each operation
	 */
	public SingleFlowTimeFitness(int job, int max, int[][] times, int[][] machines) {
		this.job = job;
		this.times = times;
		this.machines = machines;
		this.max = max;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFitness(List<Integer> individual, List<? extends List<Integer>> population) {
		return max - JobShopUtils.evalFlowTimes(individual, JobShopUtils.createJobsList(times, machines))[job];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNatural() {
		return true;
	}

}
