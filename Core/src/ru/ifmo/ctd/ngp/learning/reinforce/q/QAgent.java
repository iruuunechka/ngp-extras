package ru.ifmo.ctd.ngp.learning.reinforce.q;

import ru.ifmo.ctd.ngp.learning.reinforce.AbstractAgent;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.Environment;
import ru.ifmo.ctd.ngp.learning.util.Map2;

import java.util.List;

/**
 * <p>
 * Basic implementation of the {@link Agent} which uses 
 * any modification of Q-learning algorithm.
 * </p><p>
 * It should be extended by classes realizing their own 
 * steps and environment exploration strategies.
 * </p> 
 * @author Arina Buzdalova
 * @param <S> type of a state
 * @param <A> type of an action
 * @param <T> type of the agent
 */
public abstract class QAgent<S, A, T extends QAgent<S, A, T>> extends AbstractAgent<S, A, T> {
	protected Environment<S, A> environment;
	protected List<A> actions;
	protected Map2<S, A, Double> Q;
	protected A lastAction;
	protected int steps;
	
	/**
	 * Gets the environment this agent interacts with
	 * @return the environment this agent interacts with
	 */
	public Environment<S, A> getEnvironment() {
		return environment;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int learn(Environment<S, A> environment) {
		prepareLearning(environment);		
		steps = 0;		
		while(!environment.isInTerminalState()) {
			prepareAndMakeStep();				
			steps++;
		}		
		return steps;
	}
	
	private void prepareLearning(Environment<S, A> environment) {
		this.environment = environment;
		this.actions = environment.getActions();
		refresh();
	}
	
	private void prepareAndMakeStep() {
		lastAction = chooseAction(environment.getCurrentState());
        updatePrinters(environment);
		makeStep(lastAction);
	}
	
	/**
     * Makes step of the Q-learning by applying the specified
     * action to the environment and recalculating the corresponding Q-value
     * @param action the specified action
     * @return the reward got from the environment after applying the {@code action}
     */
	protected abstract double makeStep(A action);
	
	/**
	 * Chooses next action to be prepared
	 * @param state the current state of the environment
	 * @return next action to be prepared
	 */
	protected abstract A chooseAction(S state);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void refresh();

	protected Map2<S, A, Double> getQ() {
		return Q;
	}

	protected A getLastAction() {
		return lastAction;
	}

	protected int getSteps() {
		return steps;
	}	
}
