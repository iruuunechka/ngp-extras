package ru.ifmo.ctd.ngp.learning.reinforce.q;

import ru.ifmo.ctd.ngp.learning.reinforce.*;

/**
 * {@link ClassicQAgent}, which uses soft-max exploration.
 * Exploration gives way for exploitation as the "temperature"
 * decreases. The probability of action choice depends on the
 * corresponding Q-value.
 * 
 * @author Arina Buzdalova
 * @param <S> type of a state
 * @param <A> type of an action
 */
public class SoftmaxAgent<S, A> extends ClassicQAgent<S, A> {
	private final SoftmaxActionChooser<S, A> chooser;
    private final double temperature;
    private final double tRate;
	
	/**
	 * Constructs {@link SoftmaxAgent}, which learns using Q-learning algorithm
	 * and Boltzmann exploration. Exploration gives way for exploitation as the 
	 * "temperature" decreases.
	 * 
	 * @param temperature initial temperature value
	 * @param tRate parameter that influences the rate of temperature decrease
	 * @param alpha parameter of the Q-learning algorithm
	 * @param gamma parameter of the Q-learning algorithm
	 */
	public SoftmaxAgent( 
			double temperature, 
			double tRate,
			double alpha, 
			double gamma) {
		super(alpha, gamma);
        this.temperature = temperature;
        this.tRate = tRate;
		chooser = new SoftmaxActionChooser<>(temperature, tRate);
	}

	@Override
	protected A chooseAction(S state) {
		return chooser.chooseAction(state, Q, actions);
	}

    @Override
    public Agent<S, A> makeClone() {
        return new SoftmaxAgent<>(temperature, tRate, alpha, gamma);
    }
}
