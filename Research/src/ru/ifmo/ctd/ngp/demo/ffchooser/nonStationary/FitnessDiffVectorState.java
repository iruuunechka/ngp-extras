package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary;

import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OptAlgEnvironment;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.StateCalculator;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDef;

import java.util.List;

/**
 * @author Irene Petrova
 */
public class FitnessDiffVectorState implements StateCalculator<String, Integer> {

    private final int length;

    /**
     * Constructs {@link FitnessDiffVectorState} with the specified length of the vector.
     * The length should be equal or less than the number of fitness evaluators.
     * If it is less, first <code>length</code> FFs will be taken after sorting by
     * <code>(x<sub>current</sub> - x<sub>previous</sub>) / x<sub>current</sub></code>.
     * @param length the specified length of the vector
     */
    public FitnessDiffVectorState(@ParamDef(name = "length") int length) {
        this.length = length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(OptAlgEnvironment<String, Integer> environment) {
        List<Double> lastValues = environment.getAlgorithm().getCurrentBest();

        if (length > lastValues.size()) {
            throw new IllegalArgumentException("Length of the vector doesn't fit the number of fitness evaluators.");
        }

        StringBuilder rv = new StringBuilder();

        for (int i = 0; i < length; i++) {
            double diff = lastValues.get(i) - environment.getPrevValues().get(i);
            if (diff == 0.0) {
                rv.append(0);
            } else if (diff < 10) {
                rv.append(1);
            } else {
                rv.append(2);
            }

        }
        return rv.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return String.format("fitDiffVector%d", length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + length;
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FitnessDiffVectorState other = (FitnessDiffVectorState) obj;
        return length == other.length;
    }
}
