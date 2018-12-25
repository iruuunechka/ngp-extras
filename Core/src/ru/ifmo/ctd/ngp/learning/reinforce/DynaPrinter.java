package ru.ifmo.ctd.ngp.learning.reinforce;

import ru.ifmo.ctd.ngp.learning.util.Maps;
import ru.ifmo.ctd.ngp.learning.util.PrintFormatter;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Printer that prints information about {@link DynaAgent}.
 * 
 * @author Arina Buzdalova
 * 
 * @param <S> the type of a state
 * @param <A> the type of an action
 */
public class DynaPrinter<S, A> implements AgentPrinter<S, A, DynaAgent<S, A>> {
	private final Writer writer;
	
	/**
	 * Constructs the {@link DynaPrinter} with the specified writer
	 * @param writer the specified writer
	 */
	private DynaPrinter(Writer writer) {
		this.writer = writer;
	}
	
	/**
	 * Constructs the printer with the specified writer
	 * and adds it to the specified agent.
	 * @param agent the agent to which the created printer will be subscribed
	 * @param writer the specified writer
	 */
	public static<S, A> void addTo(DynaAgent<S, A> agent, Writer writer) {
		agent.addPrinter(new DynaPrinter<>(writer));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(DynaAgent<S, A> agent, Environment<? extends S, ? extends A> environment) {
		try {
            List<? extends A> actions = environment.getActions();
			writer.append(String.format("\nQ: \n%s", Maps.format(agent.getQ(), actions, DOUBLE_0p3)));
			writer.append(String.format("R: \n%s", Maps.format(agent.getR(), actions, DOUBLE_0p3)));
			writer.append(String.format("T: \n%s", Maps.format(agent.getT(), actions, DOUBLE_0p3)));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


    private static final PrintFormatter<Double> DOUBLE_0p3 = value -> String.format("%.3f ", value);
}
