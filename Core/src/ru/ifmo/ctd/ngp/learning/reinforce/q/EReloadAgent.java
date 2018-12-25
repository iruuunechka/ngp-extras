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
 * </p><p>
 * If the target criteria stays unchanged for some specific time,
 * epsilon is reloaded.
 * </p>
 * @author Arina Buzdalova
 * @param <S> type of a state
 * @param <A> type of an action
 */
@SuppressWarnings("UnusedDeclaration")
public class EReloadAgent<S, A> extends ClassicQAgent<S, A> {
	
	private double eps;
	private final double rate;
	private int unchanged;
	private final Random rand;
	private final double eps0;
	
	/**
	 * Constructs agent, which learns using Q-learning algorithm
	 * 
	 * @param epsilon the probability of choosing random action in order to explore the environment
	 * @param rate the rate of epsilon decrease
	 * @param alpha parameter of the Q-learning algorithm
	 * @param gamma parameter of the Q-learning algorithm
	 */
	public EReloadAgent(double epsilon, double rate, double alpha, double gamma) {
		super(alpha, gamma);
		this.eps = epsilon;
		this.eps0 = epsilon;
		this.rate = rate;
		this.rand = new Random();
		this.unchanged = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int learn(Environment<S, A> environment) {
		this.environment = environment;
		this.actions = environment.getActions();
		
		int steps = 0;
		double oldReward = 0;
		
		while(!environment.isInTerminalState()) {
			Double reward = makeStep(chooseAction(environment.getCurrentState()));		
			countUnchanged(reward, oldReward);			
			oldReward = reward;			
			steps++;
		}		
		return steps;
	}

    @Override
    public Agent<S, A> makeClone() {
        return new EReloadAgent<>(eps, rate, alpha, gamma);
    }

    private void countUnchanged(double reward, double oldReward) {
		if (reward - oldReward == 0) {
			unchanged++;
		} else {
			unchanged = 0;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected A chooseAction(S state) {			
		
		if (unchanged >= 5) {
			eps = eps0;
		}
		
		eps /= rate;		
		
		if (Math.random() < eps) {
			return actions.get(rand.nextInt(actions.size()));
		} else {
			return Maps.argMax(Q, state, actions);
		}
	}
}
