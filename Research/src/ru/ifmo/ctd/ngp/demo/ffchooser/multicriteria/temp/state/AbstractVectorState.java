package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.state;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.utils.*;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.*;

import java.util.*;

/**
 * @author Irene Petrova
 */
public abstract class AbstractVectorState implements MultiStateCalculator<String, Integer> {

    private static final long serialVersionUID = 3272575296320071612L;
    protected final int length;
    protected final List<Double> prevValues;

    protected AbstractVectorState(@ParamDef(name = "length") int length, MulticriteriaOptimizationAlgorithm alg) {
        prevValues = getLastValues(alg);
        this.length = length;
    }

    /**
     * Get list of individuals based on
     * {@link #getIndividualSet(ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MulticriteriaOptimizationAlgorithm) getIndividualSet}
     * i.e. individuals with maximal target value.
     */
    protected abstract List<Double> getLastValues(MulticriteriaOptimizationAlgorithm alg);

    /**
     * Get list of current individuals i.e. pareto front or generation.
     */
    protected abstract List<List<Double>> getIndividualSet(MulticriteriaOptimizationAlgorithm alg);

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(MultiOptAlgEnvironment<String, Integer> environment) {
        List<Double> lastValues = getLastValues(environment.getAlgorithm());
        List<Integer> actions = environment.getActions();

        if (length > lastValues.size()) {
            throw new IllegalArgumentException("Length of the vector doesn't fit the number of fitness evaluators.");
        }

        StringBuilder rv = new StringBuilder();
        ArrayList<ObjectWithValue<Integer>> list = new ArrayList<>();
        for (int i : actions) {
            list.add(new ObjectWithValue<>((lastValues.get(i) - prevValues.get(i)) / lastValues.get(i), i));
        }
        Collections.sort(list);

        for (int i = 0; i < length; i++) {
            rv.append(list.get(i).getObject());
        }
        return rv.toString();
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
        AbstractVectorState other = (AbstractVectorState) obj;
        return length == other.length;
    }
}
