package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp;

import org.uncommons.watchmaker.framework.FitnessEvaluator;
import ru.ifmo.ctd.ngp.demo.ffchooser.jobshop.JobShopUtils;

import java.util.List;

/**
 * Fitness evaluator for the Job Shop Scheduling Problem.
 * It evaluates the flow time of several jobs.
 * @author Irene Petrova
 */
public class MultiFlowTimeFitness implements FitnessEvaluator<List<Integer>> {
    private final int[][] times;
    private final int[][] machines;
    private final int max;
    private final int[] job;

    /**
     * Constructs {@link MultiFlowTimeFitness} with the specified parameters.
     *
     * @param job jobs whose flow time will be evaluated as fitness
     * @param max the maximal flow time for each job
     * @param times the processing times for each operation
     * @param machines the machines corresponding to each operation
     */
    public MultiFlowTimeFitness(int[] job, int max, int[][] times, int[][] machines) {
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
        double sum = 0;
        int[] res = JobShopUtils.evalFlowTimes(individual, JobShopUtils.createJobsList(times, machines));
        for (int j : job) {
            sum += res[j];
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