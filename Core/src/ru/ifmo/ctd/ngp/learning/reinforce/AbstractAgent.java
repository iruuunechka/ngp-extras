package ru.ifmo.ctd.ngp.learning.reinforce;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic implementation of the {@link Agent} that provides 
 * servicing the printers.
 * 
 * @author Arina Buzdalova
 *
 * @param <S> the type of a state
 * @param <A> the type of an action
 * @param <T> the type of the agent
 */
public abstract class AbstractAgent<S, A, T extends Agent<S, A>> implements Agent<S, A> {
	private final List<AgentPrinter<? super S, ? super A, ? super T>> printers;
	
	/**
	 * Returns reference to this agent of its concrete type
	 * @return this agent
	 */
	protected abstract T self();
	
	/**
	 * Constructs {@link AbstractAgent} without any parameters. 
	 * Should be called in the subclasses in order to initialize inner fields.
	 */
    protected AbstractAgent() {
		printers = new ArrayList<>();
	}
	
	/**
	 * Subscribes the specified printer to get information
	 * about this agent
	 * @param printer the printer to be subscribed
	 */
	public void addPrinter(AgentPrinter<? super S, ? super A, ? super T> printer) {
		printers.add(printer);
	}

	/**
	 * Sends information about the agent's performance 
	 * to all subscribed printers
     * @param currentEnvironment the current environment.
     */
	protected void updatePrinters(Environment<S, A> currentEnvironment) {
		for (AgentPrinter<? super S, ? super A, ? super T> printer : printers) {
			printer.print(self(), currentEnvironment);
		}
	}
}
