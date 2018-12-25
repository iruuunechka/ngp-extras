package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.state;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MultiOptAlgEnvironment;

/**
 * @author Irene Petrova
 */
public class FitnessIntervalState implements MultiStateCalculator<String, Integer> {
    final double maxFitness;
    final double logBase;
    final double stateCount;

    public FitnessIntervalState(double optimalFitness, double max, double stateCount) {
        this.logBase = Math.ceil(Math.pow(optimalFitness * 0.75, 1 / stateCount));
        this.maxFitness = max - optimalFitness;
        this.stateCount = stateCount;
    }

    @Override
    public String calculate(MultiOptAlgEnvironment<String, Integer> environment) {
        double targRes = environment.getAlgorithm().getCurrentBest().get(0);
        return String.valueOf(Math.ceil(Math.log(maxFitness - targRes) / Math.log(logBase)));
    }

    @Override
    public String getName() {
        return "FitnessIntervalState" + stateCount;
    }
}
