package ru.ifmo.ctd.ngp.learning.reinforce.q;

import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.util.Map2;
import ru.ifmo.ctd.ngp.learning.util.Maps;

/**
 * Basic implementation of the {@link Agent} which uses classic Q-learning algorithm.
 * It should be extended by classes realizing their own environment exploration strategies. 
 * Exploration strategy solves the "exploration vs exploitation" problem.
 *  
 * @author Arina Buzdalova
 * @param <S> type of a state
 * @param <A> type of an action
 */
public abstract class ClassicQAgent<S, A> extends QAgent<S, A, ClassicQAgent<S, A>> {
	/**
	 * learning speed
	 */
	protected final double alpha;
	/**
	 * discount factor
	 */
	protected final double gamma;
	
	/**
	 * Constructs agent, which learns using Q-learning algorithm.
	 * @param alpha parameter of the Q-learning algorithm
	 * @param gamma parameter of the Q-learning algorithm
	 */
	public ClassicQAgent(double alpha, double gamma) {
		super();
		this.Q = new Map2<>(0.0);
		this.alpha = alpha;
		this.gamma = gamma;
	}
	
	/**
     * Makes step of the Q-learning by applying the specified
     * action to the environment and recalculating the corresponding Q-value
     * @param action the specified action
     * @return the reward got from the environment after applying the {@code action}
     */
	protected double makeStep(A action) {		
		
		S state = environment.getCurrentState();
		double reward = environment.applyAction(action);
		S newState = environment.getCurrentState();
	
		double old = Q.get(state, action);
		Q.put(state, action, old + alpha * (reward +
                gamma * Maps.max(Q, newState, environment.getActions()) - old));
		
		return reward;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		Q = new Map2<>(0.0);
	}
	
	@Override
	protected ClassicQAgent<S, A> self() {
		return this;
	}
}
