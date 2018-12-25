package ru.ifmo.ctd.ngp.learning.reinforce.q;

import ru.ifmo.ctd.ngp.learning.reinforce.AgentPrinter;
import ru.ifmo.ctd.ngp.learning.reinforce.Environment;

/**
 * Printer that prints information about {@link ClassicQAgent} to
 * the console.
 * 
 * @author Arina Buzdalova
 * @param <S> the type of a state
 * @param <A> the type of an action
 */
public class ClassicConsolePrinter<S, A> implements AgentPrinter<S, A, ClassicQAgent<S, A>> {
	private static final ClassicConsolePrinter<?, ?> instance = new ClassicConsolePrinter<>();
	private ClassicConsolePrinter() {}
	
	/**
	 * Gets the instance of this printer.
	 * @return the instance of this printer
	 */
	@SuppressWarnings("unchecked")
    public static<S, A> ClassicConsolePrinter<S, A> getInstance() {
		return (ClassicConsolePrinter<S, A>) instance;
	}
	
	/**
	 * Adds this printer to the specified agent.
	 * @param agent the specified agent
	 */
	@SuppressWarnings("unchecked")
    public static <S, A> void addTo(ClassicQAgent<S, A> agent) {
		agent.addPrinter((ClassicConsolePrinter<S, A>) instance);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(ClassicQAgent<S, A> agent, Environment<? extends S, ? extends A> environment) {
		System.out.println("step " + agent.getSteps());
		System.out.println("action taken: " + agent.getLastAction());
		System.out.println("state received: " + environment.getCurrentState());
		System.out.println("Q:");
		System.out.println(agent.getQ());
	}

}
