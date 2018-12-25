package ru.ifmo.ctd.ngp.learning.reinforce;

/**
 * Interface for entities that print information about the {@link Agent}'s
 * performance.
 * 
 * @author Arina Buzdalova
 * @param <S> the type of a state
 * @param <A> the type of an action
 * @param <T> the type of an agent
 */
public interface AgentPrinter<S, A, T extends Agent<S, A>> {

	/**
	 * Prints information about the specified {@link Agent}
     * @param agent the agent to be observed
     * @param environment the environment on which the agent is working.
     */
	void print(T agent, Environment<? extends S, ? extends A> environment);
	
}
