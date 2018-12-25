package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.state;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MultiOptAlgEnvironment;

/**
 * @author Irene Petrova
 */
public class SingleState implements MultiStateCalculator<String, Integer> {

    private static final long serialVersionUID = -8286528404794273631L;

    @Override
    public String calculate(MultiOptAlgEnvironment<String, Integer> environment) {
        return "s";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
            return "single";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
            return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
            return this == obj || obj != null && getClass() == obj.getClass();
    }
}

