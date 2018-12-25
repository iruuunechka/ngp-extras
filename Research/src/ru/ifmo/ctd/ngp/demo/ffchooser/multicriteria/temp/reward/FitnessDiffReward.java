package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.reward;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MultiOptAlgEnvironment;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MulticriteriaOptimizationAlgorithm;

/**
 * @author Irene Petrova
 */
public class FitnessDiffReward implements MultiRewardCalculator{
    @Override
    public double calculate(MultiOptAlgEnvironment<?, ?> environment) {
        MulticriteriaOptimizationAlgorithm alg = environment.getAlgorithm();
        double oldFitness = alg.getCurrentBest().get(0);
        double newFitness = alg.computeGenerationQuality(alg.getCurrentCriterion()).get(0);
        return newFitness - oldFitness;
    }

    @Override
    public String getName() {
        return "FitnessDiffReward";
    }
}
