package ru.ifmo.ctd.ngp.learning.reinforce;


/**
 * The agent which learns to optimally influence the {@link Environment}.
 * It influences the environment by performing actions over it and
 * gets the rewards for these actions. 
 * The goal is to maximize the total reward.
 * 
 * @author Arina Buzdalova
 * @param <S> type of a state
 * @param <A> type of an action
 */
public interface Agent<S, A> {
	
	/**
	 * <p>
	 * Learns this agent by performing actions over the specified {@link Environment}.
	 * Agent stops then the environment is in the termination state.
	 * </p><p>
	 * The effectiveness of decisions increases with time.
	 * It is measured by the total reward got from the environment.
	 * The reward depends on the action performed and on the current state of the environment.
	 * </p>
	 * @param environment the specified environment
	 * @return number of steps performed by the agent
	 */
	int learn(Environment<S, A> environment);
	
	/**
	 * Refreshes this agent, so it forgets about the previous experience.
	 */
	void refresh();

    @SuppressWarnings("UnusedReturnValue") // actually used in ru.ifmo.ctd.ngp.opt.misc.earl.EARLConfiguration
	Agent<S, A> makeClone();
}
