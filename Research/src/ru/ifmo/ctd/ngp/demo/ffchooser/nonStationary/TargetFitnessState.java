package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary;

import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OptAlgEnvironment;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.StateCalculator;

import java.util.List;

/**
 * @author Irene Petrova
 * calcu
 */
public class TargetFitnessState implements StateCalculator<String, Integer> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(OptAlgEnvironment<String, Integer> environment) {
        List<Double> lastValues = environment.getAlgorithm().getCurrentBest();
        int target = environment.getAlgorithm().getTargetParameter();
        double diff = lastValues.get(target) - environment.getPrevValues().get(target);

        int res = 0;
        if (diff > 0) {
            res = 1;
        } else if (diff < 0) {
            res = -1;
        }
        return String.valueOf(res);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "targFitr";
    }

}
