package ru.ifmo.ctd.ngp.learning.reinforce.q;

import ru.ifmo.ctd.ngp.learning.reinforce.*;
import ru.ifmo.ctd.ngp.learning.util.Maps;

import java.util.Random;

/**
 * <p>
 * {@link ClassicQAgent}, which explore the {@link Environment} using
 * epsilon-greedy strategy. 
 * </p><p>
 * This means that the action with the maximal Q-value 
 * is chosen with the probability 1 - epsilon.
 * Otherwise, any random action is chosen. 
 * </p><p>
 * Epsilon decreases with time.
 * </p>
 * @author Arina Buzdalova
 * @param <S> type of a state
 * @param <A> type of an action
 */
public class EGreedyAgent<S, A> extends ClassicQAgent<S, A> {
	
	private double eps;
	private final double rate;
	private final Random rand;
	
	/**
	 * Constructs agent, which learns using Q-learning algorithm
	 * 
	 * @param epsilon the probability of choosing random action in order to explore the environment
	 * @param rate the rate of epsilon decrease
	 * @param alpha the speed of learning
	 * @param gamma the discount factor
	 */
	public EGreedyAgent(double epsilon, double rate, double alpha, double gamma) {
		super(alpha, gamma);
		this.eps = epsilon;
		this.rate = rate;
		this.rand = new Random();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected A chooseAction(S state) {			
		eps /= rate;		
		if (rand.nextDouble() < eps) {
			return actions.get(rand.nextInt(actions.size()));
		} else {
			return Maps.argMax(Q, state, actions);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "greedy-eps" + eps + "alpha" + alpha + "gamma" + gamma;
	}

    @Override
    public Agent<S, A> makeClone() {
        return new EGreedyAgent<>(eps, rate, alpha, gamma);
    }
}
