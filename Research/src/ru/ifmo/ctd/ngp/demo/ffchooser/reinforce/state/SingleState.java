package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state;

import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OptAlgEnvironment;

/**
 * {@link StateCalculator} that always returns the same state
 * 
 * @author Arina Buzdalova
 */
public class SingleState implements StateCalculator<String, Integer> {

	private static final long serialVersionUID = 814033758419171734L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String calculate(OptAlgEnvironment<String, Integer> environment) {
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
