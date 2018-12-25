package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.reward;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MultiOptAlgEnvironment;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MulticriteriaOptimizationAlgorithm;

import java.util.List;

/**
 * @author Irene Petrova
 */
public class FitnessParetoDiffReward implements MultiRewardCalculator {

    private double fitnessByPareto(List<List<Double>> pareto) {
        double sum = 0;
        for (List<Double> ind : pareto) {
            sum += ind.get(0);
        }
        return sum / pareto.size();
    }
    @Override
    public double calculate(MultiOptAlgEnvironment<?, ?> environment) {
        MulticriteriaOptimizationAlgorithm alg = environment.getAlgorithm();
        double oldFitness = fitnessByPareto(alg.getCurrentParetoFront());
        double newFitness = fitnessByPareto(alg.computeParetoOfGeneration(alg.getCurrentCriterion()));
        return newFitness - oldFitness;
    }

    @Override
    public String getName() {
        return "FitnessParetoDiff";
    }
}
