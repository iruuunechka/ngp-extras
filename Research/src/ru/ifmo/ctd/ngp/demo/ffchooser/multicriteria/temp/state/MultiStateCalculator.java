package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.state;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.*;

import java.io.*;

/**
 * Interface for a thing that calculates an state
 * basing on the performance history of an optimization algorithm environment.
 *
 * @param <S> the type of a state
 * @param <A> the type of an action
 */
public interface MultiStateCalculator<S, A> extends Serializable {

    /**
     * Calculates the state based on the performance history
     * of the specified optimization algorithm environment
     * @param environment the specified environment
     * @return the state
     */
    S calculate(MultiOptAlgEnvironment<S, A> environment);

    /**
     * Returns short mnemonic string representation of this state calculator.
     * Names of calculators can be equal only if they are equal themself.
     * @return short mnemonic string representation of this state calculator
     */
    String getName();

    /**
     * {@inheritDoc}
     */
    @Override
    boolean equals(Object obj);

    /**
     * {@inheritDoc}
     */
    @Override
    int hashCode();
}
