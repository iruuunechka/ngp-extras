package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.state;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MultiOptAlgEnvironment;

/**
 * @author Irene Petrova
 */
public class HelperState implements MultiStateCalculator<String, Integer> {

    @Override
    public String calculate(MultiOptAlgEnvironment<String, Integer> environment) {
        return String.valueOf(environment.getAlgorithm().getCurrentCriterion());
    }

    @Override
    public String getName() {
        return "HelperState";
    }
}
