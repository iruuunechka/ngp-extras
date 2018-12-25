package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.state;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MultiOptAlgEnvironment;

/**
 * @author Irene Petrova
 */
public class TimeIntervalState implements MultiStateCalculator<String, Integer> {
    final double maxIter;
    final double logBase;
    final double stateCount;

    public TimeIntervalState(double maxIter, double stateCount) {
        this.logBase = Math.pow(maxIter, 1 / stateCount);
        this.maxIter = maxIter;
        this.stateCount = stateCount;
    }

    @Override
    public String calculate(MultiOptAlgEnvironment<String, Integer> environment) {
        double curIter = environment.getAlgorithm().getIterationsNumber();
        return String.valueOf(Math.ceil(Math.log(maxIter - curIter) / Math.log(logBase)));
    }

    @Override
    public String getName() {
        return "TimeIntervalState" + stateCount;
    }
}

