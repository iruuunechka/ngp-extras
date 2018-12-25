package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state;

import java.io.Serializable;

import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OptAlgEnvironment;

/**
 * Interface for a thing that calculates an state
 * basing on the performance history of an optimization algorithm environment.
 * 
 * @author Arina Buzdalova
 *
 * @param <S> the type of a state
 * @param <A> the type of an action
 */
public interface StateCalculator<S, A> extends Serializable {
	/**
	 * Calculates the state based on the performance history 
	 * of the specified optimization algorithm environment
	 * @param environment the specified environment
	 * @return the state
	 */
    S calculate(OptAlgEnvironment<S, A> environment);
	
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
