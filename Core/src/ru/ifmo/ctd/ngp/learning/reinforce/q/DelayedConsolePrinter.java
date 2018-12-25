package ru.ifmo.ctd.ngp.learning.reinforce.q;

import ru.ifmo.ctd.ngp.learning.reinforce.AgentPrinter;
import ru.ifmo.ctd.ngp.learning.reinforce.Environment;

/**
 * {@link AgentPrinter} that prints information 
 * about {@link DelayedAgent} to the console.
 *  
 * @author Arina Buzdalova
 * 
 * @param <S> the type of a state
 * @param <A> the type of an action
 */
public class DelayedConsolePrinter<S, A> implements AgentPrinter<S, A, DelayedAgent<S, A>> {
	private static final DelayedConsolePrinter<?, ?> instance = new DelayedConsolePrinter<>();
	private DelayedConsolePrinter() {}
	
	/**
	 * Gets the instance of this printer
	 * @return the instance of this printer
	 */
	@SuppressWarnings("unchecked")
    public static<S, A> DelayedConsolePrinter<S, A> getInstance() {
		return (DelayedConsolePrinter<S, A>) instance;
	}
	
	@SuppressWarnings("unchecked")
    public static <S, A> void addTo(DelayedAgent<S, A> agent) {
		agent.addPrinter((DelayedConsolePrinter<S, A>) instance);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(DelayedAgent<S, A> agent, Environment<? extends S, ? extends A> environment) {
		System.out.println(agent.getSteps() + ")" + "t =  " + agent.getT());
		System.out.println("action taken: " + agent.getLastAction());
		System.out.println("state received: " + environment.getCurrentState());
		System.out.println("Q:");
		System.out.print(agent.getQ());
		System.out.println("U:");
		System.out.print(agent.getU());
		System.out.println("learn: ");
		System.out.println(agent.getLearn());
	}
	
}